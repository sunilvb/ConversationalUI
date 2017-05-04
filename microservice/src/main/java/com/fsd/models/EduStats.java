package com.fsd.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "table2")
public class EduStats {

	@Id
	private Integer id;
	
	private String marriagename;
	private String eduname;
	private String total;
	private String defaults;
	private String perdefault;
	public EduStats(Integer id, String marriagename, String eduname, String total, String defaults, String perdefault) {
		super();
		this.id = id;
		this.marriagename = marriagename;
		this.eduname = eduname;
		this.total = total;
		this.defaults = defaults;
		this.perdefault = perdefault;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMarriagename() {
		return marriagename;
	}
	public void setMarriagename(String marriagename) {
		this.marriagename = marriagename;
	}
	public String getEduname() {
		return eduname;
	}
	public void setEduname(String eduname) {
		this.eduname = eduname;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getDefaults() {
		return defaults;
	}
	public void setDefaults(String defaults) {
		this.defaults = defaults;
	}
	public String getPerdefault() {
		return perdefault;
	}
	public void setPerdefault(String perdefault) {
		this.perdefault = perdefault;
	}
	public EduStats() {
		super();
	}
	
	
	
	
}
