/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigValue;

public interface ConfigResolver {
    public ConfigValue lookup(String var1);

    public ConfigResolver withFallback(ConfigResolver var1);
}

