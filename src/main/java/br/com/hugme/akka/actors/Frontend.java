package br.com.hugme.akka.actors;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.ClusterSingletonProxy;
import akka.dispatch.Mapper;
import akka.dispatch.Recover;
import akka.util.Timeout;
import br.com.hugme.akka.beans.state.Ack;
import br.com.hugme.akka.beans.state.NotOk;
import br.com.hugme.akka.beans.state.Ok;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

public class Frontend extends UntypedActor {

	private ActorRef masterProxy = getContext().actorOf(ClusterSingletonProxy.defaultProps("/user/master/active", "backend"), "masterProxy");

	public void onReceive(Object message) {
		Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
		Future<Object> f = ask(masterProxy, message, timeout);
		final ExecutionContext ec = getContext().system().dispatcher();

		Future<Object> res = f.map(new Mapper<Object, Object>() {
			@Override
			public Object apply(Object msg) {
				if (msg instanceof Ack)
					return Ok.getInstance();
				else
					return super.apply(msg);
			}
		}, ec).recover(new Recover<Object>() {
			@Override
			public Object recover(Throwable failure) throws Throwable {
				return NotOk.getInstance();
			}
		}, ec);
		pipe(res, ec).to(getSender());
	}

}
