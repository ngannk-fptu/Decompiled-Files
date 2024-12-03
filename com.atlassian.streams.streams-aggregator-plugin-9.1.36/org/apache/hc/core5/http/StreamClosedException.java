/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.io.IOException;

public class StreamClosedException
extends IOException {
    private static final long serialVersionUID = 1L;

    public StreamClosedException() {
        super("Stream already closed");
    }

    public StreamClosedException(String message) {
        super(message);
    }
}

