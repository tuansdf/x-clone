package com.example.springboot.modules.role.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchRoleRequestDTO {

    private Integer pageNumber;
    private Integer pageSize;
    private String code;
    private String status;
    private OffsetDateTime createdAtFrom;
    private OffsetDateTime createdAtTo;

}
