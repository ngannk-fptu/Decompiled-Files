/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.Priority
 */
package com.atlassian.confluence.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class LoggingOutputStream
extends OutputStream {
    private static final int DEFAULT_BUFFER_LENGTH = 2048;
    private boolean closed = false;
    private byte[] buf;
    private int count;
    private int curBufLength;
    private final Logger log;
    private final Level level;

    public LoggingOutputStream(@Nonnull Logger log, @Nonnull Level level) {
        Objects.requireNonNull(log, "'log' must not be null");
        Objects.requireNonNull(level, "'level' must not be null");
        this.log = log;
        this.level = level;
        this.curBufLength = 2048;
        this.buf = new byte[this.curBufLength];
        this.count = 0;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.closed) {
            throw new IOException("The stream has been closed.");
        }
        if (b == 0) {
            return;
        }
        if (this.count == this.curBufLength) {
            int newBufLength = this.curBufLength + 2048;
            byte[] newBuf = new byte[newBufLength];
            System.arraycopy(this.buf, 0, newBuf, 0, this.curBufLength);
            this.buf = newBuf;
            this.curBufLength = newBufLength;
        }
        this.buf[this.count] = (byte)b;
        ++this.count;
    }

    @Override
    public void flush() {
        if (this.count == 0) {
            return;
        }
        byte[] bytes = new byte[this.count];
        System.arraycopy(this.buf, 0, bytes, 0, this.count);
        String processedLine = this.processLine(new String(bytes, StandardCharsets.UTF_8));
        if (processedLine != null) {
            this.log.log((Priority)this.level, (Object)processedLine);
        }
        this.count = 0;
    }

    @Override
    public void close() {
        this.flush();
        this.closed = true;
    }

    protected String processLine(String s) {
        return StringUtils.isNotBlank((CharSequence)s) ? StringUtils.strip((String)s) : null;
    }
}

