/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletInputStream
 */
package com.atlassian.mywork.host.util;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;

public class DelegatingServletInputStream
extends ServletInputStream {
    private final InputStream sourceStream;

    public DelegatingServletInputStream(InputStream sourceStream) {
        this.sourceStream = sourceStream;
    }

    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }
}

