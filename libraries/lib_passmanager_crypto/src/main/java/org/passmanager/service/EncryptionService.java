package org.passmanager.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;

import static org.passmanager.constants.EncryptionConstants.RCON;
import static org.passmanager.constants.EncryptionConstants.SBOX;

/**
 * Service class for encrypting plaintext into cipher text using
 * the AES encryption algorithm. Plaintext and Key are provided and
 * a cipher text string is returned.
 */
@Service
public class EncryptionService {

    public String encrypt(String plaintext, String key) {
        byte[] paddedPlaintext = pad(plaintext.getBytes());
        byte[] keyBytes = prepareKey(key);
        byte[][] roundKeys = keyExpansion(keyBytes);

        byte[] encrypted = new byte[paddedPlaintext.length];

        for (int i = 0; i < paddedPlaintext.length; i += 16) {
            byte[] block = Arrays.copyOfRange(paddedPlaintext, i, i + 16);
            byte[] encryptedBlock = encryptBlock(block, roundKeys);
            System.arraycopy(encryptedBlock, 0, encrypted, i, 16);
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static byte[] prepareKey(String key) {
        byte[] keyBytes = key.getBytes();
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32); // Pad with zeros
        }
        return keyBytes;
    }

    private byte[][] keyExpansion(byte[] key) {
        int Nk = key.length / 4;  // Number of 32-bit words in the key
        int Nr = Nk + 6; // Number of rounds
        int Nb = 4;  // Number of columns (32-bit words) comprising the State

        if (key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException(STR."Invalid key length: \{key.length}");
        }

        // Create the round keys array. Each round key is a flat array of 16 bytes.
        byte[][] roundKeys = new byte[Nr + 1][Nb * 4];

        // Initialize the first Nk words with the cipher key
        for (int i = 0; i < Nk; i++) {
            roundKeys[i / Nb][4 * (i % Nb)] = key[4 * i];
            roundKeys[i / Nb][4 * (i % Nb) + 1] = key[4 * i + 1];
            roundKeys[i / Nb][4 * (i % Nb) + 2] = key[4 * i + 2];
            roundKeys[i / Nb][4 * (i % Nb) + 3] = key[4 * i + 3];
        }

        byte[] temp = new byte[4];

        for (int i = Nk; i < Nb * (Nr + 1); i++) {
            temp = Arrays.copyOfRange(roundKeys[(i - 1) / Nb], 4 * ((i - 1) % Nb), 4 * ((i - 1) % Nb) + 4);

            if (i % Nk == 0) {
                subWord(rotWord(temp));
                for (int j = 0; j < 4; j++) {
                    temp[j] ^= (byte) RCON[i / Nk][j];
                }
            } else if (Nk > 6 && i % Nk == 4) {
                subWord(temp);
            }

            for (int j = 0; j < 4; j++) {
                roundKeys[i / Nb][4 * (i % Nb) + j] = (byte) (roundKeys[(i - Nk) / Nb][4 * ((i - Nk) % Nb) + j] ^ temp[j]);
            }
        }

        // Flatten the round keys array
        byte[][] flatRoundKeys = new byte[Nr + 1][Nb * 4];
        for (int i = 0; i < flatRoundKeys.length; i++) {
            System.arraycopy(roundKeys[i], 0, flatRoundKeys[i], 0, Nb * 4);
        }

        return flatRoundKeys;
    }


    private void subWord(byte[] word) {
        for (int i = 0; i < word.length; i++) {
            word[i] = (byte) SBOX[word[i] & 0xFF];
        }
    }

    private byte[] rotWord(byte[] word) {
        byte temp = word[0];

        for (int i = 0; i < word.length - 1; i++) {
            word[i] = word[i + 1];
        }

        word[word.length - 1] = temp;
        return word;
    }

    private void subBytes(byte[] state) {
        for (int i = 0; i < state.length; i++) {
            state[i] = (byte) SBOX[state[i] & 0xFF];
        }
    }

    private byte[] shiftRows(byte[] state) {
        byte[] shifted = new byte[state.length];

        shifted[0] = state[0];
        shifted[4] = state[4];
        shifted[8] = state[8];
        shifted[12] = state[12];

        shifted[1] = state[5];
        shifted[5] = state[9];
        shifted[9] = state[13];
        shifted[13] = state[1];

        shifted[2] = state[10];
        shifted[6] = state[14];
        shifted[10] = state[2];
        shifted[14] = state[6];

        shifted[3] = state[15];
        shifted[7] = state[3];
        shifted[11] = state[7];
        shifted[15] = state[11];

        return shifted;
    }

    private byte[] mixColumns(byte[] state) {
        byte[] mixed = new byte[state.length];

        for (int i = 0; i < 4; i++) {
            int col = i * 4;
            mixed[col] = (byte) (mul(0x02, state[col]) ^ mul(0x03, state[col + 1]) ^ state[col + 2] ^ state[col + 3]);
            mixed[col + 1] = (byte) (state[col] ^ mul(0x02, state[col + 1]) ^ mul(0x03, state[col + 2]) ^ state[col + 3]);
            mixed[col + 2] = (byte) (state[col] ^ state[col + 1] ^ mul(0x02, state[col + 2]) ^ mul(0x03, state[col + 3]));
            mixed[col + 3] = (byte) (mul(0x03, state[col]) ^ state[col + 1] ^ state[col + 2] ^ mul(0x02, state[col + 3]));
        }

        return mixed;
    }

    private int mul(int a, int b) {
        int p = 0;
        int hiBitSet;
        for (int counter = 0; counter < 8; counter++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            hiBitSet = (a & 0x80);
            a <<= 1;
            if (hiBitSet != 0) {
                a ^= 0x1b;
            }
            b >>= 1;
        }
        return p;
    }

    private byte[] addRoundKey(byte[] state, byte[] roundKey) {
        byte[] result = new byte[state.length];
        for (int i = 0; i < state.length; i++) {
            result[i] = (byte) (state[i] ^ roundKey[i]);
        }
        return result;
    }


    private byte[] encryptBlock(byte[] block, byte[][] roundKeys) {
        byte[] state = block.clone();

        state = addRoundKey(state, roundKeys[0]);

        for (int round = 1; round < roundKeys.length - 1; round++) {
            subBytes(state);
            state = shiftRows(state);
            state = mixColumns(state);
            state = addRoundKey(state, roundKeys[round]);
        }

        subBytes(state);
        state = shiftRows(state);
        state = addRoundKey(state, roundKeys[roundKeys.length - 1]);

        return state;
    }

    private byte[] pad(byte[] input) {
        int padding = 16 - (input.length % 16);
        byte[] padded = Arrays.copyOf(input, input.length + padding);
        for (int i = input.length; i < padded.length; i++) {
            padded[i] = (byte) padding;
        }
        return padded;
    }
}
