/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.beanutils.BeanUtils
 *  org.apache.commons.collections.ExtendedProperties
 */
package org.apache.velocity.tools.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.FileFactoryConfiguration;
import org.apache.velocity.tools.config.Property;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class PropertiesFactoryConfiguration
extends FileFactoryConfiguration {
    public PropertiesFactoryConfiguration() {
        this("");
    }

    public PropertiesFactoryConfiguration(String id) {
        super(PropertiesFactoryConfiguration.class, id);
    }

    @Override
    public void read(InputStream input) throws IOException {
        ExtendedProperties props = new ExtendedProperties();
        props.load(input);
        this.read(props.subset("tools"));
    }

    public void read(ExtendedProperties factory) {
        this.readProperties(factory, this);
        this.readToolboxes(factory);
        this.readData(factory.subset("data"));
    }

    protected void readProperties(ExtendedProperties configProps, Configuration config) {
        ExtendedProperties properties = configProps.subset("property");
        if (properties != null) {
            Iterator i = properties.getKeys();
            while (i.hasNext()) {
                String name = (String)i.next();
                String value = properties.getString(name);
                ExtendedProperties propProps = properties.subset(name);
                if (propProps.size() == 1) {
                    config.setProperty(name, value);
                    continue;
                }
                Property property = new Property();
                property.setName(name);
                property.setValue(value);
                this.setProperties(propProps, property);
            }
        }
    }

    protected void readToolboxes(ExtendedProperties factory) {
        String[] scopes;
        for (String scope : scopes = factory.getStringArray("toolbox")) {
            ToolboxConfiguration toolbox = new ToolboxConfiguration();
            toolbox.setScope(scope);
            this.addToolbox(toolbox);
            ExtendedProperties toolboxProps = factory.subset(scope);
            this.readTools(toolboxProps, toolbox);
            this.readProperties(toolboxProps, toolbox);
        }
    }

    protected void readTools(ExtendedProperties tools, ToolboxConfiguration toolbox) {
        Iterator i = tools.getKeys();
        while (i.hasNext()) {
            String key = (String)i.next();
            if (key.indexOf(46) >= 0) continue;
            String classname = tools.getString(key);
            ToolConfiguration tool = new ToolConfiguration();
            tool.setClassname(classname);
            tool.setKey(key);
            toolbox.addTool(tool);
            ExtendedProperties toolProps = tools.subset(key);
            this.readProperties(toolProps, tool);
            Iterator j = toolProps.getKeys();
            while (j.hasNext()) {
                String name = (String)j.next();
                if (name.equals(tool.getKey())) continue;
                tool.setProperty(name, toolProps.getString(name));
            }
            String restrictTo = toolProps.getString("restrictTo");
            tool.setRestrictTo(restrictTo);
        }
    }

    protected void readData(ExtendedProperties dataset) {
        if (dataset != null) {
            Iterator i = dataset.getKeys();
            while (i.hasNext()) {
                String key = (String)i.next();
                if (key.indexOf(46) >= 0) continue;
                Data data = new Data();
                data.setKey(key);
                data.setValue(dataset.getString(key));
                ExtendedProperties props = dataset.subset(key);
                this.setProperties(props, data);
                this.addData(data);
            }
        }
    }

    protected void setProperties(ExtendedProperties props, Data data) {
        try {
            BeanUtils.populate((Object)data, (Map)props);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

