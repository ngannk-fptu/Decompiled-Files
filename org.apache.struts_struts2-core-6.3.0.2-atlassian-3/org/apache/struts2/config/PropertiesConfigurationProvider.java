/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.config;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.util.Iterator;
import org.apache.struts2.config.DefaultSettings;
import org.apache.struts2.config.Settings;

public class PropertiesConfigurationProvider
implements ConfigurationProvider {
    @Override
    public void destroy() {
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
    }

    @Override
    public void loadPackages() throws ConfigurationException {
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        DefaultSettings settings = new DefaultSettings();
        this.loadSettings(props, settings);
    }

    protected void loadSettings(LocatableProperties props, Settings settings) {
        Iterator i = settings.list();
        while (i.hasNext()) {
            String name = (String)i.next();
            props.setProperty(name, settings.get(name), settings.getLocation(name));
        }
    }
}

