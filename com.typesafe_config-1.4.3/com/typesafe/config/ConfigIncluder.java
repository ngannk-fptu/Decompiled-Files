/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigObject;

public interface ConfigIncluder {
    public ConfigIncluder withFallback(ConfigIncluder var1);

    public ConfigObject include(ConfigIncludeContext var1, String var2);
}

