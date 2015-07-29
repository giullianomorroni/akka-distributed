package br.com.hugme.akka.beans.state;

import br.com.hugme.akka.beans.worker.WorkerStatus;
import scala.concurrent.duration.Deadline;

public class Idle extends WorkerStatus {
	
	private static final Idle instance = new Idle();

	public static Idle getInstance() {
		return instance;
	}

	public boolean isIdle() {
		return true;
	}

	public String getWorkId() {
		throw new IllegalAccessError();
	}

	public Deadline getDeadLine() {
		throw new IllegalAccessError();
	}

	public String toString() {
		return "Idle";
	}

}
