/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.upm.api.util.Option;
import java.util.ArrayList;
import java.util.List;

public abstract class SysCommon {
    public static final String PLUGIN_KEY_LIST_IGNORE_VALUE = "-";
    public static final String ATLASSIAN_CONNECT_XML_BUNDLE_ATTRIBUTE = "Remote-Plugin";
    public static final String ATLASSIAN_CONNECT_PLUGIN_KEY = "com.atlassian.plugins.atlassian-connect-plugin";

    public static Option<List<String>> getPluginKeysFromSysProp(String propKey) {
        return Option.option(System.getProperty(propKey)).flatMap(pluginKeys -> {
            if (pluginKeys.equals(PLUGIN_KEY_LIST_IGNORE_VALUE)) {
                return Option.none();
            }
            ArrayList<String> keys = new ArrayList<String>();
            for (String key : pluginKeys.split(",")) {
                keys.add(key.trim());
            }
            return Option.some(keys);
        });
    }
}

