package com.fsd.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.fsd.models.EduStats;


@RepositoryRestResource
public interface EduStatsRepo extends CrudRepository<EduStats, Integer>{
	public List<EduStats> findAllByOrderByPerdefaultDesc();

}
