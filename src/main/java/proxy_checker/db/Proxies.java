package proxy_checker.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;
//
@Entity
@Table(name="proxies")
public class Proxies {
	private Long id;
	private Date dataDodania;
	private String adres;
	private int port;
	private double rank;
	private Set<Score> scores = new HashSet<Score>();
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="proxy_id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataDodania() {
		return dataDodania;
	}
	public void setDataDodania(Date dataDodania) {
		this.dataDodania = dataDodania;
	}
	@Column(columnDefinition="mediumtext")
	public String getAdres() {
		return adres;
	}
	public void setAdres(String adres) {
		this.adres = adres;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Column(nullable=true)
	public double getRank() {
		return rank;
	}
	public void setRank(double rank) {
		this.rank = rank;
	}
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinTable(name="proxies_score", joinColumns={@JoinColumn(name="proxy_id", nullable=false, updatable=false)}, inverseJoinColumns={
			@JoinColumn(name="score_id", nullable=false, updatable=false)
	})
	public Set<Score> getScores() {
		return scores;
	}
	public void setScores(Set<Score> scores) {
		this.scores = scores;
	}
	public Proxies() {
		super();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adres == null) ? 0 : adres.hashCode());
		result = prime * result + port;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Proxies other = (Proxies) obj;
		if (adres == null) {
			if (other.adres != null)
				return false;
		} else if (!adres.equals(other.adres))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Proxies [id=" + id + ", dataDodania=" + dataDodania + ", adres=" + adres + ", port=" + port + ", rank="
				+ rank + ", scores=" + scores + "]";
	}
	
	
	

}
