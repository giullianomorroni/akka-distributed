package br.com.hugme.akka.beans.worker;

import java.io.Serializable;

import br.com.hugme.akka.beans.work.WorkDomainEvent;

public class WorkerTimedOut implements WorkDomainEvent, Serializable {
	
	private static final long serialVersionUID = 760483990317816563L;

	public final String workId;

	public WorkerTimedOut(String workId) {
		this.workId = workId;
	}

}
