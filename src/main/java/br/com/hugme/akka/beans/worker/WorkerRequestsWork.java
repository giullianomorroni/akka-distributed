package br.com.hugme.akka.beans.worker;

import java.io.Serializable;

public class WorkerRequestsWork implements Serializable {
	private static final long serialVersionUID = -1831560527729246963L;
	public final String workerId;

    public WorkerRequestsWork(String workerId) {
      this.workerId = workerId;
    }

    @Override
    public String toString() {
      return "WorkerRequestsWork{" +
        "workerId='" + workerId + '\'' +
        '}';
    }

}
