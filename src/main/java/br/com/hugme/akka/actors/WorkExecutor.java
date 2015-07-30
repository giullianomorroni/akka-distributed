package br.com.hugme.akka.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import br.com.hugme.akka.beans.work.WorkComplete;

public class WorkExecutor extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) {
		log.info("Produced result {}", message);
		getSender().tell(new WorkComplete(message), getSelf());
	}
	
	@Override
	public void unhandled(Object message) {
		log.info("Produced unhandled result {}", message);
	}
	
}
