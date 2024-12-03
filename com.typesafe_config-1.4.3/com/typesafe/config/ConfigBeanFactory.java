/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.Config;
import com.typesafe.config.impl.ConfigBeanImpl;

public class ConfigBeanFactory {
    public static <T> T create(Config config, Class<T> clazz) {
        return ConfigBeanImpl.createInternal(config, clazz);
    }
}

