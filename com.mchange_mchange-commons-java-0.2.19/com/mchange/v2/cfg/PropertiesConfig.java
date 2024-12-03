/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import java.util.Properties;

public interface PropertiesConfig {
    public Properties getPropertiesByPrefix(String var1);

    public String getProperty(String var1);
}

