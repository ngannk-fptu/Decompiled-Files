/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.ConfigurationPersister
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.ConfigurationPersister;
import java.util.HashMap;
import java.util.Map;

public class ReadOnlyApplicationConfig
implements ApplicationConfiguration {
    private final ApplicationConfiguration delegate;

    public ReadOnlyApplicationConfig(ApplicationConfiguration delegate) {
        this.delegate = delegate;
    }

    public String getApplicationHome() {
        return this.delegate.getApplicationHome();
    }

    public void setApplicationHome(String home) throws ConfigurationException {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public boolean isApplicationHomeValid() {
        return this.delegate.isApplicationHomeValid();
    }

    public void setProperty(Object key, Object value) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void setProperty(Object key, int value) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void setProperty(Object key, boolean value) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public Object getProperty(Object key) {
        return this.delegate.getProperty(key);
    }

    public boolean getBooleanProperty(Object key) {
        return this.delegate.getBooleanProperty(key);
    }

    public int getIntegerProperty(Object key) {
        return this.delegate.getIntegerProperty(key);
    }

    public Object removeProperty(Object key) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public Map getProperties() {
        return new HashMap(this.delegate.getProperties());
    }

    public String getBuildNumber() {
        return this.delegate.getBuildNumber();
    }

    public void setBuildNumber(String build) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public int getMajorVersion() {
        return this.delegate.getMajorVersion();
    }

    public void setMajorVersion(int majorVersion) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public int getMinorVersion() {
        return this.delegate.getMinorVersion();
    }

    public void setMinorVersion(int minorVersion) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public String getApplicationVersion() {
        return this.delegate.getApplicationVersion();
    }

    public Map getPropertiesWithPrefix(String prefix) {
        return this.delegate.getPropertiesWithPrefix(prefix);
    }

    public boolean isSetupComplete() {
        return this.delegate.isSetupComplete();
    }

    public void setSetupComplete(boolean setupComplete) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void setConfigurationPersister(ConfigurationPersister config) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void save() throws ConfigurationException {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void reset() {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public String getSetupType() {
        return this.delegate.getSetupType();
    }

    public void setSetupType(String setupType) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public String getCurrentSetupStep() {
        return this.delegate.getCurrentSetupStep();
    }

    public void setCurrentSetupStep(String currentSetupStep) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public void load() throws ConfigurationException {
        throw new UnsupportedOperationException("Mutation not allowed");
    }

    public boolean configFileExists() {
        return this.delegate.configFileExists();
    }

    public void setConfigurationFileName(String configurationFileName) {
        throw new UnsupportedOperationException("Mutation not allowed");
    }
}

