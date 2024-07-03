package org.cipherlock.utils;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import org.cipherlock.dto.ResponseDTO.PasswordValidationResponseDTO;
import org.passay.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.cipherlock.constants.EncryptionConstants.SHA_512;
import static org.cipherlock.constants.PasswordRules.PASSWORD_VALIDATOR;
import static org.cipherlock.constants.PasswordRules.ZXCVBN;

/**
 * This class is responsible for everything password related. Functionality exists for hashing passwords,
 * comparing passwords, checking passwords against rules, checking passwords for strength, etc.
 */
public class PasswordUtils {

    /**
     * Hash a password with a salt using SHA-512.
     *
     * @param password the plain text password
     * @param salt the salt
     * @return the hashed password
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     */
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_512);

        digest.update(salt);
        byte[] hashedBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

        byte[] saltAndHash = new byte[salt.length + hashedBytes.length];
        System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
        System.arraycopy(hashedBytes, 0, saltAndHash, salt.length, hashedBytes.length);

        return Base64.getEncoder().encodeToString(saltAndHash);
    }

    /**
     * Verify a password against a stored hash and salt.
     *
     * @param password the plain text password
     * @param storedHash the stored hash with salt
     * @param salt the salt used in hashing
     * @return true if the password matches, false otherwise
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     */
    public static boolean verifyPassword(String password, String storedHash, byte[] salt) throws NoSuchAlgorithmException {
        String newHash = hashPassword(password, salt);
        return storedHash.equals(newHash);
    }

    /**
     * Check the strength of a password.
     *
     * @param password the password to check
     * @return the strength score (0-4) and feedback
     */
    public static Strength checkPasswordStrength(String password) {
        return ZXCVBN.measure(password);
    }

    /**
     * Check if a password follows the specified rules.
     *
     * @param password the password to check
     * @return a PasswordValidationResponseDTO object with the validation results
     */
    public static PasswordValidationResponseDTO validatePassword(String password) {
        RuleResult result = PASSWORD_VALIDATOR.validate(new PasswordData(password));

        return PasswordValidationResponseDTO.builder()
                .valid(result.isValid())
                .messages(PASSWORD_VALIDATOR.getMessages(result))
                .build();
    }

    /**
     * Generate a strong password that follows the specified rules.
     *
     * @return a strong password
     */
    public static String generateStrongPassword() {
        PasswordGenerator generator = new PasswordGenerator();
        List<CharacterRule> rules = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 2),
                new CharacterRule(EnglishCharacterData.LowerCase, 2),
                new CharacterRule(EnglishCharacterData.Digit, 2),
                new CharacterRule(EnglishCharacterData.Special, 2)
        );
        return generator.generatePassword(16, rules);
    }
}
