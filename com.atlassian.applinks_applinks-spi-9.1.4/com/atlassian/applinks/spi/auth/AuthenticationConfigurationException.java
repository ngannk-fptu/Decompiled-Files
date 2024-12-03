/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.auth;

public class AuthenticationConfigurationException
extends Exception {
    public AuthenticationConfigurationException(String message) {
        super(message);
    }

    public AuthenticationConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationConfigurationException(Throwable cause) {
        super(cause);
    }
}

