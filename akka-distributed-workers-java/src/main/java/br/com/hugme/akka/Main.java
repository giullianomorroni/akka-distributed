package br.com.hugme.akka;

import akka.actor.*;
import akka.contrib.pattern.*;
import br.com.hugme.akka.actors.SocialMonitorWorker;
import br.com.hugme.akka.actors.WorkExecutor;
import br.com.hugme.akka.actors.Worker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashSet;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		startWorkers(5);
		Thread.sleep(5000);

		startMonitors();
		Thread.sleep(5000);
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
