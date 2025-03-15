package org.example.final_project.report.service;

import lombok.extern.slf4j.Slf4j;
import org.example.final_project.comment.model.Comment;
import org.example.final_project.exception.ReportNotFoundException;
import org.example.final_project.report.model.Report;
import org.example.final_project.report.repository.ReportRepository;
import org.example.final_project.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }


    public void reportComment(User reported, User reporter, Comment comment) {
        Report report = Report.builder()
                .reported(reported)
                .reporter(reporter)
                .comment(comment)
                .reportTime(LocalDateTime.now())
                .resolved(false)
                .build();

        this.reportRepository.save(report);
        log.info("Report created! User %s reported %s for comment with id [%s]".formatted(reporter.getUsername(), reported.getUsername(), report.getComment().getId()));
    }

    public List<Report> findAll() {
        return this.reportRepository.findAllByResolvedOrderByReportTimeDesc(false);
    }

    public void resolve(Report report) {
        Optional<Report> reportOptional = this.reportRepository.findById(report.getId());

        if (reportOptional.isEmpty()) {
            throw new ReportNotFoundException("Report with id [" + report.getId() + "] not found");
        }

        report.setResolved(true);
        this.reportRepository.save(report);
        log.info("Resolved report with id [%s]".formatted(report.getId()));
    }

    public Optional<Report> findById(UUID reportId) {
        return this.reportRepository.findById(reportId);
    }
}
