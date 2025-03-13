package com.example.demo.module.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRoleRequestDTO {

    private Long pageNumber;
    private Long pageSize;
    private String code;
    private Integer status;
    private Instant createdAtFrom;
    private Instant createdAtTo;

}
