/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.core.HazelcastException;

public class ConfigurationException
extends HazelcastException {
    public ConfigurationException(String itemName, String candidate, String duplicate) {
        super(String.format("Found ambiguous configurations for item \"%s\": \"%s\" vs. \"%s\"%nPlease specify your configuration.", itemName, candidate, duplicate));
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}

