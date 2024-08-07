package org.cipherlock.rules;

import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

@Slf4j
@Component
public final class PasswordRules {

    public final Zxcvbn zxcvbn;
    public final PasswordValidator passwordValidator;

    public PasswordRules() {
        this.zxcvbn = new Zxcvbn();

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            // Load the file from the resources directory
            inputStream = PasswordRules.class.getClassLoader().getResourceAsStream("dictionary.txt");
            if (inputStream == null) {
                throw new IOException("Dictionary file not found in resources.");
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            WordListDictionary dictionary = new WordListDictionary(WordLists.createFromReader(
                    new BufferedReader[]{reader},
                    false,
                    new ArraysSort()
            ));

            this.passwordValidator = new PasswordValidator(Arrays.asList(
                    new LengthRule(14, 128),
                    new CharacterRule(EnglishCharacterData.UpperCase, 1),
                    new CharacterRule(EnglishCharacterData.LowerCase, 1),
                    new CharacterRule(EnglishCharacterData.Digit, 1),
                    new CharacterRule(EnglishCharacterData.Special, 1),
                    new WhitespaceRule(),
                    new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                    new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                    new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                    new DictionaryRule(dictionary)
            ));
        } catch (IOException e) {
            log.error("Error reading dictionary for password rules: {}", e.getMessage());
            throw new RuntimeException("Failed to load dictionary file for password validation", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Error closing dictionary file reader: {}", e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error closing dictionary file input stream: {}", e.getMessage());
                }
            }
        }
    }
}
