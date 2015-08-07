package br.com.hugme.webadmin;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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
		FutureTask<String> s1 = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				DistributedWorkMain.shutDownWorks();
				return new String();
			}
		});

		FutureTask<String> s2 = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				DistributedWorkersMain.shutDownWorkers();
				return new String();
			}
		});

		FutureTask<String> s3 = new FutureTask<String>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				DistributedClusterMain.shutDownMasters();
				return new String();
			}
		});

		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.submit(s1);
		executor.submit(s2);
		executor.submit(s3);
	}

}