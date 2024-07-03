package org.cipherlock.service;

import com.nulabinc.zxcvbn.Strength;
import lombok.AllArgsConstructor;
import org.cipherlock.dto.ResponseDTO.PasswordValidationResponseDTO;
import org.cipherlock.utils.PasswordUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MasterPasswordService {

    private final PasswordUtils passwordUtils;

    public Integer checkPasswordStrength(String password) {
        Strength strength = passwordUtils.checkPasswordStrength(password);
        return strength.getScore();
    }

    /**
     * This method validates the password against our preset password rules.
     *
     * @param password The supplied password to be checked
     * @return The result of the validation check.
     */
    public PasswordValidationResponseDTO validatePassword(String password) {
        return passwordUtils.validatePassword(password);
    }

    /**
     * This method utilizes crypto library to generate strong password for the user
     *
     * @return The generated password.
     */
    public String generatePassword() {
        return passwordUtils.generateStrongPassword();
    }
}
