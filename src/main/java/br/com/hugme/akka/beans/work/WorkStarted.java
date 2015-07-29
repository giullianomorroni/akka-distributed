package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkStarted implements WorkDomainEvent, Serializable {
	
	private static final long serialVersionUID = 6045005065871205464L;
	public final String workId;

	public WorkStarted(String workId) {
		this.workId = workId;
	}

}
