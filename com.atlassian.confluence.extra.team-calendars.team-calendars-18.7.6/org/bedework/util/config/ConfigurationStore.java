/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.util.List;
import java.util.ResourceBundle;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.config.ConfigException;

public interface ConfigurationStore {
    public boolean readOnly();

    public String getLocation() throws ConfigException;

    public void saveConfiguration(ConfigBase var1) throws ConfigException;

    public ConfigBase getConfig(String var1) throws ConfigException;

    public ConfigBase getConfig(String var1, Class var2) throws ConfigException;

    public List<String> getConfigs() throws ConfigException;

    public ConfigurationStore getStore(String var1) throws ConfigException;

    public ResourceBundle getResource(String var1, String var2) throws ConfigException;
}

