package com.szcinda.rpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobEntityRepository extends JpaRepository<JobEntity, String>, JpaSpecificationExecutor<JobEntity> {
}
