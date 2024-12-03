/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigObject;

public interface ConfigIncluderClasspath {
    public ConfigObject includeResources(ConfigIncludeContext var1, String var2);
}

