/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.opensocial.spi;

public class PersonServiceException
extends RuntimeException {
    public PersonServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonServiceException(String message) {
        super(message);
    }

    public PersonServiceException(Throwable cause) {
        super(cause);
    }
}

