/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class UrlYamlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(UrlYamlConfig.class);

    public UrlYamlConfig(String url) throws IOException {
        this(new URL(url));
    }

    public UrlYamlConfig(String url, Properties properties) throws IOException {
        this(new URL(url), properties);
    }

    public UrlYamlConfig(URL url) throws IOException {
        this(url, System.getProperties());
    }

    public UrlYamlConfig(URL url, Properties properties) throws IOException {
        Preconditions.checkTrue(url != null, "url can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + url.toString() + "'.");
        InputStream in = url.openStream();
        new YamlConfigBuilder(in).setProperties(properties).build(this);
    }
}

