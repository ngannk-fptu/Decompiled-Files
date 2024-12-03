/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigValue;
import java.util.List;

public interface ConfigList
extends List<ConfigValue>,
ConfigValue {
    @Override
    public List<Object> unwrapped();

    @Override
    public ConfigList withOrigin(ConfigOrigin var1);
}

