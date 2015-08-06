package br.com.hugme.akka.beans.state;

import java.io.Serializable;

public class Ok implements Serializable {

	private static final long serialVersionUID = 3702664409051589799L;

	private Ok() {
	}

	private static final Ok instance = new Ok();

	public static Ok getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "Ok";
	}

}
