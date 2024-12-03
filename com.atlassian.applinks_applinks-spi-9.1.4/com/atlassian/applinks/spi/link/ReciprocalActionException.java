/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.link;

public class ReciprocalActionException
extends Exception {
    public ReciprocalActionException(String message) {
        super(message);
    }

    public ReciprocalActionException(Throwable cause) {
        super(cause);
    }

    public ReciprocalActionException(String message, Throwable cause) {
        super(message, cause);
    }
}

