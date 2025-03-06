package org.example.final_project.report.service;

import org.example.final_project.comment.model.Comment;
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
    }

    public List<Report> findAll() {
        return this.reportRepository.findAllByResolvedOrderByReportTimeDesc(false);
    }

    public void resolve(Report report) {
        Optional<Report> reportOptional = this.reportRepository.findById(report.getId());

        if (reportOptional.isEmpty()) {
            throw new IllegalArgumentException("Report with id " + report.getId() + " not found");
        }

        report.setResolved(true);
        this.reportRepository.save(report);
    }

    public Report findById(UUID reportId) {
        return this.reportRepository.findById(reportId).orElse(null);
    }
}
