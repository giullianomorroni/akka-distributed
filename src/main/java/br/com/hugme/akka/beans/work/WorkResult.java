package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkResult implements Serializable {

	private static final long serialVersionUID = 8007887863079998345L;
	
	public final String workId;
	public final Object result;

	public WorkResult(String workId, Object result) {
		this.workId = workId;
		this.result = result;
	}

	@Override
	public String toString() {
		return "WorkResult{" + "workId='" + workId + '\'' + ", result=" + result + '}';
	}

}
