/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.parser;

import com.typesafe.config.ConfigValue;

public interface ConfigDocument {
    public ConfigDocument withValueText(String var1, String var2);

    public ConfigDocument withValue(String var1, ConfigValue var2);

    public ConfigDocument withoutPath(String var1);

    public boolean hasPath(String var1);

    public String render();
}

