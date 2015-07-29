package br.com.hugme.akka.beans.state;

import java.io.Serializable;

public class NotOk implements Serializable {

	private static final long serialVersionUID = 1358016458258510682L;

	private NotOk() {
	}

	private static final NotOk instance = new NotOk();

	public static NotOk getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "NotOk";
	}
	
}
