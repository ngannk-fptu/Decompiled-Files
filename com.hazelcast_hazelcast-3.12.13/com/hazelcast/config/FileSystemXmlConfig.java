/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class FileSystemXmlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(FileSystemXmlConfig.class);

    public FileSystemXmlConfig(String configFilename) throws FileNotFoundException {
        this(configFilename, System.getProperties());
    }

    public FileSystemXmlConfig(String configFilename, Properties properties) throws FileNotFoundException {
        this(new File(configFilename), properties);
    }

    public FileSystemXmlConfig(File configFile) throws FileNotFoundException {
        this(configFile, System.getProperties());
    }

    public FileSystemXmlConfig(File configFile, Properties properties) throws FileNotFoundException {
        Preconditions.checkTrue(configFile != null, "configFile can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + configFile.getAbsolutePath() + "'.");
        FileInputStream in = new FileInputStream(configFile);
        new XmlConfigBuilder(in).setProperties(properties).build(this);
    }
}

