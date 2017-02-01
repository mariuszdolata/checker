package proxy_checker.db;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="score")
public class Score {

	private Long id;
	private Date dataTestu;
	private String potwierdzenie;
	private int ms;
	private int proby;
	private Proxies proxies;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataTestu() {
		return dataTestu;
	}
	public void setDataTestu(Date dataTestu) {
		this.dataTestu = dataTestu;
	}
	@Column(columnDefinition="mediumtext")
	public String getPotwierdzenie() {
		return potwierdzenie;
	}
	public void setPotwierdzenie(String potwierdzenie) {
		this.potwierdzenie = potwierdzenie;
	}
	public int getMs() {
		return ms;
	}
	public void setMs(int ms) {
		this.ms = ms;
	}
	public int getProby() {
		return proby;
	}
	public void setProby(int proby) {
		this.proby = proby;
	}
	@ManyToOne(cascade=CascadeType.ALL)
	public Proxies getProxy() {
		return proxies;
	}
	public void setProxy(Proxies proxies) {
		this.proxies = proxies;
	}
	public Score() {
		super();
	}
	
	
}
