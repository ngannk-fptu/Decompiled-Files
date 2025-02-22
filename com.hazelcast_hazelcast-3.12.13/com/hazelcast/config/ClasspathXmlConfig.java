/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.InputStream;
import java.util.Properties;

public class ClasspathXmlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(ClasspathXmlConfig.class);

    public ClasspathXmlConfig(String resource) {
        this(resource, System.getProperties());
    }

    public ClasspathXmlConfig(String resource, Properties properties) {
        this(Thread.currentThread().getContextClassLoader(), resource, properties);
    }

    public ClasspathXmlConfig(ClassLoader classLoader, String resource) {
        this(classLoader, resource, System.getProperties());
    }

    public ClasspathXmlConfig(ClassLoader classLoader, String resource, Properties properties) {
        Preconditions.checkTrue(classLoader != null, "classLoader can't be null");
        Preconditions.checkTrue(resource != null, "resource can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + resource + "'.");
        InputStream in = classLoader.getResourceAsStream(resource);
        Preconditions.checkTrue(in != null, "Specified resource '" + resource + "' could not be found!");
        new XmlConfigBuilder(in).setProperties(properties).build(this);
    }
}

