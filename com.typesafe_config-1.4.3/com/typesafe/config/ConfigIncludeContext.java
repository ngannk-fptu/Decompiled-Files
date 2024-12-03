/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigParseable;

public interface ConfigIncludeContext {
    public ConfigParseable relativeTo(String var1);

    public ConfigParseOptions parseOptions();

    public ConfigIncludeContext setParseOptions(ConfigParseOptions var1);
}

