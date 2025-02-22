/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.YamlConfigBuilder;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

public class FileSystemYamlConfig
extends Config {
    private static final ILogger LOGGER = Logger.getLogger(FileSystemYamlConfig.class);

    public FileSystemYamlConfig(String configFilename) throws FileNotFoundException {
        this(configFilename, System.getProperties());
    }

    public FileSystemYamlConfig(String configFilename, Properties properties) throws FileNotFoundException {
        this(new File(configFilename), properties);
    }

    public FileSystemYamlConfig(File configFile) throws FileNotFoundException {
        this(configFile, System.getProperties());
    }

    public FileSystemYamlConfig(File configFile, Properties properties) throws FileNotFoundException {
        Preconditions.checkTrue(configFile != null, "configFile can't be null");
        Preconditions.checkTrue(properties != null, "properties can't be null");
        LOGGER.info("Configuring Hazelcast from '" + configFile.getAbsolutePath() + "'.");
        FileInputStream in = new FileInputStream(configFile);
        new YamlConfigBuilder(in).setProperties(properties).build(this);
    }
}

