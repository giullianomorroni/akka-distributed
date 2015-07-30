package br.com.hugme.akka.actors;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.contrib.pattern.ClusterSingletonProxy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import br.com.hugme.akka.actors.function.TwitterMonitor;
import br.com.hugme.akka.actors.mapper.MyMapper;
import br.com.hugme.akka.actors.recover.MyRecover;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class SocialMonitorWorker extends UntypedActor {

	private ActorRef masterProxy = getContext().actorOf(ClusterSingletonProxy.defaultProps("/user/master/active", "backend"), "masterProxy");

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private TwitterMonitor runnable;

	public SocialMonitorWorker() {
		super();
	}

	@Override
	public void preStart() {
		ExecutionContext executionContext = getContext().system().dispatcher();
		Scheduler scheduler = getContext().system().scheduler();
		runnable = new TwitterMonitor(this);
//		scheduler.schedule(
//				FiniteDuration.Zero(),
//				FiniteDuration.create(30, "seconds"),
//				runnable, 
//				executionContext);
		scheduler.scheduleOnce(
				FiniteDuration.create(10, "seconds"),
				runnable, 
				executionContext);
	}

	@Override
	public void onReceive(Object message) {
		log.info("Produced work: {}", message);
		ExecutionContext executionContext = getContext().system().dispatcher();
		Timeout timeout = new Timeout(5, TimeUnit.SECONDS);
		Future<Object> f = ask(masterProxy, message, timeout);
		Future<Object> res = f.map(new MyMapper() , executionContext).recover(new MyRecover(), executionContext);
		pipe(res, executionContext).to(getSender());
	}

}
