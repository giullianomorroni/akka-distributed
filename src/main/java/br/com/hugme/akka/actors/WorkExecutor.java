package br.com.hugme.akka.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import br.com.hugme.akka.beans.work.WorkComplete;

public class WorkExecutor extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) {
		if (message instanceof Integer) {
			Integer n = (Integer) message;
			int n2 = n.intValue() * n.intValue();
			String result = n + " * " + n + " = " + n2;
			log.info("Produced result {}", result);
			getSender().tell(new WorkComplete(result), getSelf());
		}
	}
}
