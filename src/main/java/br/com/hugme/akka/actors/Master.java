package br.com.hugme.akka.actors;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.contrib.pattern.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.UntypedPersistentActor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.hugme.akka.beans.state.Ack;
import br.com.hugme.akka.beans.state.Busy;
import br.com.hugme.akka.beans.state.Idle;
import br.com.hugme.akka.beans.work.Work;
import br.com.hugme.akka.beans.work.WorkAccepted;
import br.com.hugme.akka.beans.work.WorkCompleted;
import br.com.hugme.akka.beans.work.WorkDomainEvent;
import br.com.hugme.akka.beans.work.WorkFailed;
import br.com.hugme.akka.beans.work.WorkIsDone;
import br.com.hugme.akka.beans.work.WorkIsReady;
import br.com.hugme.akka.beans.work.WorkResult;
import br.com.hugme.akka.beans.work.WorkStarted;
import br.com.hugme.akka.beans.work.WorkState;
import br.com.hugme.akka.beans.worker.RegisterWorker;
import br.com.hugme.akka.beans.worker.WorkerFailed;
import br.com.hugme.akka.beans.worker.WorkerRequestsWork;
import br.com.hugme.akka.beans.worker.WorkerState;
import br.com.hugme.akka.beans.worker.WorkerTimedOut;
import scala.collection.JavaConversions;
import scala.concurrent.duration.FiniteDuration;

public class Master extends UntypedPersistentActor {

	private final FiniteDuration workTimeout;
	private final ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final Cancellable cleanupTask;
	private HashMap<String, WorkerState> workers = new HashMap<String, WorkerState>();
	private WorkState workState = new WorkState();
	public static String ResultsTopic = "results";

	public static Props props(FiniteDuration workTimeout) {
		return Props.create(Master.class, workTimeout);
	}

	public Master(FiniteDuration workTimeout) {
		ClusterReceptionistExtension.get(getContext().system()).registerService(getSelf());
		this.cleanupTask = getContext().system().scheduler().schedule(FiniteDuration.Zero(), FiniteDuration.create(20, "seconds"), getSelf(), CleanupTick, getContext().dispatcher(), getSelf());
		this.workTimeout = workTimeout;
	}

	@Override
	public void postStop() {
		cleanupTask.cancel();
	}

	private void notifyWorkers() {
		if (workState.hasWork()) {
			for (WorkerState state : workers.values()) {
				if (state.status.isIdle()) {
					state.ref.tell(WorkIsReady.getInstance(), getSelf());
				}
			}
		}
	}

	public static final Object CleanupTick = new Object() {
		@Override
		public String toString() {
			return "CleanupTick";
		}
	};

	@Override
	public void onReceiveRecover(Object arg0) throws Exception {
		if (arg0 instanceof WorkDomainEvent) {
			workState = workState.updated((WorkDomainEvent) arg0);
			log.info("Master Answering with {}", arg0.getClass().getSimpleName());
		}
	}

	public String persistenceId() {
		for (String role : JavaConversions.asJavaIterable((Cluster.get(getContext().system()).selfRoles()))) {
			if (role.startsWith("backend-")) {
				return role + "-master";
			}
		}
		return "master";
	}

	@Override
	public void onReceiveCommand(Object cmd) throws Exception {
		if (cmd instanceof RegisterWorker) {
			String workerId = ((RegisterWorker) cmd).workerId;
			if (workers.containsKey(workerId)) {
				workers.put(workerId, workers.get(workerId).copyWithRef(getSender()));
			} else {
				log.info("Worker registered: {}", workerId);
				workers.put(workerId, new WorkerState(getSender(), Idle.getInstance()));
				if (workState.hasWork()) {
					getSender().tell(WorkIsReady.getInstance(), getSelf());
				}
			}
		} else if (cmd instanceof WorkerRequestsWork) {
			if (workState.hasWork()) {
				final String workerId = ((WorkerRequestsWork) cmd).workerId;
				final WorkerState state = workers.get(workerId);
				if (state != null && state.status.isIdle()) {
					final Work work = workState.nextWork();
					persist(new WorkStarted(work.getId()), new Procedure<WorkStarted>() {
						public void apply(WorkStarted event) throws Exception {
							workState = workState.updated(event);
							log.info("Giving worker {} some work {}", workerId, event.workId);
							workers.put(workerId, state.copyWithStatus(new Busy(event.workId, workTimeout.fromNow())));
							getSender().tell(work, getSelf());

						}
					});
				}
			}
		} else if (cmd instanceof WorkIsDone) {
			final String workerId = ((WorkIsDone) cmd).workerId;
			final String workId = ((WorkIsDone) cmd).workId;
			if (workState.isDone(workId)) {
				getSender().tell(new Ack(workId), getSelf());
			} else if (!workState.isInProgress(workId)) {
				log.info("Work {} not in progress, reported as done by worker {}", workId, workerId);
			} else {
				log.info("Work {} is done by worker {}", workId, workerId);
				changeWorkerToIdle(workerId, workId);
				persist(new WorkCompleted(workId, ((WorkIsDone) cmd).result),
						new Procedure<WorkCompleted>() {
							public void apply(WorkCompleted event) throws Exception {
								workState = workState.updated(event);
								mediator.tell(new DistributedPubSubMediator.Publish(ResultsTopic,
										new WorkResult(event.workId, event.result)), getSelf());
								getSender().tell(new Ack(event.workId), getSelf());
							}
						});
			}
		} else if (cmd instanceof WorkFailed) {
			final String workId = ((WorkFailed) cmd).workId;
			final String workerId = ((WorkFailed) cmd).workerId;
			if (workState.isInProgress(workId)) {
				log.info("Work {} failed by worker {}", workId, workerId);
				changeWorkerToIdle(workerId, workId);
				persist(new WorkerFailed(workId), new Procedure<WorkerFailed>() {
					public void apply(WorkerFailed event) throws Exception {
						workState = workState.updated(event);
						notifyWorkers();
					}
				});
			}
		} else if (cmd instanceof Work) {
			final String workId = ((Work) cmd).getId();
			// idempotent
			if (workState.isAccepted(workId)) {
				getSender().tell(new Ack(workId), getSelf());
			} else {
				log.info("Accepted work: {}", workId);
				persist(new WorkAccepted((Work) cmd), new Procedure<WorkAccepted>() {
					public void apply(WorkAccepted event) throws Exception {
						// Ack back to original sender
						getSender().tell(new Ack(event.work.getId()), getSelf());
						workState = workState.updated(event);
						notifyWorkers();
					}
				});
			}
		} else if (cmd == CleanupTick) {
			cleanUpWorks();
		} else {
			unhandled(cmd);
		}
	}

	/**
	 * 
	 */
	private void cleanUpWorks() {
		Iterator<Map.Entry<String, WorkerState>> iterator = workers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, WorkerState> entry = iterator.next();
			String workerId = entry.getKey();
			WorkerState state = entry.getValue();
			if (state.status.isBusy()) {
				if (state.status.getDeadLine().isOverdue()) {
					log.info("Work timed out: {}", state.status.getWorkId());
					workers.remove(workerId);
					persist(new WorkerTimedOut(state.status.getWorkId()),
							new Procedure<WorkerTimedOut>() {
								public void apply(WorkerTimedOut event) throws Exception {
									workState = workState.updated(event);
									notifyWorkers();
								}
							});
				}
			}
		}
	}

	private void changeWorkerToIdle(String workerId, String workId) {
		if (workers.get(workerId).status.isBusy()) {
			workers.put(workerId, workers.get(workerId).copyWithStatus(new Idle()));
		}
	}

	@Override
	public void unhandled(Object ojb) {
		log.info("Master unhandled job: {}", ojb);
		super.unhandled(ojb);
	}

}
