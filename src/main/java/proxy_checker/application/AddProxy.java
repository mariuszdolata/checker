package proxy_checker.application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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
 * starymi i uaktualnia liste proxy tak aby adresy byly unikalne//
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
	private List<Proxies> newProxies = new ArrayList<Proxies>();
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

	public List<Proxies> getNewProxies() {
		return newProxies;
	}

	public void setNewProxies(List<Proxies> newProxies) {
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
//		insertProxies(this.getEntityManagerFactory());
	}
	public AddProxy(EntityManagerFactory entityManagerFactory){
		this.entityManagerFactory=entityManagerFactory;
	}

	/**
	 * wczytuje proxy z pliku tekstowego (wynik dzialania proxy scrapera)
	 * ponizsze adresy proxy beda dodawane do bazy danych
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private List<Proxies> loadProxiesFromTextFile(String filePath) throws IOException {
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

		return new ArrayList<Proxies>(proxiesTxt);
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
			e.printStackTrace();
		}
		// dodanie nowych wczesniej nieistniejacych adresow
		try {
//			this.getExistingProxies().addAll(this.getNewProxies());
			this.getNewProxies().removeAll(this.getExistingProxies());
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
	public Set<Proxies> loadProxiesFromDatabase(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p", Proxies.class);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		em.getTransaction().commit();
		em.close();
		return set;
	}
	public Set<Proxies> loadProxiesFromDatabaseRandom(EntityManagerFactory entityManagerFactory, int numberOfRecords){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("Select p FROM Proxies p order by rand()", Proxies.class).setMaxResults(numberOfRecords);
		if(em.getTransaction().isActive())em.getTransaction().commit();
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		em.close();
		return set;
	}
	public Set<Proxies> loadProxiesFromDataBaseRecently(EntityManagerFactory entityManagerFactory){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p order by dataDodania desc", Proxies.class);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		if(em.getTransaction().isActive())em.getTransaction().commit();
		em.close();
		return set;
	}
	public Set<Proxies> loadProxiesFromDataBaseRank(EntityManagerFactory entityManagerFactory, double rank){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p WHERE rank <=:rank", Proxies.class);
		query.setParameter("rank", rank);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		if(em.getTransaction().isActive())em.getTransaction().commit();
		em.close();
		return set;
	}

	/**
	 * Uaktualnienie istniej¹cego zbioru o nowe numery
	 * 
	 * @param entityManagerFactory
	 */
	public void insertProxies(EntityManagerFactory entityManagerFactory) {
		if (!this.getNewProxies().isEmpty()) {
			EntityManager em = entityManagerFactory.createEntityManager();
			for (Proxies proxies : this.getNewProxies()) {
				try {
					if (!em.getTransaction().isActive())
						em.getTransaction().begin();

					em.persist(proxies);
					if (em.getTransaction().isActive())
						em.getTransaction().commit();
					logger.info("wstawiony adres " + proxies.toString());
				} catch (Exception e) {
//					em.getTransaction().commit();
					logger.warn("nie mozna wstawic obiektu do bazy " + proxies.toString());
					StringWriter stack = new StringWriter();
					e.printStackTrace(new PrintWriter(stack));

					logger.warn(stack.toString());
				}
			}
			em.close();
		} else {
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

	public void printInfo() {
		logger.info("newProxies zawiera " + this.getNewProxies().size() + " elementow");
		logger.info("existingProxies zawiera " + this.getExistingProxies().size() + " elementow");
	}

}
