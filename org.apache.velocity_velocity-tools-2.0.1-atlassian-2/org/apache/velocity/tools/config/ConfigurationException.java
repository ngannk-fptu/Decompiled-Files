/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.Data;

public class ConfigurationException
extends RuntimeException {
    private final Object source;

    public ConfigurationException(Data data, Throwable cause) {
        super(cause);
        this.source = data;
    }

    public ConfigurationException(Data data, String message) {
        super(message);
        this.source = data;
    }

    public ConfigurationException(Data data, String message, Throwable cause) {
        super(message, cause);
        this.source = data;
    }

    public ConfigurationException(Configuration config, Throwable cause) {
        super(cause);
        this.source = config;
    }

    public ConfigurationException(Configuration config, String message) {
        super(message);
        this.source = config;
    }

    public ConfigurationException(Configuration config, String message, Throwable cause) {
        super(message, cause);
        this.source = config;
    }

    public Object getSource() {
        return this.source;
    }
}

