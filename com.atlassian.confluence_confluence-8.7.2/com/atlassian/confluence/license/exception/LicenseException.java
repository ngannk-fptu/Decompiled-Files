/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception;

public class LicenseException
extends RuntimeException {
    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }
}

