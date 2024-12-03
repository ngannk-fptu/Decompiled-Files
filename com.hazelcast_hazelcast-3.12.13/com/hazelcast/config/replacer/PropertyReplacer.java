/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.replacer;

import com.hazelcast.config.replacer.spi.ConfigReplacer;
import java.util.Properties;

public class PropertyReplacer
implements ConfigReplacer {
    private Properties properties;

    @Override
    public void init(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getReplacement(String variable) {
        return this.properties.getProperty(variable);
    }
}

