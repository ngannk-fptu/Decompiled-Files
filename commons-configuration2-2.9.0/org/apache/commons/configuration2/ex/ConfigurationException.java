/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.ex;

public class ConfigurationException
extends Exception {
    private static final long serialVersionUID = -1316746661346991484L;

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

