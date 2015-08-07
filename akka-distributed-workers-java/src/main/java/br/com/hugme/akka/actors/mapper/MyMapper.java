package br.com.hugme.akka.actors.mapper;

import br.com.hugme.akka.beans.state.Ack;
import br.com.hugme.akka.beans.state.Ok;

public class MyMapper extends akka.dispatch.Mapper<Object, Object> {

	@Override
	public Object apply(Object msg) {
		if (msg instanceof Ack)
			return Ok.getInstance();
		else
			return super.apply(msg);
	}

}
