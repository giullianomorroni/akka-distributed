package br.com.hugme.akka.beans.work;

import java.io.Serializable;
import java.util.UUID;

public class Work implements Serializable {

	private static final long serialVersionUID = 7000769616989768230L;
	private final Object message;
	private final String id = UUID.randomUUID().toString().substring(0, 10);

	public Work(Object job) {
		this.message = job;
	}

	public Object getMessage() {
		return message;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Work [Message=" + message + "]";
	}

}
