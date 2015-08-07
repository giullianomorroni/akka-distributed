package br.com.hugme.akka.beans.work;

import java.io.Serializable;

public class WorkIsReady implements Serializable {

	private static final long serialVersionUID = 8556070344147060388L;

	private static final WorkIsReady instance = new WorkIsReady();

	public static WorkIsReady getInstance() {
		return instance;
	}

}