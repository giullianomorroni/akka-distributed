package br.com.hugme.akka.actors;

import java.util.UUID;

import akka.actor.*;
import akka.contrib.pattern.ClusterClient.SendToAll;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import br.com.hugme.akka.beans.state.Ack;
import br.com.hugme.akka.beans.work.Work;
import br.com.hugme.akka.beans.work.WorkComplete;
import br.com.hugme.akka.beans.work.WorkIsDone;
import br.com.hugme.akka.beans.work.WorkIsReady;
import br.com.hugme.akka.beans.worker.RegisterWorker;
import br.com.hugme.akka.beans.worker.WorkerRequestsWork;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Worker extends UntypedActor {

	public static Props props(ActorRef clusterClient, Props workExecutorProps, FiniteDuration registerInterval) {
		return Props.create(Worker.class, clusterClient, workExecutorProps, registerInterval);
	}

	public static Props props(ActorRef clusterClient, Props workExecutorProps) {
		return props(clusterClient, workExecutorProps, Duration.create(10, "seconds"));
	}

	private final ActorRef clusterClient;
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final String workerId = UUID.randomUUID().toString();
	private final ActorRef workExecutor;
	private final Cancellable registerTask;
	private String currentWorkId = null;

	public Worker(ActorRef clusterClient, Props workExecutorProps, FiniteDuration registerInterval) {
		this.clusterClient = clusterClient;
		this.workExecutor = getContext().watch(getContext().actorOf(workExecutorProps, "exec"));
		this.registerTask = getContext().system().scheduler().schedule(
				Duration.Zero(), registerInterval, clusterClient,
				new SendToAll("/user/master/active", 
				new RegisterWorker(workerId)), 
				getContext().dispatcher(), getSelf());
	}

	private String workId() {
		if (currentWorkId != null)
			return currentWorkId;
		else
			throw new IllegalStateException("Not working");
	}

	@Override
	public void postStop() {
		registerTask.cancel();
	}
 
	public void onReceive(Object message) {
		log.info("Worker Got work: {}", message);
		unhandled(message);
	}

	private final Procedure<Object> idle = new Procedure<Object>() {
		public void apply(Object message) {
			if (message instanceof WorkIsReady) {
				sendToMaster(new WorkerRequestsWork(workerId));
			} else if (message instanceof Work) {
				Work work = (Work) message;
				log.info("Got work: {}", work.getMessage());
				currentWorkId = work.getId();
				workExecutor.tell(work, getSelf());
				getContext().become(working);
			} else {
				unhandled(message);
			}
		}
	};

	{
		getContext().become(idle);
	}

	private final Procedure<Object> working = new Procedure<Object>() {
		public void apply(Object message) {
			if (message instanceof WorkComplete) {
				Object result = ((WorkComplete) message).result;
				log.info("Work is complete. Result {}.", result);
				sendToMaster(new WorkIsDone(workerId, workId(), result));
				getContext().setReceiveTimeout(Duration.create(5, "seconds"));
				getContext().become(waitForWorkIsDoneAck(result));
			} else if (message instanceof Work) {
				log.info("Yikes. Master told me to do work, while I'm working.");
			} else {
				unhandled(message);
			}
		}
	};

	private Procedure<Object> waitForWorkIsDoneAck(final Object result) {
		return new Procedure<Object>() {
			public void apply(Object message) {
				if (message instanceof Ack && ((Ack) message).workId.equals(workId())) {
					sendToMaster(new WorkerRequestsWork(workerId));
					getContext().setReceiveTimeout(Duration.Undefined());
					getContext().become(idle);
				} else if (message instanceof ReceiveTimeout) {
					log.info("No ack from master, retrying (" + workerId + " -> " + workId() + ")");
					sendToMaster(new WorkIsDone(workerId, workId(), result));
				} else {
					unhandled(message);
				}
			}
		};
	}

	@Override
	public void unhandled(Object message) {
		if (message instanceof Terminated && ((Terminated) message).getActor().equals(workExecutor)) {
			getContext().stop(getSelf());
		} else if (message instanceof WorkIsReady) {
			// do nothing
		} else {
			super.unhandled(message);
		}
	}

	private void sendToMaster(Object msg) {
		clusterClient.tell(new SendToAll("/user/master/active", msg), getSelf());
	}

}
