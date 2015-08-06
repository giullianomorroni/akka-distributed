package br.com.hugme.akka;

import akka.actor.*;
import akka.contrib.pattern.*;
import br.com.hugme.akka.actors.SocialMonitorWorker;
import br.com.hugme.akka.actors.WorkExecutor;
import br.com.hugme.akka.actors.Worker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DistributedWorkersMain {

	public static List<ActorRef> workers = new ArrayList<ActorRef>();
	private static ActorSystem system;
	
	public static void main(String[] args) throws InterruptedException {
		startWorkers(5);
	}

	public static void startWorkers(int instances) {
		for(int i=0;i<instances; i++) {
			Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port = 0").withFallback(ConfigFactory.load("worker"));
			system = ActorSystem.create("WorkerSystem", conf);
			Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
			for (String contactAddress : conf.getStringList("contact-points")) {
			  initialContacts.add(system.actorSelection(contactAddress + "/user/receptionist"));
			}
			final ActorRef clusterClient = system.actorOf(ClusterClient.defaultProps(initialContacts), "clusterClient");
			ActorRef actorOf = system.actorOf(Worker.props(clusterClient, Props.create(WorkExecutor.class)));
			workers.add(actorOf);
		}
	}

	public static void shutDownWorkers() {
		system.shutdown();
	}

}
