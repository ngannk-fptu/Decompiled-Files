/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class UrlXmlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(UrlXmlConfig.class);

    public UrlXmlConfig(String url) throws IOException {
        this(new URL(url));
    }

    public UrlXmlConfig(String url, Properties properties) throws IOException {
        this(new URL(url), properties);
    }

    public UrlXmlConfig(URL url) throws IOException {
        this(url, System.getProperties());
    }

    public UrlXmlConfig(URL url, Properties properties) throws IOException {
        Preconditions.checkTrue(url != null, "url can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + url.toString() + "'.");
        InputStream in = url.openStream();
        new XmlConfigBuilder(in).setProperties(properties).build(this);
    }
}

