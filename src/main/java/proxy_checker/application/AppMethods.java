package proxy_checker.application;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

/**
 * Klasa zawieraj¹ca metody do obs³ugi GUI
 * @author mariusz
 *
 */
public class AppMethods {
	public static Logger logger = Logger.getLogger(AppMethods.class);
	
	/**
	 * Metoda zwracaj¹ca tabelê (w JScrollPane!) z wynikami na podstawie zbioru proxy
	 * @param data
	 */
	public static JScrollPane  fillTable(Set<Proxies> data) {
		
		Object[] col = { "id", "data dodania", "adres", "port", "ranking" };
		Object[][] proxies = new Object[data.size()][5];
		Iterator<Proxies> iterateNewProxies = data.iterator();
		int iter = 0;
		while (iterateNewProxies.hasNext()) {
			Proxies proxy = iterateNewProxies.next();
			try {
				proxies[iter][0] = proxy.getId();
				proxies[iter][1] = proxy.getDataDodania();
				proxies[iter][2] = proxy.getAdres();
				proxies[iter][3] = proxy.getPort();
				proxies[iter][4] = proxy.getRank();
			} catch (Exception e) {
				logger.error("Blad podczas wype³niania tabeli danymi nowych proxy");
				e.printStackTrace();
			}
			iter++;
		}
		
		JScrollPane scrollPane = new JScrollPane(new JTable(proxies, col));
		scrollPane.setPreferredSize(new Dimension(600,800));
		return scrollPane;
	}
/**
 * Metoda wczytuj¹ca plik tekstowy z list¹ proxy, wyznaczaj¹ca tylko nowe adresy proxy,
 * oraz wstawiaj¹ca nowoznalezione do bazy danych (poprzedzaj¹ce pytanie)
 * @param entityManagerFactory
 */
	public static JScrollPane selectOpenFile(EntityManagerFactory entityManagerFactory, JScrollPane scrollPane) {
		File file;
		JFileChooser fileChooser = new JFileChooser("C://crawlers//proxies");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file=fileChooser.getSelectedFile();
			try {
				logger.info("Wczytano plik: " + file.getCanonicalPath().toString());
				
				AddProxy addProxy = new AddProxy(file.getCanonicalPath().toString(), entityManagerFactory);
				// Wypelnienie tabeli nowymi adresami proxy
				scrollPane = fillTable(addProxy.getNewProxies());
				int insert = JOptionPane.showConfirmDialog(null, "Czy umieœciæ nowe adresy proxy w bazie", "INSERT",
						JOptionPane.YES_NO_OPTION);
				if (insert == JOptionPane.YES_OPTION) {
					try {
						addProxy.insertProxies(entityManagerFactory);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "B³¹d podczas wstawiania nowych adresów proxy do bazy");
						logger.error("B³¹d podczas wstawiania nowych adresów proxy do bazy");
						logger.error(e.getLocalizedMessage());
					}
				} else {
					JOptionPane.showMessageDialog(null, "Nowe adresy nie zosta³y umieszczone w bazie",
							"Pominiêcie insertu", JOptionPane.INFORMATION_MESSAGE);
				}

			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "B³¹d podczas wczytywania pliku z proxy", "B³¹d",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

		}
		return scrollPane;
	}
	public static Set<Proxies> criteriaSelectProxy(EntityManagerFactory entityManagerFactory, JRadioButton all, JRadioButton random, JRadioButton recently, JRadioButton grader, JRadioButton less){
		Set<Proxies> selectedProxies = new HashSet<Proxies>();
		if(all.isSelected()){
			selectedProxies = AppMethods.loadProxiesFromDatabaseAll(entityManagerFactory);
		}else if(random.isSelected()){
			selectedProxies = AppMethods.loadProxiesFromDatabaseRandom(entityManagerFactory, 10);
		}else if(recently.isSelected()){
			selectedProxies = AppMethods.loadProxiesFromDataBaseRecently(entityManagerFactory);
		}else if(grader.isSelected()){
			selectedProxies = AppMethods.loadProxiesFromDataBaseRank(entityManagerFactory, 0);
		}else if(less.isSelected()){
			selectedProxies = AppMethods.loadProxiesFromDataBaseRank(entityManagerFactory, 0);
		}else{
			logger.error("Nie zostalo wybrane zande kryterium wyboru!");
		}
		
		return selectedProxies;
	}
	/**
	 * wczytuje istniejacy zbior proxy tak aby dodac tylko adresy, ktorych nie
	 * ma w bazie
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	public static Set<Proxies> loadProxiesFromDatabaseAll(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p order by id", Proxies.class);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		em.getTransaction().commit();
		em.close();
		return set;
	}
	public static Set<Proxies> loadProxiesFromDatabaseRandom(EntityManagerFactory entityManagerFactory, int numberOfRecords){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("Select p FROM Proxies p order by rand()", Proxies.class).setMaxResults(numberOfRecords);
		if(em.getTransaction().isActive())em.getTransaction().commit();
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		em.close();
		return set;
	}
	public static Set<Proxies> loadProxiesFromDataBaseRecently(EntityManagerFactory entityManagerFactory){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p order by dataDodania desc", Proxies.class);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		if(em.getTransaction().isActive())em.getTransaction().commit();
		em.close();
		return set;
	}
	public static Set<Proxies> loadProxiesFromDataBaseRank(EntityManagerFactory entityManagerFactory, double rank){
		EntityManager em = entityManagerFactory.createEntityManager();
		if(!em.getTransaction().isActive())em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p WHERE rank <=:rank", Proxies.class);
		query.setParameter("rank", rank);
		Set<Proxies> set = new HashSet<Proxies>(query.getResultList());
		if(em.getTransaction().isActive())em.getTransaction().commit();
		em.close();
		return set;
	}




}
