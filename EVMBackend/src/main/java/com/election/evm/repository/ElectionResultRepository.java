package com.election.evm.repository;

import com.election.evm.entity.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectionResultRepository extends JpaRepository<ElectionResult, String> {
}
