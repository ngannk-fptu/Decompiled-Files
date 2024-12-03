/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.wan;

public class IllegalConfigurationException
extends RuntimeException {
    public IllegalConfigurationException() {
    }

    public IllegalConfigurationException(String message) {
        super(message);
    }

    public IllegalConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConfigurationException(Throwable cause) {
        super(cause);
    }
}

