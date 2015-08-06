package br.com.hugme.akka;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.contrib.pattern.ClusterSingletonManager;
import br.com.hugme.akka.actors.Master;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class DistributedClusterMain {

	private static ActorSystem cluster; 

	public static void startMasters() {
		startMasters(2551, "backend");
		startMasters(2552, "backend");
	}

	public static void shutDownMasters() {
		cluster.shutdown();
	}

	public static void startMasters(int port, String role) {
		Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
				withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)).
				withFallback(ConfigFactory.load());

		cluster = ActorSystem.create("ClusterSystem", conf);
		FiniteDuration workTimeout = Duration.create(10, "seconds");
		cluster.actorOf(ClusterSingletonManager.defaultProps(Master.props(workTimeout), "active", PoisonPill.getInstance(), role), "master");
	}

}
