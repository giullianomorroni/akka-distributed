package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkIsDone implements Serializable {
	
	private static final long serialVersionUID = -3565614377623673333L;
	public final String workerId;
    public final String workId;
    public final Object result;

    public WorkIsDone(String workerId, String workId, Object result) {
      this.workerId = workerId;
      this.workId = workId;
      this.result = result;
    }

    @Override
    public String toString() {
      return "WorkIsDone{" +
        "workerId='" + workerId + '\'' +
        ", workId='" + workId + '\'' +
        ", result=" + result +
        '}';
    }

}
