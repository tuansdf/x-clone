package com.example.demo.module.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchUserRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String username;
    private String email;
    private Integer status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
