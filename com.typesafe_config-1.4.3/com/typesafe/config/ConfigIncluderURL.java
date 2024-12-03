/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigObject;
import java.net.URL;

public interface ConfigIncluderURL {
    public ConfigObject includeURL(ConfigIncludeContext var1, URL var2);
}

