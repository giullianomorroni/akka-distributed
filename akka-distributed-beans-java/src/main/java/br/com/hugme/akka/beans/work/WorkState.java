package br.com.hugme.akka.beans.work;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import br.com.hugme.akka.beans.worker.WorkerFailed;
import br.com.hugme.akka.beans.worker.WorkerTimedOut;

public final class WorkState {

	private final Map<String, Work> workInProgress;
	private final Set<String> acceptedWorkIds;
	private final Set<String> doneWorkIds;
	private final ConcurrentLinkedDeque<Work> pendingWork;

	public WorkState() {
		this.workInProgress = new HashMap<String, Work>();
		this.pendingWork = new ConcurrentLinkedDeque<Work>();
		this.acceptedWorkIds = new HashSet<String>();
		this.doneWorkIds = new HashSet<String>();
	}

	public WorkState updated(WorkDomainEvent event) {
		WorkState newState = null;
		//success
		if (event instanceof WorkAccepted) {
			return new WorkState(this, (WorkAccepted) event);
		} else if (event instanceof WorkStarted) {
			return new WorkState(this, (WorkStarted) event);
		} else if (event instanceof WorkCompleted) {
			return new WorkState(this, (WorkCompleted) event);
		}

		//failed
		if (event instanceof WorkerFailed) {
			return new WorkState(this, (WorkerFailed) event);
		} else if (event instanceof WorkerTimedOut) {
			return new WorkState(this, (WorkerTimedOut) event);
		}

		return newState;
	}

	private WorkState(WorkState workState, WorkAccepted workAccepted) {
		ConcurrentLinkedDeque<Work> tmp_pendingWork = new ConcurrentLinkedDeque<Work>(workState.pendingWork);
		Set<String> tmp_acceptedWorkIds = new HashSet<String>(workState.acceptedWorkIds);
		tmp_pendingWork.addLast(workAccepted.work);
		tmp_acceptedWorkIds.add(workAccepted.work.getId());
		workInProgress = new HashMap<String, Work>(workState.workInProgress);
		acceptedWorkIds = tmp_acceptedWorkIds;
		doneWorkIds = new HashSet<String>(workState.doneWorkIds);
		pendingWork = tmp_pendingWork;
	}

	public WorkState(WorkState workState, WorkStarted workStarted) {
		ConcurrentLinkedDeque<Work> tmp_pendingWork = new ConcurrentLinkedDeque<Work>(workState.pendingWork);
		Map<String, Work> tmp_workInProgress = new HashMap<String, Work>(workState.workInProgress);

		Work work = tmp_pendingWork.removeFirst();
		if (!work.getId().equals(workStarted.workId)) {
			throw new IllegalArgumentException( "WorkStarted expected workId " + work.getId() + "==" + workStarted.workId);
		}
		tmp_workInProgress.put(work.getId(), work);

		workInProgress = tmp_workInProgress;
		acceptedWorkIds = new HashSet<String>(workState.acceptedWorkIds);
		doneWorkIds = new HashSet<String>(workState.doneWorkIds);
		pendingWork = tmp_pendingWork;
	}

	public WorkState(WorkState workState, WorkCompleted workCompleted) {
		Map<String, Work> tmp_workInProgress = new HashMap<String, Work>(workState.workInProgress);
		Set<String> tmp_doneWorkIds = new HashSet<String>(workState.doneWorkIds);
		tmp_workInProgress.remove(workCompleted.workId);
		tmp_doneWorkIds.add(workCompleted.workId);
		workInProgress = tmp_workInProgress;
		acceptedWorkIds = new HashSet<String>(workState.acceptedWorkIds);
		doneWorkIds = tmp_doneWorkIds;
		pendingWork = new ConcurrentLinkedDeque<Work>(workState.pendingWork);
	}

	public WorkState(WorkState workState, WorkerFailed workerFailed) {
		Map<String, Work> tmp_workInProgress = new HashMap<String, Work>(workState.workInProgress);
		ConcurrentLinkedDeque<Work> tmp_pendingWork = new ConcurrentLinkedDeque<Work>(workState.pendingWork);
		tmp_pendingWork.addLast(workState.workInProgress.get(workerFailed.workId));
		tmp_workInProgress.remove(workerFailed.workId);
		workInProgress = tmp_workInProgress;
		acceptedWorkIds = new HashSet<String>(workState.acceptedWorkIds);
		doneWorkIds = new HashSet<String>(workState.doneWorkIds);
		pendingWork = tmp_pendingWork;
	}

	public WorkState(WorkState workState, WorkerTimedOut workerTimedOut) {
		Map<String, Work> tmp_workInProgress = new HashMap<String, Work>(workState.workInProgress);
		ConcurrentLinkedDeque<Work> tmp_pendingWork = new ConcurrentLinkedDeque<Work>(workState.pendingWork);
		tmp_pendingWork.addLast(workState.workInProgress.get(workerTimedOut.workId));
		tmp_workInProgress.remove(workerTimedOut.workId);
		workInProgress = tmp_workInProgress;
		acceptedWorkIds = new HashSet<String>(workState.acceptedWorkIds);
		doneWorkIds = new HashSet<String>(workState.doneWorkIds);
		pendingWork = tmp_pendingWork;
	}

	public String toString() {
		return "" + acceptedWorkIds.size();
	}

	public Work nextWork() {
		return pendingWork.getFirst();
	}

	public boolean hasWork() {
		return !pendingWork.isEmpty();
	}

	public boolean isAccepted(String workId) {
		return acceptedWorkIds.contains(workId);
	}

	public boolean isInProgress(String workId) {
		return workInProgress.containsKey(workId);
	}

	public boolean isDone(String workId) {
		return doneWorkIds.contains(workId);
	}

}
