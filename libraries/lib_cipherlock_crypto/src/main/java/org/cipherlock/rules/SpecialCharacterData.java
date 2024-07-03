package org.cipherlock.rules;

import org.passay.CharacterData;

public class SpecialCharacterData implements CharacterData {
    public static final String ERROR_CODE = "INSUFFICIENT_SPECIAL";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_+=<>?";

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }

    @Override
    public String getCharacters() {
        return SPECIAL_CHARACTERS;
    }
}
