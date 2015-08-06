package br.com.hugme.webadmin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import akka.actor.ActorRef;
import br.com.hugme.akka.DistributedWorkersMain;
import br.com.hugme.akka.actors.Master;

@ManagedBean
@RequestScoped
public class MasterBean implements Serializable {

	private static final long serialVersionUID = -4439408028673267295L;

	private List<String> clusters;
	private List<String> workers;

	@PostConstruct
	public void init() {
		clusters = new ArrayList<String>();
		workers = new ArrayList<String>();

		List<String> configs = Master.configs();
		clusters.addAll(configs);

		for (ActorRef a : DistributedWorkersMain.workers) {
			workers.add("Path: " + a.path());
		}
	}

	public List<String> getClusters() {
		return clusters;
	}

	public void setClusters(List<String> clusters) {
		this.clusters = clusters;
	}

	public List<String> getWorkers() {
		return workers;
	}

	public void setWorkers(List<String> workers) {
		this.workers = workers;
	}

}