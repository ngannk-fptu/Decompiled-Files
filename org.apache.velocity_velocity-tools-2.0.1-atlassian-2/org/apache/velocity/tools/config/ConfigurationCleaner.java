/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.config;

import java.util.Iterator;
import org.apache.velocity.tools.config.Configuration;
import org.apache.velocity.tools.config.ConfigurationException;
import org.apache.velocity.tools.config.Data;
import org.apache.velocity.tools.config.FactoryConfiguration;
import org.apache.velocity.tools.config.LogSupport;
import org.apache.velocity.tools.config.Property;
import org.apache.velocity.tools.config.ToolConfiguration;
import org.apache.velocity.tools.config.ToolboxConfiguration;

public class ConfigurationCleaner
extends LogSupport {
    private static final String LOG_PREFIX = "ConfigurationCleaner : ";

    @Override
    protected String logPrefix() {
        return LOG_PREFIX;
    }

    public void clean(FactoryConfiguration factory) {
        if (this.isTraceEnabled()) {
            this.trace("Cleaning factory: " + factory);
        }
        this.cleanProperties(factory);
        Iterator i = factory.getData().iterator();
        while (i.hasNext()) {
            Data datum = (Data)i.next();
            try {
                datum.validate();
            }
            catch (ConfigurationException ce) {
                if (this.isDebugEnabled()) {
                    this.debug(ce.getMessage());
                }
                if (this.isWarnEnabled()) {
                    this.warn("Removing " + datum);
                }
                i.remove();
            }
        }
        for (ToolboxConfiguration toolbox : factory.getToolboxes()) {
            this.clean(toolbox);
        }
    }

    public void clean(ToolboxConfiguration toolbox) {
        this.cleanProperties(toolbox);
        Iterator<ToolConfiguration> i = toolbox.getTools().iterator();
        while (i.hasNext()) {
            ToolConfiguration tool = i.next();
            this.cleanProperties(tool);
            try {
                tool.validate();
            }
            catch (ConfigurationException ce) {
                if (this.isDebugEnabled()) {
                    this.debug(ce.getMessage());
                }
                if (this.isWarnEnabled()) {
                    this.warn("Removing " + tool);
                }
                i.remove();
            }
        }
    }

    public void clean(Configuration config) {
        if (config instanceof FactoryConfiguration) {
            this.clean((FactoryConfiguration)config);
        } else if (config instanceof ToolboxConfiguration) {
            this.clean((ToolboxConfiguration)config);
        } else {
            this.cleanProperties(config);
        }
    }

    public void cleanProperties(Configuration config) {
        Iterator i = config.getProperties().iterator();
        while (i.hasNext()) {
            Property prop = (Property)i.next();
            try {
                prop.validate();
            }
            catch (ConfigurationException ce) {
                if (this.isDebugEnabled()) {
                    this.debug(ce.getMessage());
                }
                if (this.isWarnEnabled()) {
                    this.warn("Removing " + prop);
                }
                i.remove();
            }
        }
    }
}

