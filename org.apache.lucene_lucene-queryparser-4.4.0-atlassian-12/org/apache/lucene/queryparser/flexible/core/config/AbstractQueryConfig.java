/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.config;

import java.util.HashMap;
import org.apache.lucene.queryparser.flexible.core.config.ConfigurationKey;

public abstract class AbstractQueryConfig {
    private final HashMap<ConfigurationKey<?>, Object> configMap = new HashMap();

    AbstractQueryConfig() {
    }

    public <T> T get(ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return (T)this.configMap.get(key);
    }

    public <T> boolean has(ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return this.configMap.containsKey(key);
    }

    public <T> void set(ConfigurationKey<T> key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        if (value == null) {
            this.unset(key);
        } else {
            this.configMap.put(key, value);
        }
    }

    public <T> boolean unset(ConfigurationKey<T> key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null!");
        }
        return this.configMap.remove(key) != null;
    }
}

