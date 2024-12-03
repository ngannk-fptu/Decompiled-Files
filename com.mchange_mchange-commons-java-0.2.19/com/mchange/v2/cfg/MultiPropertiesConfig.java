/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.cfg;

import com.mchange.v2.cfg.BasicMultiPropertiesConfig;
import com.mchange.v2.cfg.ConfigUtils;
import com.mchange.v2.cfg.PropertiesConfig;
import java.util.List;
import java.util.Properties;

public abstract class MultiPropertiesConfig
implements PropertiesConfig {
    private static String PROGRAMMATICALLY_SUPPLIED_PROPERTIES = "PROGRAMMATICALLY_SUPPLIED_PROPERTIES";

    public static MultiPropertiesConfig readVmConfig(String[] stringArray, String[] stringArray2) {
        return ConfigUtils.readVmConfig(stringArray, stringArray2);
    }

    public static MultiPropertiesConfig readVmConfig() {
        return ConfigUtils.readVmConfig();
    }

    public static MultiPropertiesConfig fromProperties(String string, Properties properties) {
        return new BasicMultiPropertiesConfig(string, properties);
    }

    public static MultiPropertiesConfig fromProperties(Properties properties) {
        return MultiPropertiesConfig.fromProperties(PROGRAMMATICALLY_SUPPLIED_PROPERTIES, properties);
    }

    public abstract String[] getPropertiesResourcePaths();

    public abstract Properties getPropertiesByResourcePath(String var1);

    @Override
    public abstract Properties getPropertiesByPrefix(String var1);

    @Override
    public abstract String getProperty(String var1);

    public abstract List getDelayedLogItems();
}

