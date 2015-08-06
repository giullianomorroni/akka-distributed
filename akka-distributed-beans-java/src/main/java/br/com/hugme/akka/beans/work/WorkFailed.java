package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkFailed implements Serializable {
	private static final long serialVersionUID = -8923516078946897493L;
	public final String workerId;
    public final String workId;

    public WorkFailed(String workerId, String workId) {
      this.workerId = workerId;
      this.workId = workId;
    }

    @Override
    public String toString() {
      return "WorkFailed{" +
        "workerId='" + workerId + '\'' +
        ", workId='" + workId + '\'' +
        '}';
    }

}
