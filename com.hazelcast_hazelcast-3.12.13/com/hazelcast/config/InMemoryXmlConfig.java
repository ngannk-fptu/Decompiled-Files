/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.ByteArrayInputStream;
import java.util.Properties;

public class InMemoryXmlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(InMemoryXmlConfig.class);

    public InMemoryXmlConfig(String xml) {
        this(xml, System.getProperties());
    }

    public InMemoryXmlConfig(String xml, Properties properties) {
        LOGGER.info("Configuring Hazelcast from 'in-memory xml'.");
        if (StringUtil.isNullOrEmptyAfterTrim(xml)) {
            throw new IllegalArgumentException("XML configuration is null or empty! Please use a well-structured xml.");
        }
        Preconditions.checkTrue(properties != null, "properties can't be null");
        ByteArrayInputStream in = new ByteArrayInputStream(StringUtil.stringToBytes(xml));
        new XmlConfigBuilder(in).setProperties(properties).build(this);
    }
}

