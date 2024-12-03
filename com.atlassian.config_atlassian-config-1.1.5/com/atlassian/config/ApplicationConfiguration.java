/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.config;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.ConfigurationPersister;
import java.util.Map;

public interface ApplicationConfiguration {
    public String getApplicationHome();

    public void setApplicationHome(String var1) throws ConfigurationException;

    public boolean isApplicationHomeValid();

    public void setProperty(Object var1, Object var2);

    public void setProperty(Object var1, int var2);

    public void setProperty(Object var1, boolean var2);

    public Object getProperty(Object var1);

    public boolean getBooleanProperty(Object var1);

    public int getIntegerProperty(Object var1);

    public Object removeProperty(Object var1);

    public Map getProperties();

    public String getBuildNumber();

    public void setBuildNumber(String var1);

    public int getMajorVersion();

    public void setMajorVersion(int var1);

    public int getMinorVersion();

    public void setMinorVersion(int var1);

    public String getApplicationVersion();

    public Map getPropertiesWithPrefix(String var1);

    public boolean isSetupComplete();

    public void setSetupComplete(boolean var1);

    public void setConfigurationPersister(ConfigurationPersister var1);

    public void save() throws ConfigurationException;

    public void reset();

    public String getSetupType();

    public void setSetupType(String var1);

    public String getCurrentSetupStep();

    public void setCurrentSetupStep(String var1);

    public void load() throws ConfigurationException;

    public boolean configFileExists();

    public void setConfigurationFileName(String var1);
}

