package proxy_checker.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

/**
 * Klasa wczytujaca potencjalnie nowe proxy do bazy. Wczytuje nowe, porownyje ze
 * starymi i uaktualnia liste proxy tak aby adresy byly unikalne
 * 
 * @author mariusz
 *
 */

public class AddProxy {
	private Logger logger = Logger.getLogger(AddProxy.class);
	private EntityManagerFactory entityManagerFactory;
	/**
	 * Sciezka dostepu do pliku tekstowego z lista proxy Plik pochodzi z
	 * autosave scrapera
	 */
	private String filePath;
	/**
	 * Zbior proxy wczytany z pliku
	 */
	private Set<Proxies> newProxies = new HashSet<Proxies>();
	/**
	 * Zbior proxy wczytany z bazy danych
	 */
	private Set<Proxies> existingProxies = new HashSet<Proxies>();

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Set<Proxies> getNewProxies() {
		return newProxies;
	}

	public void setNewProxies(Set<Proxies> newProxies) {
		this.newProxies = newProxies;
	}

	public Set<Proxies> getExistingProxies() {
		return existingProxies;
	}

	public void setExistingProxies(Set<Proxies> existingProxies) {
		this.existingProxies = existingProxies;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public AddProxy(String filePath, EntityManagerFactory entityManagerFactory) {
		super();
		this.filePath = filePath;
		this.entityManagerFactory = entityManagerFactory;
		joinProxySets(this.getFilePath());
		insertProxies(this.getEntityManagerFactory());
	}

	/**
	 * wczytuje proxy z pliku tekstowego (wynik dzialania proxy scrapera)
	 * ponizsze adresy proxy beda dodawane do bazy danych
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private Set<Proxies> loadProxiesFromTextFile(String filePath) throws IOException {
		Set<Proxies> proxiesTxt = new HashSet<Proxies>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		try {
			String line = br.readLine();
			while (line != null) {
				try {
					String[] addressPort = line.split(":");
					if (addressPort.length == 2) {
						Proxies proxies = new Proxies();
						proxies.setAdres(addressPort[0]);
						proxies.setPort(Integer.parseInt(addressPort[1]));
						// Dodanie pojedynczego proxy do listy
						proxiesTxt.add(proxies);
					}
				} catch (Exception e) {
					logger.error("Nie udalo sie podzielic adresu i portu dla " + line);
				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}

		return proxiesTxt;
	}

	/**
	 * Metoda wczytuje nowe proxy z pliku tekstowego i istniejace z bazy
	 * 
	 * @param filePath
	 */
	private void joinProxySets(String filePath) {
		try {
			this.setNewProxies(loadProxiesFromTextFile(filePath));
		} catch (IOException e) {
			logger.error("Blad wczytania pliku o sciezce " + filePath);
			logger.error(e.getMessage());
		}
		try {
			this.setExistingProxies(loadProxiesFromDatabase(entityManagerFactory));
		} catch (Exception e) {
			logger.error("Blad wczytania listy istniejacych proxy z bazy danych");
			logger.error(e.getMessage());
		}
		// dodanie nowych wczesniej nieistniejacych adresow
		try {
			this.getExistingProxies().addAll(this.getNewProxies());
		} catch (Exception e) {
			logger.error("Nie mozna dodac nowych adresow proxy do istniejacego zbioru");
			logger.error(e.getMessage());
		}

	}

	/**
	 * wczytuje istniejacy zbior proxy tak aby dodac tylko adresy, ktorych nie
	 * ma w bazie
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	private Set<Proxies> loadProxiesFromDatabase(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createNamedQuery("SELECT p FROM Proxies p", Proxies.class);
		List<Proxies> proxyList = query.getResultList();
		em.getTransaction().commit();
		em.close();
		// Upewnienie siê, ¿e nie ma duplikatow
		Set<Proxies> proxies = new HashSet<Proxies>(proxyList);
		return proxies;
	}
	/**
	 * Uaktualnienie istniej¹cego zbioru o nowe numery
	 * @param entityManagerFactory
	 */
	private void insertProxies(EntityManagerFactory entityManagerFactory){
		if(!this.getExistingProxies().isEmpty()){
			EntityManager em = entityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			for(Proxies proxies:this.getExistingProxies()){
				em.persist(proxies);
			}
			em.getTransaction().commit();
			em.close();
		}else{
			logger.warn("Nie mozna dodac proxy - lista jest pusta");
			this.printInfo();
		}
	}

	/**
	 * METODY WYPISUJACE WCZYTANE PROXY
	 */
	public void printNewProxies() {
		if (!this.getNewProxies().isEmpty()) {
			for (Proxies proxies : this.getNewProxies()) {
				logger.info("newProxy=" + proxies.toString());
			}
		} else
			logger.warn("Brak adresow proxy w newProxy");
	}

	public void printExistingProxies() {
		if (!this.getExistingProxies().isEmpty()) {
			for (Proxies proxies : this.getExistingProxies()) {
				logger.info("existingProxy=" + proxies.toString());
			}
		} else {
			logger.warn("Brak adresow proxy w existingProxy");
		}
	}
	public void printInfo(){
		logger.info("newProxies zawiera "+this.getNewProxies().size()+" elementow");
		logger.info("existingProxies zawiera "+this.getExistingProxies().size()+" elementow");
	}

}
