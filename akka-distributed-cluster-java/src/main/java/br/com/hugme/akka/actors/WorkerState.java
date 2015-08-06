package br.com.hugme.akka.actors;

import java.io.Serializable;

import akka.actor.ActorRef;
import br.com.hugme.akka.beans.worker.WorkerStatus;

public class WorkerState implements Serializable {

	private static final long serialVersionUID = -1823234853391535376L;

	public final ActorRef ref;
	public final WorkerStatus status;

	public WorkerState(ActorRef ref, WorkerStatus status) {
		this.ref = ref;
		this.status = status;
	}

	public WorkerState copyWithRef(ActorRef ref) {
		return new WorkerState(ref, this.status);
	}

	public WorkerState copyWithStatus(WorkerStatus status) {
		return new WorkerState(this.ref, status);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !getClass().equals(o.getClass()))
			return false;

		WorkerState that = (WorkerState) o;

		if (!ref.equals(that.ref))
			return false;
		if (!status.equals(that.status))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = ref.hashCode();
		result = 31 * result + status.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "WorkerState{" + "ref=" + ref + ", status=" + status + '}';
	}

}
