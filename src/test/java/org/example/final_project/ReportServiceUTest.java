package org.example.final_project;

import org.example.final_project.comment.model.Comment;
import org.example.final_project.exception.ReportNotFoundException;
import org.example.final_project.report.model.Report;
import org.example.final_project.report.repository.ReportRepository;
import org.example.final_project.report.service.ReportService;
import org.example.final_project.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceUTest {

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportService reportService;

    private Report report;

    @BeforeEach
    void setUp() {
        report = Report.builder()
                .id(UUID.randomUUID())
                .resolved(false)
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void testResolve_ReportExists() {
        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        reportService.resolve(report);

        assertTrue(report.isResolved());
        verify(reportRepository, times(1)).save(report);
        verify(reportRepository, times(1)).findById(report.getId());
    }

    @Test
    void testResolve_ReportNotFound() {
        when(reportRepository.findById(report.getId())).thenReturn(Optional.empty());

       assertThrows(ReportNotFoundException.class, () -> reportService.resolve(report));

        verify(reportRepository, times(1)).findById(report.getId());
        verify(reportRepository, times(0)).save(report);
    }

    @Test
    void findById_ShouldReturnReportIfExists() {

        when(reportRepository.findById(report.getId())).thenReturn(Optional.of(report));

        Optional<Report> result = reportService.findById(report.getId());

        assertTrue(result.isPresent());
        assertEquals(report, result.get());
        verify(reportRepository, times(1)).findById(report.getId());

    }

    @Test
    void reportComment_ShouldCreateAndSaveReport() {
        User reporter = User.builder()
                .id(UUID.randomUUID())
                .username("sstoew")
                .build();

        User reported = User.builder()
                .id(UUID.randomUUID())
                .username("reported")
                .build();

        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .build();

        reportService.reportComment(reported, reporter, comment);

        verify(reportRepository, times(1)).save(any(Report.class));

    }

    @Test
    void findAll_ShouldReturnAllReportsThatIsNotResolvedForOneWeek() {
        when(reportRepository.findAllByResolvedOrderByReportTimeDesc(false))
                .thenReturn(List.of(report));

        List<Report> result = reportService.findAll();

        verify(reportRepository, times(1)).findAllByResolvedOrderByReportTimeDesc(false);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

    }
}
