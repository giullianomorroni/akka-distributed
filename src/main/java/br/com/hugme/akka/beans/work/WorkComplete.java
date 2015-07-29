package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkComplete implements Serializable {
	
	private static final long serialVersionUID = 7655384998425741475L;

	public final Object result;

	public WorkComplete(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "WorkComplete{" + "result=" + result + '}';
	}

}
