/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.jmx;

import org.bedework.util.config.ConfigBase;
import org.bedework.util.config.ConfigException;
import org.bedework.util.config.ConfigurationStore;

public interface ConfigHolder<T extends ConfigBase> {
    public String getConfigUri();

    public T getConfig();

    public void putConfig();

    public ConfigurationStore getStore() throws ConfigException;
}

