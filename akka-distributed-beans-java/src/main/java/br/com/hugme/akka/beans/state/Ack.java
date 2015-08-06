package br.com.hugme.akka.beans.state;

import java.io.Serializable;

public class Ack implements Serializable {
	
	private static final long serialVersionUID = 7102732979346210378L;

	public final String workId;

	public Ack(String workId) {
		this.workId = workId;
	}

	@Override
	public String toString() {
		return "Ack{" + "workId='" + workId + '\'' + '}';
	}

}
