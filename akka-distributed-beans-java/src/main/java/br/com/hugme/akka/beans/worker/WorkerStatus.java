package br.com.hugme.akka.beans.worker;

import scala.concurrent.duration.Deadline;

public abstract class WorkerStatus {

	public abstract boolean isIdle();

	public boolean isBusy() {
		return !isIdle();
	}

	public abstract String getWorkId();

	public abstract Deadline getDeadLine();
}
