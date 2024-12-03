/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigParseOptions;

public interface ConfigParseable {
    public ConfigObject parse(ConfigParseOptions var1);

    public ConfigOrigin origin();

    public ConfigParseOptions options();
}

