/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import java.io.IOException;

public class BadRequestException
extends IOException {
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(Throwable throwable) {
        super(throwable);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

