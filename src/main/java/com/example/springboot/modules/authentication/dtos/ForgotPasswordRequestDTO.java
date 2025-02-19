package com.example.springboot.modules.authentication.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ForgotPasswordRequestDTO {

    private String email;

}
