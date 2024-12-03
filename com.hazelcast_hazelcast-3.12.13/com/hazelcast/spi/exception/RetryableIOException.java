/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.exception;

import com.hazelcast.spi.exception.RetryableException;
import java.io.IOException;

public class RetryableIOException
extends IOException
implements RetryableException {
    public RetryableIOException() {
    }

    public RetryableIOException(String message) {
        super(message);
    }

    public RetryableIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryableIOException(Throwable cause) {
        super(cause);
    }
}

