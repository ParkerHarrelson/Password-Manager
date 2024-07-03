package org.cipherlock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserPasswordSecretDTO {

    private String password;
    private String secretKey;
    private String salt;
}
