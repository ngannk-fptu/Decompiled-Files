/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.GuardedBy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.config;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.ConfigurationPersister;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig
implements ApplicationConfiguration {
    public static final boolean NULL_BOOLEAN_VALUE = false;
    public static final int NULL_INTEGER_VALUE = Integer.MIN_VALUE;
    public static final String DEFAULT_CONFIG_FILE_NAME = "atlassian-config.xml";
    public static final String DEFAULT_APPLICATION_HOME = ".";
    private static final Logger privateLog = LoggerFactory.getLogger(ApplicationConfig.class);
    private final String setupStepNode = "setupStep";
    private final String setupTypeNode = "setupType";
    private final String buildNumberNode = "buildNumber";
    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
    @GuardedBy(value="this")
    protected volatile ConfigurationPersister configurationPersister;
    @GuardedBy(value="this")
    private String currentSetupStep;
    @GuardedBy(value="this")
    private String setupType;
    @GuardedBy(value="this")
    private String configurationFileName;
    @GuardedBy(value="this")
    private boolean homeOk = false;
    @GuardedBy(value="this")
    private String applicationHome = ".";
    @GuardedBy(value="this")
    private String buildNumber = "0";
    @GuardedBy(value="this")
    private int majorVersion = 0;
    @GuardedBy(value="this")
    private int minorVersion = 0;
    @GuardedBy(value="this")
    private boolean setupComplete = false;

    @Override
    public synchronized void reset() {
        this.homeOk = false;
        this.applicationHome = DEFAULT_APPLICATION_HOME;
        this.properties.clear();
        this.buildNumber = "0";
        this.majorVersion = 0;
        this.minorVersion = 0;
        this.setupComplete = false;
        this.configurationPersister = null;
    }

    @Override
    public synchronized String getApplicationHome() {
        return this.applicationHome;
    }

    @Override
    public synchronized void setApplicationHome(String home) throws ConfigurationException {
        File homeDir = new File(home);
        if (!homeDir.isDirectory()) {
            privateLog.warn("Application home does not exist. Creating directory: {}", (Object)homeDir.getAbsolutePath());
            this.homeOk = homeDir.mkdirs();
            if (!this.homeOk) {
                throw new ConfigurationException("Could not make directory/ies: " + homeDir.getAbsolutePath());
            }
        }
        try {
            this.applicationHome = homeDir.getCanonicalPath();
            this.homeOk = true;
        }
        catch (IOException e) {
            this.homeOk = false;
            throw new ConfigurationException("Failed to locate application home: " + home, e);
        }
    }

    @Override
    public synchronized boolean isApplicationHomeValid() {
        return this.homeOk;
    }

    @Override
    public void setProperty(Object key, Object value) {
        this.properties.put(key.toString(), value != null ? value : NULL.OBJECT);
    }

    @Override
    public Object removeProperty(Object key) {
        return this.properties.remove(key.toString());
    }

    @Override
    public Object getProperty(Object key) {
        Object value = this.properties.get(key.toString());
        return value != NULL.OBJECT ? value : null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public synchronized String getBuildNumber() {
        return this.buildNumber;
    }

    @Override
    public synchronized void setBuildNumber(String build) {
        this.buildNumber = build;
    }

    @Override
    public synchronized int getMajorVersion() {
        return this.majorVersion;
    }

    @Override
    public synchronized void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    @Override
    public synchronized int getMinorVersion() {
        return this.minorVersion;
    }

    @Override
    public synchronized void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    @Override
    public synchronized String getApplicationVersion() {
        return this.getMajorVersion() + DEFAULT_APPLICATION_HOME + this.getMinorVersion() + " build: " + this.getBuildNumber();
    }

    @Override
    public synchronized Map<String, Object> getPropertiesWithPrefix(String prefix) {
        HashMap<String, Object> newProps = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : this.properties.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith(prefix)) continue;
            newProps.put(key, entry.getValue());
        }
        return newProps;
    }

    @Override
    public synchronized boolean isSetupComplete() {
        return this.setupComplete;
    }

    @Override
    public synchronized void setSetupComplete(boolean setupComplete) {
        this.setupComplete = setupComplete;
    }

    @Override
    public void setProperty(Object key, int value) {
        this.properties.put(key.toString(), value);
    }

    @Override
    public void setProperty(Object key, boolean value) {
        this.properties.put(key.toString(), value);
    }

    @Override
    public boolean getBooleanProperty(Object key) {
        Object temp = this.properties.get(key.toString());
        if (temp == null) {
            return false;
        }
        if (temp instanceof Boolean) {
            return (Boolean)temp;
        }
        return Boolean.valueOf(temp.toString());
    }

    @Override
    public int getIntegerProperty(Object key) {
        Object temp = this.properties.get(key.toString());
        if (temp == null) {
            return Integer.MIN_VALUE;
        }
        if (temp instanceof Integer) {
            return (Integer)temp;
        }
        return Integer.valueOf(temp.toString());
    }

    @Override
    public synchronized void setConfigurationPersister(ConfigurationPersister configurationPersister) {
        this.configurationPersister = configurationPersister;
    }

    @Deprecated
    public void setInitialProperties(Map<String, Object> initialProperties) {
        this.properties.putAll(initialProperties);
    }

    protected synchronized String getConfigurationFileName() {
        return this.configurationFileName;
    }

    @Override
    public synchronized void setConfigurationFileName(String configurationFileName) {
        this.configurationFileName = configurationFileName;
    }

    @Override
    public synchronized String getSetupType() {
        return this.setupType;
    }

    @Override
    public synchronized void setSetupType(String setupType) {
        this.setupType = setupType;
    }

    @Override
    public synchronized String getCurrentSetupStep() {
        return this.currentSetupStep;
    }

    @Override
    public synchronized void setCurrentSetupStep(String currentSetupStep) {
        this.currentSetupStep = currentSetupStep;
    }

    @Override
    public synchronized void load() throws ConfigurationException {
        this.configurationPersister.load(this.getApplicationHome(), this.getConfigurationFileName());
        this.setBuildNumber(this.configurationPersister.getStringConfigElement("buildNumber"));
        this.setSetupType(this.configurationPersister.getStringConfigElement("setupType"));
        this.setCurrentSetupStep(this.configurationPersister.getStringConfigElement("setupStep"));
        this.properties.putAll((Map)this.configurationPersister.getConfigElement(Map.class, "properties"));
    }

    @Override
    public synchronized boolean configFileExists() {
        return new File(this.getApplicationHome(), this.getConfigurationFileName()).exists();
    }

    @Override
    public synchronized void save() throws ConfigurationException {
        this.configurationPersister.clear();
        this.configurationPersister.addConfigElement(this.getCurrentSetupStep(), "setupStep");
        this.configurationPersister.addConfigElement(this.getSetupType(), "setupType");
        this.configurationPersister.addConfigElement(this.getBuildNumber(), "buildNumber");
        this.configurationPersister.addConfigElement(new TreeMap<String, Object>(this.getProperties()), "properties");
        this.configurationPersister.save(this.getApplicationHome(), this.getConfigurationFileName());
    }

    static enum NULL {
        OBJECT;

    }
}

