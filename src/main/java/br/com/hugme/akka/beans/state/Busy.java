package br.com.hugme.akka.beans.state;

import br.com.hugme.akka.beans.worker.WorkerStatus;
import scala.concurrent.duration.Deadline;

public class Busy extends WorkerStatus {

	private final String workId;
	private final Deadline deadline;

	public Busy(String workId, Deadline deadline) {
		this.workId = workId;
		this.deadline = deadline;
	}

	@Override
	public boolean isIdle() {
		return false;
	}

	@Override
	public String getWorkId() {
		return workId;
	}

	@Override
	public Deadline getDeadLine() {
		return deadline;
	}

	@Override
	public String toString() {
		return "Busy{" + "work=" + workId + ", deadline=" + deadline + '}';
	}

}
