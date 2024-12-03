/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.env;

import java.util.Optional;

public interface EnvConfig {
    default public Optional<String> getValueFor(String name, String separator, String value, String environment) {
        return Optional.empty();
    }
}

