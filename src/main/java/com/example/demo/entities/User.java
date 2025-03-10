package com.example.demo.entities;

import com.example.demo.constants.ResultSetName;
import com.example.demo.modules.user.dtos.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "_user")
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
                @ColumnResult(name = "email", type = String.class),
                @ColumnResult(name = "name", type = String.class),
                @ColumnResult(name = "status", type = Integer.class),
                @ColumnResult(name = "created_by", type = UUID.class),
                @ColumnResult(name = "updated_by", type = UUID.class),
                @ColumnResult(name = "created_at", type = OffsetDateTime.class),
                @ColumnResult(name = "updated_at", type = OffsetDateTime.class),
                @ColumnResult(name = "roles", type = String.class),
                @ColumnResult(name = "permissions", type = String.class),
        })
})
@SqlResultSetMapping(name = ResultSetName.USER_SEARCH_CONTACT, classes = {
        @ConstructorResult(targetClass = UserDTO.class, columns = {
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "username", type = String.class),
        })
})
public class User extends AbstractEntity {

    @Column(name = "username", columnDefinition = "text", unique = true)
    private String username;
    @Column(name = "email", columnDefinition = "text", unique = true)
    private String email;
    @Column(name = "password", columnDefinition = "text")
    private String password;
    @Column(name = "otp_secret", columnDefinition = "text")
    private String otpSecret;
    @Column(name = "otp_enabled", columnDefinition = "text")
    private Boolean otpEnabled;
    @Column(name = "name", columnDefinition = "text")
    private String name;
    @Column(name = "status")
    private Integer status;
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by")
    private UUID updatedBy;

}
