/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.filter.mutable;

import java.util.Map;

public class KeyValuePairConfig {
    private Map<String, String[]> configs;

    public Map<String, String[]> getConfigs() {
        return this.configs;
    }

    public void setConfig(Map<String, String[]> configs) {
        this.configs = configs;
    }
}

