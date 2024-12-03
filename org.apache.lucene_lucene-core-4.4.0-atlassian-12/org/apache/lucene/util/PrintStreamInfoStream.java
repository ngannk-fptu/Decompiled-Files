/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.lucene.util.InfoStream;

public class PrintStreamInfoStream
extends InfoStream {
    private static final AtomicInteger MESSAGE_ID = new AtomicInteger();
    protected final int messageID;
    protected final PrintStream stream;

    public PrintStreamInfoStream(PrintStream stream) {
        this(stream, MESSAGE_ID.getAndIncrement());
    }

    public PrintStreamInfoStream(PrintStream stream, int messageID) {
        this.stream = stream;
        this.messageID = messageID;
    }

    @Override
    public void message(String component, String message) {
        this.stream.println(component + " " + this.messageID + " [" + new Date() + "; " + Thread.currentThread().getName() + "]: " + message);
    }

    @Override
    public boolean isEnabled(String component) {
        return true;
    }

    @Override
    public void close() throws IOException {
        if (!this.isSystemStream()) {
            this.stream.close();
        }
    }

    public boolean isSystemStream() {
        return this.stream == System.out || this.stream == System.err;
    }
}

