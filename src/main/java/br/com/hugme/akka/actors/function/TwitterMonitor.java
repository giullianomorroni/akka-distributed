package br.com.hugme.akka.actors.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import br.com.hugme.akka.beans.work.Work;

public class TwitterMonitor implements Runnable {

	private UntypedActor actor;

	public TwitterMonitor(UntypedActor actor) {
		this.actor = actor;
	}

	public void run() {
		try {
			File file = new File("/tmp","twitter.txt");
			if (file.exists()){
				FileReader fileReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(fileReader);
				String content = reader.readLine();
				reader.close();
				fileReader.close();
				Work work = new Work(content);
				this.actor.getSelf().tell(work, ActorRef.noSender());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
