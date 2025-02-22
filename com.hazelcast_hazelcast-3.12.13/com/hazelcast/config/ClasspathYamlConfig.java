/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.InputStream;
import java.util.Properties;

public class ClasspathYamlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(ClasspathYamlConfig.class);

    public ClasspathYamlConfig(String resource) {
        this(resource, System.getProperties());
    }

    public ClasspathYamlConfig(String resource, Properties properties) {
        this(Thread.currentThread().getContextClassLoader(), resource, properties);
    }

    public ClasspathYamlConfig(ClassLoader classLoader, String resource) {
        this(classLoader, resource, System.getProperties());
    }

    public ClasspathYamlConfig(ClassLoader classLoader, String resource, Properties properties) {
        Preconditions.checkTrue(classLoader != null, "classLoader can't be null");
        Preconditions.checkTrue(resource != null, "resource can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + resource + "'.");
        InputStream in = classLoader.getResourceAsStream(resource);
        Preconditions.checkTrue(in != null, "Specified resource '" + resource + "' could not be found!");
        new YamlConfigBuilder(in).setProperties(properties).build(this);
    }
}

