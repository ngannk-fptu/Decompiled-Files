/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValue;
import java.util.Map;

public interface ConfigObject
extends ConfigValue,
Map<String, ConfigValue> {
    public Config toConfig();

    @Override
    public Map<String, Object> unwrapped();

    @Override
    public ConfigObject withFallback(ConfigMergeable var1);

    @Override
    public ConfigValue get(Object var1);

    public ConfigObject withOnlyKey(String var1);

    public ConfigObject withoutKey(String var1);

    public ConfigObject withValue(String var1, ConfigValue var2);

    @Override
    public ConfigObject withOrigin(ConfigOrigin var1);
}

