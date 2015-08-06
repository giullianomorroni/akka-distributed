package br.com.hugme.akka;

import akka.actor.*;
import akka.contrib.pattern.*;
import br.com.hugme.akka.actors.Master;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Main {

	private static FiniteDuration workTimeout = Duration.create(10, "seconds");

	public static void main(String[] args) throws InterruptedException {
		startMasters(2551, "backend");
		startMasters(2552, "backend");
	}

	public static void startMasters(int port, String role) {
		Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
				withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
				withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem", conf);
		system.actorOf(ClusterSingletonManager.defaultProps(Master.props(workTimeout), "active", PoisonPill.getInstance(), role), "master");
	}

}
