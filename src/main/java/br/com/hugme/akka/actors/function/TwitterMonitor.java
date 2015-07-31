package br.com.hugme.akka.actors.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import br.com.hugme.akka.beans.work.Work;
import br.com.hugme.akka.beans.worker.WorkerRequestsWork;

public class TwitterMonitor implements Runnable {

	private UntypedActor actor;

	public TwitterMonitor(UntypedActor actor) {
		this.actor = actor;
	}

	public void run() {
		try {
			File file = new File("/tmp","twitter.json");
			if (file.exists()){
				FileReader fileReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(fileReader);
				String content = new String();
				while(reader.ready()) {
					content += reader.readLine();
				}
				reader.close();
				fileReader.close();
				JSONArray jsonArray = new JSONArray(content);
				for (int i=0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.optJSONObject(i);
					Work work = new Work(jsonObject.toString());
					this.actor.getSelf().tell(work, ActorRef.noSender());
				}
				file.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
