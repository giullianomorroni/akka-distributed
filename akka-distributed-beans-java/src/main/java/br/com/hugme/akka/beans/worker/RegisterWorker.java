package br.com.hugme.akka.beans.worker;

import java.io.Serializable;

public class RegisterWorker implements Serializable {
	
	private static final long serialVersionUID = -8495822899844342357L;

	public final String workerId;

    public RegisterWorker(String workerId) {
      this.workerId = workerId;
    }

    @Override
    public String toString() {
      return "RegisterWorker{" + "workerId='" + workerId + '\'' + '}';
    }

}
