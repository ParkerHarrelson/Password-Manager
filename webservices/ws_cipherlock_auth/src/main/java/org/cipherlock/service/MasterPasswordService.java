package org.cipherlock.service;

import lombok.AllArgsConstructor;
import org.cipherlock.dto.ResponseDTO.PasswordValidationResponseDTO;
import org.cipherlock.utils.PasswordUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MasterPasswordService {

    public Integer checkPasswordStrength(String password) {

        return 0;
    }

    /**
     * This method validates the password against our preset password rules.
     *
     * @param password The supplied password to be checked
     * @return The result of the validation check.
     */
    public PasswordValidationResponseDTO validatePassword(String password) {
        return PasswordUtils.validatePassword(password);
    }

    /**
     * This method utilizes crypto library to generate strong password for the user
     *
     * @return The generated password.
     */
    public String generatePassword() {
        return PasswordUtils.generateStrongPassword();
    }
}
