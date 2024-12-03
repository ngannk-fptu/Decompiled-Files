/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigParseOptions;

public interface ConfigLoadingStrategy {
    public Config parseApplicationConfig(ConfigParseOptions var1);
}

