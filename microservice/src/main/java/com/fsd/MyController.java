package com.fsd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsd.models.GenderStats;
import com.fsd.repository.GenderStatsRepo;

@RestController
public class MyController {
	
	@Autowired
	GenderStatsRepo repo;
	
	@RequestMapping("/gender")
	public String doall()
	{
		String mP = null, fP = null;
		for(GenderStats cc : repo.findAll())
		{
			if("Male".equals(cc.getSexname())) 
				mP = cc.getPercent();
			else
				fP = cc.getPercent();
		}
		
		return "Historically the male credit card default percentage has been " + mP + " % and the female credit card default percentage has been " + fP + " %";
	}
	
	

}
