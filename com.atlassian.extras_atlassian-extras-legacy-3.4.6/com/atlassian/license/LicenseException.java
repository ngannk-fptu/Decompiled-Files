/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.license;

@Deprecated
public class LicenseException
extends Exception {
    public LicenseException(String message) {
        super(message);
    }

    public LicenseException(Throwable cause) {
        super(cause);
    }

    public LicenseException(String message, Throwable cause) {
        super(message, cause);
    }
}

