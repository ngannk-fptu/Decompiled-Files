/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.lob;

import java.io.IOException;
import java.io.Reader;

@Deprecated
public class ReaderInputStream
extends org.hibernate.engine.jdbc.ReaderInputStream {
    public ReaderInputStream(Reader reader) {
        super(reader);
    }

    @Override
    public int read() throws IOException {
        return super.read();
    }
}

