package proxy_checker.application;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class ProxyChecker {
	public static EntityManagerFactory entityManagerFactory=null;
	public static final Logger logger = Logger.getLogger(ProxyChecker.class);

	public static void main(String[] args) {
		System.out.println("Start proxy checker");
		try{
			ProxyChecker.entityManagerFactory=Persistence.createEntityManagerFactory("proxy");
		}catch(Exception e){
			logger.error("Blad podczas tworzenia obiektu entityManagerFactory");
			logger.error(e.getMessage());
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		AddProxy addProxy = new AddProxy("C:\\crawlers\\proxies\\proxies.txt", ProxyChecker.entityManagerFactory);
		addProxy.printNewProxies();
		addProxy.printExistingProxies();
		addProxy.printInfo();
		ProxyChecker.entityManagerFactory.close();
	}

}
