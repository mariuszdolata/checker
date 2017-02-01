package proxy_checker.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;

import crawler.api.DatabaseAccess;
import proxy_checker.db.Proxies;

public class CheckTaskRepository implements Runnable {

	public Logger logger = Logger.getLogger(CheckTaskRepository.class);
	private List<Proxies> proxiesToCheck = Collections.synchronizedList(new ArrayList<Proxies>());
	private List<String> status = Collections.synchronizedList(new ArrayList<String>());
	private BrowserSettings browserSettings;
	private EntityManagerFactory entityManagerFactory;
	private int threadId;

	
	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

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

	public CheckTaskRepository(int threadId, EntityManagerFactory entityManagerFactory,
			BrowserSettings browserSettings, List<Proxies> proxiesToCheck, List<String> status) {
		this.setBrowserSettings(browserSettings);
		this.setProxiesToCheck(proxiesToCheck);
		this.setStatus(status);
		this.setThreadId(threadId);
		this.setEntityManagerFactory(entityManagerFactory);
	}

	public void run() {
		do{//petla restartuj�ca watek po bledzie
			try{//glowny blok wykonywania w�tku
				Integer index = null;
				do{
					index = getProxy(status);
					if(index!=null){
						//uurchomienie pojedynczego testu
						CheckTask task = new CheckTask(this.getThreadId(),  this.getEntityManagerFactory(), this.getBrowserSettings(), this.getProxiesToCheck().get(index));
						//zapisanie statusu dla danego proxy
						task.startChecking();
						setProxy(status, index, task.getResult());
					}
				}while(index!= null);
				
				
			}catch(Exception e){
				logger.error("Thread - zosta� rzucony wyj�tek, nast�pi�o ponowienie");
				logger.error(e.getMessage());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(!Thread.interrupted());
	}

	public synchronized Integer getProxy(List<String> status) {
		int j;
		for (int i = 0; i < status.size(); i++) {
			if (status.get(i) != "SPRAWDZONO" && status.get(i) != "SPRAWDZANIE")
				status.set(i, "SPRAWDZANIE");
			return i;
		}
		return (Integer) null;
	}

	public synchronized void setProxy(List<String> status, int i, String statusString) {
		status.set(i, statusString);
	}

}
