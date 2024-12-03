/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.replacer.spi;

import java.util.Properties;

public interface ConfigReplacer {
    public void init(Properties var1);

    public String getPrefix();

    public String getReplacement(String var1);
}

