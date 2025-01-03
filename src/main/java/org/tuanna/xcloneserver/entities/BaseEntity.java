package org.tuanna.xcloneserver.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BaseEntity implements Serializable {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    private void prePersist() {
        ZonedDateTime now = ZonedDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (id == null) {
            id = UUIDUtils.generateId();
        }
    }

    @PreUpdate
    private void preUpdate() {
        ZonedDateTime now = ZonedDateTime.now();
        updatedAt = now;
    }

}
