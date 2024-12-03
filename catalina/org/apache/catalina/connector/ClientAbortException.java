/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.coyote.BadRequestException
 */
package org.apache.catalina.connector;

import org.apache.coyote.BadRequestException;

public final class ClientAbortException
extends BadRequestException {
    private static final long serialVersionUID = 1L;

    public ClientAbortException() {
    }

    public ClientAbortException(String message) {
        super(message);
    }

    public ClientAbortException(Throwable throwable) {
        super(throwable);
    }

    public ClientAbortException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

