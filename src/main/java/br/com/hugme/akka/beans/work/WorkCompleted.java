package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkCompleted implements WorkDomainEvent, Serializable {

	private static final long serialVersionUID = 6557536632671050545L;

	public final String workId;
	public final Object result;

	public WorkCompleted(String workId, Object result) {
		this.workId = workId;
		this.result = result;
	}

}
