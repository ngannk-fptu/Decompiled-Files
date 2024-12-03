/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import java.util.Map;

public final class ConfigValueFactory {
    private ConfigValueFactory() {
    }

    public static ConfigValue fromAnyRef(Object object, String originDescription) {
        return ConfigImpl.fromAnyRef(object, originDescription);
    }

    public static ConfigObject fromMap(Map<String, ? extends Object> values, String originDescription) {
        return (ConfigObject)ConfigValueFactory.fromAnyRef(values, originDescription);
    }

    public static ConfigList fromIterable(Iterable<? extends Object> values, String originDescription) {
        return (ConfigList)ConfigValueFactory.fromAnyRef(values, originDescription);
    }

    public static ConfigValue fromAnyRef(Object object) {
        return ConfigValueFactory.fromAnyRef(object, null);
    }

    public static ConfigObject fromMap(Map<String, ? extends Object> values) {
        return ConfigValueFactory.fromMap(values, null);
    }

    public static ConfigList fromIterable(Iterable<? extends Object> values) {
        return ConfigValueFactory.fromIterable(values, null);
    }
}

