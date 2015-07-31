package br.com.hugme.akka;

import akka.actor.*;
import akka.contrib.pattern.*;
import br.com.hugme.akka.actors.Master;
import br.com.hugme.akka.actors.SocialMonitorWorker;
import br.com.hugme.akka.actors.WorkExecutor;
import br.com.hugme.akka.actors.Worker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.HashSet;
import java.util.Set;

public class Main {

	private static FiniteDuration workTimeout = Duration.create(10, "seconds");

	public static void main(String[] args) throws InterruptedException {
		startMasters(2551, "backend");
		startMasters(2552, "backend");
		Thread.sleep(2000);

		startWorkers(5);
		Thread.sleep(5000);

		startMonitors();
		Thread.sleep(5000);
	}

	public static void startMasters(int port, String role) {
		Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
				withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
				withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem", conf);
		system.actorOf(ClusterSingletonManager.defaultProps(Master.props(workTimeout), "active", PoisonPill.getInstance(), role), "master");
	}

	public static void startWorkers(int instances) {
		for(int i=0;i<instances; i++) {
			Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0").withFallback(ConfigFactory.load("worker"));
			ActorSystem system = ActorSystem.create("WorkerSystem", conf);
			Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
			for (String contactAddress : conf.getStringList("contact-points")) {
			  initialContacts.add(system.actorSelection(contactAddress + "/user/receptionist"));
			}
			final ActorRef clusterClient = system.actorOf(ClusterClient.defaultProps(initialContacts), "clusterClient");
			system.actorOf(Worker.props(clusterClient, Props.create(WorkExecutor.class)));
		}
	}

	public static void startMonitors() {
		Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=0").withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ClusterSystem", conf);
		system.actorOf(Props.create(SocialMonitorWorker.class));
	}

}
