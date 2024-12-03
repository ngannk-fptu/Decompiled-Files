/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AlphabetMapper;

public class BasicAlphabetMapper
implements AlphabetMapper {
    private Map<Character, Integer> indexMap = new HashMap<Character, Integer>();
    private Map<Integer, Character> charMap = new HashMap<Integer, Character>();

    public BasicAlphabetMapper(String alphabet) {
        this(alphabet.toCharArray());
    }

    public BasicAlphabetMapper(char[] alphabet) {
        for (int i = 0; i != alphabet.length; ++i) {
            if (this.indexMap.containsKey(Character.valueOf(alphabet[i]))) {
                throw new IllegalArgumentException("duplicate key detected in alphabet: " + alphabet[i]);
            }
            this.indexMap.put(Character.valueOf(alphabet[i]), i);
            this.charMap.put(i, Character.valueOf(alphabet[i]));
        }
    }

    @Override
    public int getRadix() {
        return this.indexMap.size();
    }

    @Override
    public byte[] convertToIndexes(char[] input) {
        byte[] out;
        if (this.indexMap.size() <= 256) {
            out = new byte[input.length];
            for (int i = 0; i != input.length; ++i) {
                out[i] = this.indexMap.get(Character.valueOf(input[i])).byteValue();
            }
        } else {
            out = new byte[input.length * 2];
            for (int i = 0; i != input.length; ++i) {
                int idx = this.indexMap.get(Character.valueOf(input[i]));
                out[i * 2] = (byte)(idx >> 8 & 0xFF);
                out[i * 2 + 1] = (byte)(idx & 0xFF);
            }
        }
        return out;
    }

    @Override
    public char[] convertToChars(byte[] input) {
        char[] out;
        if (this.charMap.size() <= 256) {
            out = new char[input.length];
            for (int i = 0; i != input.length; ++i) {
                out[i] = this.charMap.get(input[i] & 0xFF).charValue();
            }
        } else {
            if ((input.length & 1) != 0) {
                throw new IllegalArgumentException("two byte radix and input string odd length");
            }
            out = new char[input.length / 2];
            for (int i = 0; i != input.length; i += 2) {
                out[i / 2] = this.charMap.get(input[i] << 8 & 0xFF00 | input[i + 1] & 0xFF).charValue();
            }
        }
        return out;
    }
}

