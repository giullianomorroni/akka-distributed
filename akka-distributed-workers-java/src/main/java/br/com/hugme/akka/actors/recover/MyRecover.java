package br.com.hugme.akka.actors.recover;

import br.com.hugme.akka.beans.state.NotOk;

public class MyRecover extends akka.dispatch.Recover<Object> {

	public Object recover(Throwable failure) throws Throwable {
		return NotOk.getInstance();
	}

}
