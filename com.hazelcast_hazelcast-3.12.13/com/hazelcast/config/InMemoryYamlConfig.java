/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.util.Properties;

public class InMemoryYamlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(InMemoryYamlConfig.class);

    public InMemoryYamlConfig(String yaml) {
        this(yaml, System.getProperties());
    }

    public InMemoryYamlConfig(String yaml, Properties properties) {
        LOGGER.info("Configuring Hazelcast from 'in-memory YAML'.");
        if (StringUtil.isNullOrEmptyAfterTrim(yaml)) {
            throw new IllegalArgumentException("YAML configuration is null or empty! Please use a well-structured YAML.");
        }
        Preconditions.checkTrue(properties != null, "properties can't be null");
        ByteArrayInputStream in = new ByteArrayInputStream(StringUtil.stringToBytes(yaml));
        new YamlConfigBuilder(in).setProperties(properties).build(this);
    }
}

