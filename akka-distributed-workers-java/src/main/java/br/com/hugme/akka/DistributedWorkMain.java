package br.com.hugme.akka;

import akka.actor.*;
import br.com.hugme.akka.actors.SocialMonitorWorker;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DistributedWorkMain {

	private static ActorSystem system;

	public static void main(String[] args) throws InterruptedException {
		startMonitors();
	}

	public static void startMonitors() {
		Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=0").withFallback(ConfigFactory.load());
		system = ActorSystem.create("ClusterSystem", conf);
		system.actorOf(Props.create(SocialMonitorWorker.class));
	}

	public static void shutDownWorks() {
		system.shutdown();
	}

}
