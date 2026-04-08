package com.election.evm.repository;

import com.election.evm.entity.FraudReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FraudReportRepository extends JpaRepository<FraudReport, String> {
    List<FraudReport> findByCreatedByIdOrderByCreatedAtDesc(String createdById);
}
