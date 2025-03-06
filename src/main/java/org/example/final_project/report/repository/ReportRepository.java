package org.example.final_project.report.repository;

import org.example.final_project.report.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findAllByResolvedOrderByReportTimeDesc(boolean resolved);
}
