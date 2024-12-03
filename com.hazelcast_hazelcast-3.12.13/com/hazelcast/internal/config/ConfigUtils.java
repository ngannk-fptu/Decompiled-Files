/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigPatternMatcher;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.partition.strategy.StringPartitioningStrategy;
import com.hazelcast.util.function.BiConsumer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class ConfigUtils {
    private static final ILogger LOGGER = Logger.getLogger(Config.class);
    private static final BiConsumer<NamedConfig, String> DEFAULT_NAME_SETTER = new BiConsumer<NamedConfig, String>(){

        @Override
        public void accept(NamedConfig namedConfig, String name) {
            namedConfig.setName(name);
        }
    };

    private ConfigUtils() {
    }

    public static <T> T lookupByPattern(ConfigPatternMatcher configPatternMatcher, Map<String, T> configPatterns, String itemName) {
        T candidate = configPatterns.get(itemName);
        if (candidate != null) {
            return candidate;
        }
        String configPatternKey = configPatternMatcher.matches(configPatterns.keySet(), itemName);
        if (configPatternKey != null) {
            return configPatterns.get(configPatternKey);
        }
        if (!"default".equals(itemName) && !itemName.startsWith("hz:")) {
            LOGGER.finest("No configuration found for " + itemName + ", using default config!");
        }
        return null;
    }

    public static <T extends NamedConfig> T getConfig(ConfigPatternMatcher configPatternMatcher, Map<String, T> configs, String name, Class clazz) {
        return (T)ConfigUtils.getConfig(configPatternMatcher, configs, name, clazz, DEFAULT_NAME_SETTER);
    }

    public static <T> T getConfig(ConfigPatternMatcher configPatternMatcher, Map<String, T> configs, String name, Class clazz, BiConsumer<T, String> nameSetter) {
        T config = ConfigUtils.lookupByPattern(configPatternMatcher, configs, name = StringPartitioningStrategy.getBaseName(name));
        if (config != null) {
            return config;
        }
        T defConfig = configs.get("default");
        try {
            if (defConfig == null) {
                Constructor constructor = clazz.getDeclaredConstructor(new Class[0]);
                constructor.setAccessible(true);
                defConfig = constructor.newInstance(new Object[0]);
                nameSetter.accept(defConfig, "default");
                configs.put("default", defConfig);
            }
            Constructor copyConstructor = clazz.getDeclaredConstructor(clazz);
            copyConstructor.setAccessible(true);
            config = copyConstructor.newInstance(defConfig);
            nameSetter.accept(config, name);
            configs.put(name, config);
            return config;
        }
        catch (NoSuchMethodException e) {
            LOGGER.severe("Could not create class " + clazz.getName());
            assert (false);
            return null;
        }
        catch (InstantiationException e) {
            LOGGER.severe("Could not create class " + clazz.getName());
            assert (false);
            return null;
        }
        catch (IllegalAccessException e) {
            LOGGER.severe("Could not create class " + clazz.getName());
            assert (false);
            return null;
        }
        catch (InvocationTargetException e) {
            LOGGER.severe("Could not create class " + clazz.getName());
            assert (false);
            return null;
        }
    }
}

