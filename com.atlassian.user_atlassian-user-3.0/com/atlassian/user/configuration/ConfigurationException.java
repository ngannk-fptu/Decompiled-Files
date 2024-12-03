/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.EntityException;

public class ConfigurationException
extends EntityException {
    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

