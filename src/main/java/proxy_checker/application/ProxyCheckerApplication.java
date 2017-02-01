package proxy_checker.application;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

public class ProxyCheckerApplication extends JFrame {
	public Logger logger = Logger.getLogger(ProxyCheckerApplication.class);
	private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("proxy");
	private List<Proxies> proxiesToCheck = Collections.synchronizedList(new ArrayList<Proxies>());
	private List<String> status = Collections.synchronizedList(new ArrayList<String>());
	private AddProxy addProxy = new AddProxy(entityManagerFactory);
	private File openSelectedFile;

	private JScrollPane scrollPane = new JScrollPane();
	private JPanel panelLista = new JPanel();
	private JPanel panelWynik = new JPanel();
	private JMenuBar menuBar;
	private JMenu mnPlik;
	private JTextField textNumberOfThreads;
	private JTextField textTimeOut;
	private JTextField textNumberOfRetrying;
	private JTextField txtUrlToScrape;
	private JTextField textXPath;
	private JTextField textSuccess;
	private JTextField textFail;
	private JTable table;
	private final ButtonGroup buttonGroupBrowser = new ButtonGroup();
	private final ButtonGroup buttonGroupKryterium = new ButtonGroup();
	private JRadioButton rdbtnAllProxies = new JRadioButton("wszystkie");
	private JRadioButton rdbtnRandomProxies = new JRadioButton("losowe");
	private JRadioButton rdbtnRecentlyProxies = new JRadioButton("dodane dzisiaj");
	private JRadioButton rdbtnGraderThanProxies = new JRadioButton("wi\u0119ksze ni\u017C");
	private JRadioButton rdbtnLessThanProxies = new JRadioButton("mniejsze ni\u017C");
	private JCheckBox chckbxJSDisable = new JCheckBox("");
	private JCheckBox chckbxImageDisable = new JCheckBox("");
	JCheckBox chckbxNonStop = new JCheckBox("");
	private JRadioButton rdbtnHtmlUnit = new JRadioButton("HtmlUnit");
	private JRadioButton rdbtnSelenium = new JRadioButton("Selenium");

	public List<Proxies> getProxiesToCheck() {
		return proxiesToCheck;
	}

	public void setProxiesToCheck(List<Proxies> proxiesToCheck) {
		this.proxiesToCheck = proxiesToCheck;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public AddProxy getAddProxy() {
		return addProxy;
	}

	public void setAddProxy(AddProxy addProxy) {
		this.addProxy = addProxy;
	}

	public File getOpenSelectedFile() {
		return openSelectedFile;
	}

	public void setOpenSelectedFile(File openSelectedFile) {
		this.openSelectedFile = openSelectedFile;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ProxyCheckerApplication frame = new ProxyCheckerApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ProxyCheckerApplication() {
		setTitle("Proxy checker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 635, 784);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnPlik = new JMenu("Plik");
		menuBar.add(mnPlik);

		JButton btnDodaj = new JButton("dodaj proxy");
		btnDodaj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane = AppMethods.selectOpenFile(entityManagerFactory, scrollPane);
				panelLista.removeAll();
				panelLista.add(scrollPane);
				panelLista.invalidate();
			}
		});

		JButton btnPobierz = new JButton("pobierz proxy");
		btnPobierz.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proxiesToCheck.clear();
				status.clear();
				
				proxiesToCheck = AppMethods.criteriaSelectProxy(entityManagerFactory, rdbtnAllProxies,
						rdbtnRandomProxies, rdbtnRecentlyProxies, rdbtnGraderThanProxies, rdbtnLessThanProxies);
				panelLista.removeAll();
				for(int i=0;i<proxiesToCheck.size();i++)
					status.add("DO SPRAWDZENIA");
				panelLista.add(AppMethods.fillTable(proxiesToCheck, status));
				panelLista.invalidate();
			}
		});

		JButton btnTestuj = new JButton("testuj");
		btnTestuj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BrowserSettings browserSettings = new BrowserSettings(textTimeOut, chckbxJSDisable, chckbxImageDisable,
						textNumberOfRetrying, txtUrlToScrape, textXPath, textSuccess, textFail, rdbtnSelenium,
						rdbtnHtmlUnit, chckbxNonStop, textNumberOfThreads);
				try{
					StartTask startTask = new StartTask(entityManagerFactory, browserSettings, proxiesToCheck, status);
					startTask.start();
				}catch(Exception ex){
					logger.error("Blad przy tworzeniu obiektu StartTask\n"+ex.getMessage());
				}

			}
		});

		JButton btnZatrzymaj = new JButton("zatrzymaj test");
		btnZatrzymaj.setEnabled(false);

		JButton btnStatystyka = new JButton("statystyka");

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		JProgressBar progressBar = new JProgressBar();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 598, Short.MAX_VALUE)
								.addGroup(groupLayout.createSequentialGroup().addComponent(btnDodaj)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnPobierz)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnTestuj)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnZatrzymaj)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnStatystyka))
								.addComponent(progressBar, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 598,
										Short.MAX_VALUE))
						.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup().addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnDodaj)
								.addComponent(btnPobierz).addComponent(btnTestuj).addComponent(btnZatrzymaj)
								.addComponent(btnStatystyka))
						.addGap(16)
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 620, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		JPanel panelOpcje = new JPanel();
		tabbedPane.addTab("opcje", null, panelOpcje, null);

		JLabel lblNewLabel = new JLabel("przegladarka");

		buttonGroupBrowser.add(rdbtnHtmlUnit);
		rdbtnHtmlUnit.setSelected(true);

		buttonGroupBrowser.add(rdbtnSelenium);

		JSeparator separator = new JSeparator();

		JSeparator separator_1 = new JSeparator();

		JLabel lblNewLabel_1 = new JLabel("kryterium");

		buttonGroupKryterium.add(rdbtnAllProxies);
		rdbtnAllProxies.setSelected(true);

		buttonGroupKryterium.add(rdbtnRandomProxies);

		buttonGroupKryterium.add(rdbtnRecentlyProxies);

		buttonGroupKryterium.add(rdbtnGraderThanProxies);

		buttonGroupKryterium.add(rdbtnLessThanProxies);

		JLabel lblNewLabel_2 = new JLabel("test non-stop");

		JLabel lblNewLabel_3 = new JLabel("liczba w\u0105tk\u00F3w");

		textNumberOfThreads = new JTextField();
		textNumberOfThreads.setText("1");
		textNumberOfThreads.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("TimeOut [ms]");

		JLabel lblNewLabel_5 = new JLabel("wy\u0142\u0105czenie javaScript");

		JLabel lblNewLabel_6 = new JLabel("wy\u0142\u0105czenie obrazk\u00F3w");

		JLabel lblNewLabel_7 = new JLabel("liczba powt\u00F3rze\u0144");

		textTimeOut = new JTextField();
		textTimeOut.setText("30000");
		textTimeOut.setColumns(10);

		textNumberOfRetrying = new JTextField();
		textNumberOfRetrying.setText("3");
		textNumberOfRetrying.setColumns(10);

		JSeparator separator_2 = new JSeparator();

		JLabel lblNewLabel_8 = new JLabel("URL");

		JLabel lblNewLabel_9 = new JLabel("XPath");

		JLabel lblNewLabel_10 = new JLabel("sukces");

		JLabel lblNewLabel_11 = new JLabel("pora\u017Cka");

		txtUrlToScrape = new JTextField();
		txtUrlToScrape.setText("http://www.moje-ip.eu");
		txtUrlToScrape.setColumns(10);

		textXPath = new JTextField();
		textXPath.setText(".//div[@class=\"mjip2\"]");
		textXPath.setColumns(10);

		textSuccess = new JTextField();
		textSuccess.setColumns(10);

		textFail = new JTextField();
		textFail.setColumns(10);
		GroupLayout gl_panelOpcje = new GroupLayout(panelOpcje);
		gl_panelOpcje.setHorizontalGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
				.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 701, Short.MAX_VALUE)
				.addGroup(gl_panelOpcje.createSequentialGroup()
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING).addGroup(gl_panelOpcje
								.createSequentialGroup().addContainerGap().addComponent(lblNewLabel).addGap(34)
								.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnHtmlUnit).addComponent(rdbtnSelenium))
								.addGap(120).addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_3).addGroup(gl_panelOpcje.createSequentialGroup()
												.addComponent(lblNewLabel_2).addGap(33).addGroup(gl_panelOpcje
														.createParallelGroup(Alignment.LEADING)
														.addComponent(textNumberOfThreads, GroupLayout.PREFERRED_SIZE,
																29, GroupLayout.PREFERRED_SIZE)
														.addComponent(chckbxNonStop)))))
								.addGroup(gl_panelOpcje.createSequentialGroup().addGap(296).addComponent(separator_1,
										GroupLayout.PREFERRED_SIZE, 1, GroupLayout.PREFERRED_SIZE)))
						.addGap(252))
				.addGroup(gl_panelOpcje.createSequentialGroup()
						.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, 593, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
				.addGroup(gl_panelOpcje.createSequentialGroup().addContainerGap()
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING, false)
								.addComponent(lblNewLabel_8, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_9, GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
								.addComponent(lblNewLabel_10, GroupLayout.PREFERRED_SIZE, 49,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_11, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))
						.addGap(12)
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelOpcje.createParallelGroup(Alignment.TRAILING)
										.addComponent(textXPath, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 502,
												Short.MAX_VALUE)
										.addComponent(txtUrlToScrape, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
												502, Short.MAX_VALUE)
										.addComponent(textSuccess, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 502,
												Short.MAX_VALUE))
								.addComponent(textFail, GroupLayout.PREFERRED_SIZE, 502, GroupLayout.PREFERRED_SIZE))
						.addGap(120))
				.addGroup(
						gl_panelOpcje.createSequentialGroup().addContainerGap().addComponent(lblNewLabel_1).addGap(51)
								.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnLessThanProxies)
										.addGroup(gl_panelOpcje.createSequentialGroup()
												.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
														.addComponent(rdbtnRandomProxies).addComponent(rdbtnAllProxies)
														.addComponent(rdbtnRecentlyProxies)
														.addComponent(rdbtnGraderThanProxies))
												.addGap(103)
												.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
														.addComponent(lblNewLabel_4).addComponent(lblNewLabel_7)
														.addComponent(lblNewLabel_5).addComponent(lblNewLabel_6))
												.addGap(29)
												.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING, false)
														.addComponent(chckbxImageDisable).addComponent(chckbxJSDisable)
														.addComponent(textTimeOut, GroupLayout.DEFAULT_SIZE, 50,
																Short.MAX_VALUE)
														.addComponent(textNumberOfRetrying, 0, 0, Short.MAX_VALUE))))
								.addContainerGap(193, Short.MAX_VALUE)));
		chckbxJSDisable.setSelected(true);
		gl_panelOpcje.setVerticalGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING).addGroup(gl_panelOpcje
				.createSequentialGroup().addContainerGap()
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.TRAILING).addGroup(gl_panelOpcje
						.createSequentialGroup()
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel)
								.addComponent(rdbtnHtmlUnit).addComponent(lblNewLabel_2).addComponent(chckbxNonStop))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING).addComponent(rdbtnSelenium)
								.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel_3).addComponent(textNumberOfThreads,
												GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE))))
						.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_4)
						.addComponent(textTimeOut, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(rdbtnAllProxies))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_5)
						.addComponent(chckbxJSDisable).addComponent(rdbtnRandomProxies))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_6)
						.addComponent(chckbxImageDisable).addComponent(rdbtnRecentlyProxies))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_7)
								.addComponent(textNumberOfRetrying, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(rdbtnGraderThanProxies))
				.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(rdbtnLessThanProxies).addGap(2)
				.addComponent(separator_2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_8)
						.addComponent(txtUrlToScrape, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_9)
						.addComponent(textXPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_10)
						.addComponent(textSuccess, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_panelOpcje.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_11)
						.addComponent(textFail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addContainerGap(229, Short.MAX_VALUE)));
		panelOpcje.setLayout(gl_panelOpcje);

		tabbedPane.addTab("lista proxy", null, panelLista, null);

		table = new JTable();
		panelLista.add(table);

		panelLista.add(scrollPane);

		tabbedPane.addTab("wynik testowania", null, panelWynik, null);
		getContentPane().setLayout(groupLayout);
	}

}
