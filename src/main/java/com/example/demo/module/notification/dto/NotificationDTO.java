package com.example.demo.module.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationDTO {

    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private String data;
    private String topic;
    private Integer retryCount;
    private Integer type;
    private Integer status;
    private Instant createdAt;
    private Instant updatedAt;

}
