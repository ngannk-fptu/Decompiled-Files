/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class SanitizedXmlFilterReader
extends FilterReader {
    private static final int HORIZONTAL_TAB_ASCII_CODE = 9;
    private static final int LINE_FEED_ASCII_CODE = 10;
    private static final int CARRIAGE_RETURN_ASCII_CODE = 13;
    private static final int MIN_SPECIAL_ASCII_CHARACTER_CODE = 0;
    private static final int MAX_SPECIAL_ASCII_CHARACTER_CODE = 31;
    private static final char SYMBOL_TO_REPLACE = '?';
    private static final Set<Integer> ALLOWED_SPECIAL_ASCII_CHARACTERS = Set.of(Integer.valueOf(9), Integer.valueOf(10), Integer.valueOf(13));
    private static final Set<Integer> CHARACTERS_TO_FILTER_OUT_ON_RESTORE = SanitizedXmlFilterReader.generateIllegalCharacters();

    public SanitizedXmlFilterReader(InputStreamReader xmlInputStreamReader) {
        super(xmlInputStreamReader);
    }

    @Override
    public int read() throws IOException {
        int read = super.read();
        return CHARACTERS_TO_FILTER_OUT_ON_RESTORE.contains(read) ? 63 : read;
    }

    @Override
    public int read(char[] buffer, int offset, int length) throws IOException {
        int readCount = super.read(buffer, offset, length);
        if (readCount == -1) {
            return -1;
        }
        for (int i = offset; i < offset + readCount; ++i) {
            if (!CHARACTERS_TO_FILTER_OUT_ON_RESTORE.contains(buffer[i])) continue;
            buffer[i] = 63;
        }
        return readCount;
    }

    private static Set<Integer> generateIllegalCharacters() {
        HashSet<Integer> illegalCharacters = new HashSet<Integer>();
        for (int i = 0; i <= 31; ++i) {
            if (ALLOWED_SPECIAL_ASCII_CHARACTERS.contains(i)) continue;
            illegalCharacters.add(i);
        }
        illegalCharacters.add(65535);
        return illegalCharacters;
    }
}

