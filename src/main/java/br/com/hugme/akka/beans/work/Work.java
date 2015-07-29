package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class Work implements Serializable {
	
	private static final long serialVersionUID = 6585293702154131716L;
	public final String workId;
	public final Object job;

	public Work(String workId, Object job) {
		this.workId = workId;
		this.job = job;
	}

	@Override
	public String toString() {
		return "Work{" + "workId='" + workId + '\'' + ", job=" + job + '}';
	}

}
