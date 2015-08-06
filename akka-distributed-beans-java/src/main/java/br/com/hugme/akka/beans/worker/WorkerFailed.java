package br.com.hugme.akka.beans.worker;

import java.io.Serializable;

import br.com.hugme.akka.beans.work.WorkDomainEvent;

public class WorkerFailed implements WorkDomainEvent, Serializable {
	
	private static final long serialVersionUID = 8788705499372203081L;

	public final String workId;

	public WorkerFailed(String workId) {
		this.workId = workId;
	}

}
