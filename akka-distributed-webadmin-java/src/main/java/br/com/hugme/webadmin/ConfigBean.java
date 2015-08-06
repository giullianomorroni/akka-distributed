package br.com.hugme.webadmin;

import java.io.Serializable;

import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import br.com.hugme.akka.DistributedClusterMain;
import br.com.hugme.akka.DistributedWorkMain;
import br.com.hugme.akka.DistributedWorkersMain;

@ManagedBean
@ApplicationScoped
public class ConfigBean implements Serializable {

	private static final long serialVersionUID = -6566432870520288620L;

	public String startCluster() {
		DistributedClusterMain.startMasters();
		return "success";
	}

	public String startSocialMonitors() {
		DistributedWorkMain.startMonitors();
		return "success";
	}

	public String startWorker() {
		DistributedWorkersMain.startWorkers(1);
		return "success";
	}

	@PreDestroy
	public void destroy() {
		DistributedWorkMain.shutDownWorks();
		DistributedWorkersMain.shutDownWorkers();
		DistributedClusterMain.shutDownMasters();
	}

}