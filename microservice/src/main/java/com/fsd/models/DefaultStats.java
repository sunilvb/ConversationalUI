package com.fsd.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "table3")
public class DefaultStats {
	
	@Id
	private int custid;
	private int defaulted;
	private int indexed;
	private int prediction;
	
	public int getCustid() {
		return custid;
	}
	public void setCustid(int custid) {
		this.custid = custid;
	}
	public int getDefaulted() {
		return defaulted;
	}
	public void setDefaulted(int defaulted) {
		this.defaulted = defaulted;
	}
	public int getIndexed() {
		return indexed;
	}
	public void setIndexed(int indexed) {
		this.indexed = indexed;
	}
	public int getPrediction() {
		return prediction;
	}
	public void setPrediction(int prediction) {
		this.prediction = prediction;
	}
	public DefaultStats(int custid, int defaulted, int indexed, int prediction) {
		super();
		this.custid = custid;
		this.defaulted = defaulted;
		this.indexed = indexed;
		this.prediction = prediction;
	}
	public DefaultStats() {
		super();
	}
	
	
	

}
