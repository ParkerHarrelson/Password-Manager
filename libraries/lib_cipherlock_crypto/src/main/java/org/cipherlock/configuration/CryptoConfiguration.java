package org.cipherlock.configuration;

import org.cipherlock.utils.AESUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfiguration {

    @Bean
    public AESUtils encryptionService() {
        return new AESUtils();
    }

}
