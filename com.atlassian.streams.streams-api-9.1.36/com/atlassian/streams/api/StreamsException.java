/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.api;

public class StreamsException
extends RuntimeException {
    public StreamsException() {
    }

    public StreamsException(String s) {
        super(s);
    }

    public StreamsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public StreamsException(Throwable throwable) {
        super(throwable);
    }
}

