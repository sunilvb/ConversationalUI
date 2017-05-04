package com.fsd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fsd.models.DefaultStats;
import com.fsd.models.EduStats;
import com.fsd.models.GenderStats;
import com.fsd.repository.DefaultStatsRepo;
import com.fsd.repository.EduStatsRepo;
import com.fsd.repository.GenderStatsRepo;

@RestController
public class MyController {
	
	@Autowired
	GenderStatsRepo repo;
	@Autowired
	EduStatsRepo repo2;
	@Autowired
	DefaultStatsRepo repo3;
	
	@RequestMapping("/gender")
	public Output doall()
	{
		String mP = null, fP = null;
		for(GenderStats cc : repo.findAll())
		{
			if("Male".equals(cc.getSexname())) 
				mP = cc.getPercent();
			else
				fP = cc.getPercent();
		}
		
		return new Output("Gender Stats", "Historically the male credit card default percentage has been " + mP + " % and the female credit card default percentage has been " + fP + " %");
	}
	
	
	@RequestMapping("/edustats")
	public Output doall2()
	{
		String mP = null, fP = null;
		EduStats cc = repo2.findAllByOrderByPerdefaultDesc().get(0);
		
		return new Output("Educational Stats","The population that is " + cc.getEduname() + " and " + cc.getMarriagename() + " are the top defaulters with default rate of " + cc.getPerdefault() +" %");
	}
	
	@RequestMapping("/forecast")
	public Output doall3()
	{
		//String mP = null, fP = null;
		int count = 0 ;
		for(DefaultStats cc : repo3.findAll())
		{
			if(cc.getPrediction() > 0)
				count++;
		}
		
		return new Output("Next Month Forecast","Based on the current month’s data, there are " + count + " defaulters forecasted for next month");
	}
	
}
