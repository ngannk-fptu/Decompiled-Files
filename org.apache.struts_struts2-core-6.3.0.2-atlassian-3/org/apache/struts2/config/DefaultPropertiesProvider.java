/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.PropertiesSettings;

public class DefaultPropertiesProvider
extends PropertiesConfigurationProvider {
    @Override
    public void destroy() {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        try {
            PropertiesSettings defaultSettings = new PropertiesSettings("org/apache/struts2/default");
            this.loadSettings(props, defaultSettings);
        }
        catch (Exception e) {
            throw new ConfigurationException("Could not find or error in org/apache/struts2/default.properties", e);
        }
    }
}

