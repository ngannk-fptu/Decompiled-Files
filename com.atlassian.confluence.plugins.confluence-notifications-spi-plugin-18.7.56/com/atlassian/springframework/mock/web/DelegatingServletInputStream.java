/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  org.springframework.util.Assert
 */
package com.atlassian.springframework.mock.web;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import org.springframework.util.Assert;

public class DelegatingServletInputStream
extends ServletInputStream {
    private final InputStream sourceStream;

    public DelegatingServletInputStream(InputStream sourceStream) {
        Assert.notNull((Object)sourceStream, (String)"Source InputStream must not be null");
        this.sourceStream = sourceStream;
    }

    public final InputStream getSourceStream() {
        return this.sourceStream;
    }

    public int read() throws IOException {
        return this.sourceStream.read();
    }

    public void close() throws IOException {
        super.close();
        this.sourceStream.close();
    }

    public boolean isFinished() {
        try {
            return this.sourceStream.available() == 0;
        }
        catch (IOException e) {
            return true;
        }
    }

    public boolean isReady() {
        try {
            return this.sourceStream.available() > 0;
        }
        catch (IOException e) {
            return false;
        }
    }

    public void setReadListener(ReadListener readListener) {
    }
}

