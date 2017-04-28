package com.fsd.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "table1")
public class GenderStats {

	@Id
	private Integer id;
	
	
	private String sexname;
	private String Total;
	private String Defaults;
	private String Percent;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSexname() {
		return sexname;
	}
	public void setSexname(String sexname) {
		this.sexname = sexname;
	}
	public String getTotal() {
		return Total;
	}
	public void setTotal(String total) {
		Total = total;
	}
	public String getDefaults() {
		return Defaults;
	}
	public void setDefaults(String defaults) {
		Defaults = defaults;
	}
	public String getPercent() {
		return Percent;
	}
	public void setPercent(String percent) {
		Percent = percent;
	}
	public GenderStats(Integer id, String sexname, String total, String defaults, String percent) {
		super();
		this.id = id;
		this.sexname = sexname;
		Total = total;
		Defaults = defaults;
		Percent = percent;
	}
	public GenderStats() {
		super();
	}
	
	
	
	
	
	
}
