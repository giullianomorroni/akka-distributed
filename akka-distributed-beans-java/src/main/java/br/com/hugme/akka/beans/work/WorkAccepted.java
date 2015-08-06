package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkAccepted implements WorkDomainEvent, Serializable {

	private static final long serialVersionUID = -8744410949825361838L;

	public final Work work;

	public WorkAccepted(Work work) {
		this.work = work;
	}

}
