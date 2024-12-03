/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

@Deprecated
public class SanitizedFilterReader
extends FilterReader {
    private final Set<Integer> characterCodesToSkip;

    protected SanitizedFilterReader(Reader in, Set<Integer> characterCodesToSkip) {
        super(in);
        this.characterCodesToSkip = characterCodesToSkip;
    }

    @Override
    public int read() throws IOException {
        int read;
        while (this.characterCodesToSkip.contains(read = super.read())) {
        }
        return read;
    }

    @Override
    public int read(char[] buffer, int offset, int length) throws IOException {
        int readCount = super.read(buffer, offset, length);
        if (readCount == -1) {
            return -1;
        }
        int pos = offset - 1;
        for (int i = offset; i < offset + readCount; ++i) {
            if (this.characterCodesToSkip.contains(buffer[i]) || ++pos >= i) continue;
            buffer[pos] = buffer[i];
        }
        return pos - offset + 1;
    }
}

