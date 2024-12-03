/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.codehaus.stax2.io.Stax2BlockSource;

public class Stax2StringSource
extends Stax2BlockSource {
    final String mText;

    public Stax2StringSource(String text) {
        this.mText = text;
    }

    @Override
    public Reader constructReader() throws IOException {
        return new StringReader(this.mText);
    }

    @Override
    public InputStream constructInputStream() throws IOException {
        return null;
    }

    public String getText() {
        return this.mText;
    }
}

