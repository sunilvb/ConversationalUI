package com.fsd.repository;

import com.fsd.models.GenderStats;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GenderStatsRepo extends CrudRepository<GenderStats, Integer>{

}
