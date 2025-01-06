package org.tuanna.xcloneserver.modules.role.dtos;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoleDTO {

    private Long id;
    private String code;
    private String name;
    private String status;
    private UUID updatedBy;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
