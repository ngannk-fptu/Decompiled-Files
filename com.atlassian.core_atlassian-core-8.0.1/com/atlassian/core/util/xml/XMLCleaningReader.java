/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.core.util.xml;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLCleaningReader
extends FilterReader {
    private static final Logger log = LoggerFactory.getLogger(XMLCleaningReader.class);

    public XMLCleaningReader(Reader reader) {
        super(reader);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int charsRead = super.read(cbuf, off, len);
        if (charsRead > -1) {
            int limit = charsRead + off;
            for (int j = off; j < limit; ++j) {
                char c = cbuf[j];
                if (c <= '\uffffffff' || c == '\t' || c == '\n' || c == '\r' || c >= ' ' && (c <= '\ud7ff' || c >= '\ue000')) continue;
                log.warn("Replaced invalid XML character " + c + " (" + c + ").");
                cbuf[j] = 65533;
            }
        }
        return charsRead;
    }

    @Override
    public int read() throws IOException {
        int i = super.read();
        if (i < 32 && i > -1 && i != 9 && i != 10 && i != 13) {
            return 65533;
        }
        return i;
    }
}

