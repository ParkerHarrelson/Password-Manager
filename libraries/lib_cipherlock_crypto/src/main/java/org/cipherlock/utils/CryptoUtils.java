package org.cipherlock.utils;

import org.cipherlock.dto.UserPasswordSecretDTO;
import org.cipherlock.dto.RecoveryKeyDTO;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.cipherlock.constants.EncryptionConstants.*;

public class CryptoUtils {

    /**
     * This method is a general method for generating a random strong salt. This
     * salt can be used with passwords, recovery key, etc.
     *
     * @return The salt bytes
     * @throws NoSuchAlgorithmException if the specified secure random instance is invalid
     */
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // TODO: method for generating a recovery key. Generation of secret key will be client side

    /**
     * This method is for deriving a symmetric key from the user's password and salt and
     * secret key using PBKDF2
     *
     * @param userPasswordSecret Object containing the user's password, secret key, and salt
     * @return The symmetric key derived from the password and secret key combination
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws InvalidKeySpecException if the specified key specification is invalid
     */
    public static String deriveSymmetricKey(UserPasswordSecretDTO userPasswordSecret)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        if (userPasswordSecret == null || userPasswordSecret.getPassword() == null ||
                userPasswordSecret.getSalt() == null || userPasswordSecret.getSecretKey() == null) {
            throw new IllegalArgumentException("Invalid input: password, salt, and secret key must not be null");
        }

        char[] password = userPasswordSecret.getPassword().toCharArray();
        byte[] salt = userPasswordSecret.getSalt().getBytes(StandardCharsets.UTF_8);
        byte[] secretKey = userPasswordSecret.getSecretKey().getBytes(StandardCharsets.UTF_8);

        byte[] saltAndKey = new byte[salt.length + secretKey.length];
        System.arraycopy(salt, 0, saltAndKey, 0, salt.length);
        System.arraycopy(secretKey, 0, saltAndKey, salt.length, secretKey.length);

        PBEKeySpec spec = new PBEKeySpec(password, saltAndKey, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_INSTANCE);
        byte[] derivedKey = keyFactory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(derivedKey);
    }

    /**
     * This method is for deriving a symmetric key from the recovery key and salt using PBKDF2
     *
     * @param recoveryKeyDTO Object containing the recovery key and salt
     * @return The symmetric key derived from the recovery key and salt
     * @throws NoSuchAlgorithmException if the specified algorithm is not available
     * @throws InvalidKeySpecException if the specified key specification is invalid
     */
    public static String deriveSymmetricKey(RecoveryKeyDTO recoveryKeyDTO)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        if (recoveryKeyDTO == null || recoveryKeyDTO.getRecoveryKey() == null ||
                recoveryKeyDTO.getSalt() == null) {
            throw new IllegalArgumentException("Invalid input: recovery key and salt must not be null");
        }

        char[] recoveryKey = recoveryKeyDTO.getRecoveryKey().toCharArray();
        byte[] salt = recoveryKeyDTO.getSalt().getBytes(StandardCharsets.UTF_8);

        PBEKeySpec spec = new PBEKeySpec(recoveryKey, salt, PBKDF2_ITERATIONS, PBKDF2_KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF2_INSTANCE);
        byte[] derivedKey = keyFactory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(derivedKey);
    }

}
