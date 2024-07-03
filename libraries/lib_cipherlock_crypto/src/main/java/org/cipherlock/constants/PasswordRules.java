package org.cipherlock.constants;

import com.nulabinc.zxcvbn.Zxcvbn;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.passay.dictionary.WordListDictionary;
import org.passay.dictionary.WordLists;
import org.passay.dictionary.sort.ArraysSort;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public final class PasswordRules {

    public static final Zxcvbn ZXCVBN = new Zxcvbn();
    public static final PasswordValidator PASSWORD_VALIDATOR;

    static {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src/main/resources/dictionary.txt"));
            WordListDictionary dictionary = new WordListDictionary(WordLists.createFromReader(
                    new BufferedReader[] { reader },
                    false,
                    new ArraysSort()
            ));

            PASSWORD_VALIDATOR = new PasswordValidator(Arrays.asList(
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
        }
    }

    private PasswordRules() {
    }
}
