/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public abstract class AbstractConfigLocator {
    private static final ILogger LOGGER = Logger.getLogger(AbstractConfigLocator.class);
    private InputStream in;
    private File configurationFile;
    private URL configurationUrl;
    private final boolean failIfSysPropWithNotExpectedSuffix;

    protected AbstractConfigLocator() {
        this(false);
    }

    protected AbstractConfigLocator(boolean failIfSysPropWithNotExpectedSuffix) {
        this.failIfSysPropWithNotExpectedSuffix = failIfSysPropWithNotExpectedSuffix;
    }

    public InputStream getIn() {
        return this.in;
    }

    public File getConfigurationFile() {
        return this.configurationFile;
    }

    public URL getConfigurationUrl() {
        return this.configurationUrl;
    }

    public boolean isConfigPresent() {
        return this.in != null || this.configurationFile != null || this.configurationUrl != null;
    }

    public abstract boolean locateFromSystemProperty();

    protected abstract boolean locateInWorkDir();

    protected abstract boolean locateOnClasspath();

    public abstract boolean locateDefault();

    public boolean locateEverywhere() {
        return this.locateFromSystemProperty() || this.locateInWorkDir() || this.locateOnClasspath() || this.locateDefault();
    }

    public boolean locateInWorkDirOrOnClasspath() {
        return this.locateInWorkDir() || this.locateOnClasspath();
    }

    protected void loadDefaultConfigurationFromClasspath(String defaultConfigFile) {
        try {
            LOGGER.info(String.format("Loading '%s' from the classpath.", defaultConfigFile));
            this.configurationUrl = Config.class.getClassLoader().getResource(defaultConfigFile);
            if (this.configurationUrl == null) {
                throw new HazelcastException(String.format("Could not find '%s' in the classpath! This may be due to a wrong-packaged or corrupted jar file.", defaultConfigFile));
            }
            this.in = Config.class.getClassLoader().getResourceAsStream(defaultConfigFile);
            if (this.in == null) {
                throw new HazelcastException(String.format("Could not load '%s' from the classpath", defaultConfigFile));
            }
        }
        catch (RuntimeException e) {
            throw new HazelcastException(e);
        }
    }

    protected boolean loadConfigurationFromClasspath(String configFileName) {
        try {
            URL url = Config.class.getClassLoader().getResource(configFileName);
            if (url == null) {
                LOGGER.finest(String.format("Could not find '%s' in the classpath.", configFileName));
                return false;
            }
            LOGGER.info(String.format("Loading '%s' from the classpath.", configFileName));
            this.configurationUrl = url;
            this.in = Config.class.getClassLoader().getResourceAsStream(configFileName);
            if (this.in == null) {
                throw new HazelcastException(String.format("Could not load '%s' from the classpath", configFileName));
            }
            return true;
        }
        catch (RuntimeException e) {
            throw new HazelcastException(e);
        }
    }

    protected boolean loadFromWorkingDirectory(String configFilePath) {
        try {
            File file = new File(configFilePath);
            if (!file.exists()) {
                LOGGER.finest(String.format("Could not find '%s' in the working directory.", configFilePath));
                return false;
            }
            LOGGER.info(String.format("Loading '%s' from the working directory.", configFilePath));
            this.configurationFile = file;
            try {
                this.in = new FileInputStream(file);
            }
            catch (FileNotFoundException e) {
                throw new HazelcastException(String.format("Failed to open file: %s", file.getAbsolutePath()), e);
            }
            return true;
        }
        catch (RuntimeException e) {
            throw new HazelcastException(e);
        }
    }

    protected boolean loadFromSystemProperty(String propertyKey, String ... expectedExtensions) {
        try {
            String configSystemProperty = System.getProperty(propertyKey);
            if (configSystemProperty == null) {
                LOGGER.finest(String.format("Could not find '%s' System property", propertyKey));
                return false;
            }
            if (expectedExtensions != null && expectedExtensions.length > 0 && !this.isExpectedExtensionConfigured(configSystemProperty, expectedExtensions)) {
                if (this.failIfSysPropWithNotExpectedSuffix) {
                    String message = String.format("The suffix of the resource '%s' referenced in '%s' is not in the list of expected suffixes: '%s'", configSystemProperty, propertyKey, Arrays.toString(expectedExtensions));
                    throw new HazelcastException(message);
                }
                return false;
            }
            LOGGER.info(String.format("Loading configuration '%s' from System property '%s'", configSystemProperty, propertyKey));
            if (configSystemProperty.startsWith("classpath:")) {
                this.loadSystemPropertyClassPathResource(configSystemProperty);
            } else {
                this.loadSystemPropertyFileResource(configSystemProperty);
            }
            return true;
        }
        catch (HazelcastException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new HazelcastException(e);
        }
    }

    private boolean isExpectedExtensionConfigured(String configSystemProperty, String[] expectedExtensions) {
        boolean expectedExtension = false;
        String configSystemPropertyLower = configSystemProperty.toLowerCase();
        for (String extension : expectedExtensions) {
            if (!configSystemPropertyLower.endsWith("." + extension.toLowerCase())) continue;
            expectedExtension = true;
            break;
        }
        return expectedExtension;
    }

    private void loadSystemPropertyFileResource(String configSystemProperty) {
        this.configurationFile = new File(configSystemProperty);
        LOGGER.info(String.format("Using configuration file at %s", this.configurationFile.getAbsolutePath()));
        if (!this.configurationFile.exists()) {
            String msg = String.format("Config file at '%s' doesn't exist.", this.configurationFile.getAbsolutePath());
            throw new HazelcastException(msg);
        }
        try {
            this.in = new FileInputStream(this.configurationFile);
        }
        catch (FileNotFoundException e) {
            throw new HazelcastException(String.format("Failed to open file: %s", this.configurationFile.getAbsolutePath()), e);
        }
        try {
            this.configurationUrl = this.configurationFile.toURI().toURL();
        }
        catch (MalformedURLException e) {
            throw new HazelcastException(String.format("Failed to create URL from the file: %s", this.configurationFile.getAbsolutePath()), e);
        }
    }

    private void loadSystemPropertyClassPathResource(String configSystemProperty) {
        String resource = configSystemProperty.substring("classpath:".length());
        LOGGER.info(String.format("Using classpath resource at %s", resource));
        if (resource.isEmpty()) {
            throw new HazelcastException("classpath resource can't be empty");
        }
        this.in = Config.class.getClassLoader().getResourceAsStream(resource);
        if (this.in == null) {
            throw new HazelcastException(String.format("Could not load classpath resource: %s", resource));
        }
        this.configurationUrl = Config.class.getClassLoader().getResource(resource);
    }
}

