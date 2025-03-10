package org.example.final_project.client;

import org.example.final_project.user.model.User;
import org.example.final_project.web.dto.BanNotification;
import org.example.final_project.web.dto.UnbanNotification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "email-service", url = "http://localhost:8081/api/v1")
public interface EmailServiceClient {

    @PostMapping("/send-notification")
    ResponseEntity<Void> sendBanNotification(@RequestBody BanNotification banNotification);

    @DeleteMapping("/unban-notification")
    ResponseEntity<Void> sendUnbanNotification(@RequestBody UnbanNotification unbanNotification);

    @GetMapping("/banned-users")
    List<User> getBannedUsers();

}
