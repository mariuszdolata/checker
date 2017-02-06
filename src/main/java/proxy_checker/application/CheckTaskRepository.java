package proxy_checker.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import crawler.api.DatabaseAccess;
import proxy_checker.db.Proxies;

public class CheckTaskRepository extends DatabaseAccess implements Runnable {

	private List<Proxies> proxiesToCheck = Collections.synchronizedList(new ArrayList<Proxies>());
	private List<String> status = Collections.synchronizedList(new ArrayList<String>());
	private BrowserSettings browserSettings;

	public BrowserSettings getBrowserSettings() {
		return browserSettings;
	}

	public void setBrowserSettings(BrowserSettings browserSettings) {
		this.browserSettings = browserSettings;
	}

	public List<Proxies> getProxiesToCheck() {
		return proxiesToCheck;
	}

	public void setProxiesToCheck(List<Proxies> proxiesToCheck) {
		this.proxiesToCheck = proxiesToCheck;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public CheckTaskRepository(int threadId, Properties properties, EntityManagerFactory entityManagerFactory,
			BrowserSettings browserSettings, List<Proxies> proxiesToCheck, List<String> status) {
		super(threadId, properties, entityManagerFactory);
		this.setBrowserSettings(browserSettings);
		this.setProxiesToCheck(proxiesToCheck);
		this.setStatus(status);
	}

	public void run() {
		do{
			int proxyNumber = getProxy(status);
			CheckTask task = new CheckTask(this.getThreadId(), this.getProperties(), this.getEntityManagerFactory(), proxiesToCheck.get(proxyNumber), this.getBrowserSettings());
			setProxy(status, proxyNumber, "DONE");
		}while(true);

	}

	public synchronized int getProxy(List<String> status) {
		int j;
		for (int i = 0; i < status.size(); i++) {
			if (status.get(i) != "DONE" && status.get(i) != "PENDING")
				status.set(i, "PENDING");
			return i;
		}
		return (Integer) null;
	}

	public synchronized void setProxy(List<String> status, int i, String statusString) {
		status.set(i, statusString);
	}

}
