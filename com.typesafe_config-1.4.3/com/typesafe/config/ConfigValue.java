/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValueType;

public interface ConfigValue
extends ConfigMergeable {
    public ConfigOrigin origin();

    public ConfigValueType valueType();

    public Object unwrapped();

    public String render();

    public String render(ConfigRenderOptions var1);

    @Override
    public ConfigValue withFallback(ConfigMergeable var1);

    public Config atPath(String var1);

    public Config atKey(String var1);

    public ConfigValue withOrigin(ConfigOrigin var1);
}

