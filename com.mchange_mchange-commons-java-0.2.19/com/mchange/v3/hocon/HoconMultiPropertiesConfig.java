/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.typesafe.config.Config
 *  com.typesafe.config.ConfigList
 *  com.typesafe.config.ConfigValue
 *  com.typesafe.config.ConfigValueType
 */
package com.mchange.v3.hocon;

import com.mchange.v2.cfg.DelayedLogItem;
import com.mchange.v2.cfg.MultiPropertiesConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HoconMultiPropertiesConfig
extends MultiPropertiesConfig {
    String quasiResourcePath;
    Properties props;
    List<DelayedLogItem> delayedLogItems = new LinkedList<DelayedLogItem>();
    Map<String, Properties> propsByPrefix = new HashMap<String, Properties>();

    public HoconMultiPropertiesConfig(String string, Config config) {
        this.quasiResourcePath = string;
        this.props = this.propsForConfig(config);
    }

    private Properties propsForConfig(Config config) {
        Properties properties = new Properties();
        for (Map.Entry entry : config.entrySet()) {
            try {
                properties.put(entry.getKey(), HoconMultiPropertiesConfig.asSimpleString((ConfigValue)entry.getValue()));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                this.delayedLogItems.add(new DelayedLogItem(DelayedLogItem.Level.FINE, "For property '" + (String)entry.getKey() + "', " + illegalArgumentException.getMessage()));
            }
        }
        return properties;
    }

    private static String asSimpleString(ConfigValue configValue) throws IllegalArgumentException {
        ConfigValueType configValueType = configValue.valueType();
        switch (configValueType) {
            case BOOLEAN: 
            case NUMBER: 
            case STRING: {
                return String.valueOf(configValue.unwrapped());
            }
            case LIST: {
                ConfigList configList = (ConfigList)configValue;
                for (ConfigValue configValue2 : configList) {
                    if (HoconMultiPropertiesConfig.isSimple(configValue2)) continue;
                    throw new IllegalArgumentException("value is a complex list, could not be rendered as a simple property: " + configValue);
                }
                StringBuilder stringBuilder = new StringBuilder();
                int n = configList.size();
                for (int i = 0; i < n; ++i) {
                    if (i != 0) {
                        stringBuilder.append(',');
                    }
                    stringBuilder.append(HoconMultiPropertiesConfig.asSimpleString((ConfigValue)configList.get(i)));
                }
                return stringBuilder.toString();
            }
            case OBJECT: {
                throw new IllegalArgumentException("value is a ConfigValue object rather than an atom or list of atoms: " + configValue);
            }
            case NULL: {
                throw new IllegalArgumentException("value is a null; will be excluded from the MultiPropertiesConfig: " + configValue);
            }
        }
        throw new IllegalArgumentException("value of an unexpected type: (value->" + configValue + ", type->" + configValueType + ")");
    }

    private static boolean isSimple(ConfigValue configValue) {
        ConfigValueType configValueType = configValue.valueType();
        switch (configValueType) {
            case BOOLEAN: 
            case NUMBER: 
            case STRING: {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getPropertiesResourcePaths() {
        return new String[]{this.quasiResourcePath};
    }

    @Override
    public Properties getPropertiesByResourcePath(String string) {
        if (string.equals(this.quasiResourcePath)) {
            Properties properties = new Properties();
            properties.putAll((Map<?, ?>)this.props);
            return properties;
        }
        return null;
    }

    @Override
    public synchronized Properties getPropertiesByPrefix(String string) {
        Properties properties = this.propsByPrefix.get(string);
        if (properties == null) {
            properties = new Properties();
            if ("".equals(string)) {
                properties.putAll((Map<?, ?>)this.props);
            } else {
                String string2 = string + '.';
                for (Map.Entry<Object, Object> entry : this.props.entrySet()) {
                    String string3 = (String)entry.getKey();
                    if (!string3.startsWith(string2)) continue;
                    properties.put(string3, entry.getValue());
                }
            }
            this.propsByPrefix.put(string, properties);
        }
        return properties;
    }

    @Override
    public String getProperty(String string) {
        return (String)this.props.get(string);
    }

    @Override
    public List getDelayedLogItems() {
        return this.delayedLogItems;
    }
}

