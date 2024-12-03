/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.runtime.log.Log
 */
package org.apache.velocity.tools;

import java.util.Map;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.Toolbox;
import org.apache.velocity.tools.ToolboxFactory;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.apache.velocity.tools.config.FactoryConfiguration;

public class ToolManager {
    protected VelocityEngine velocity;
    protected ToolboxFactory factory = new ToolboxFactory();
    private Toolbox application;
    private boolean userOverwrite = true;

    public ToolManager() {
        this(true, true);
    }

    public ToolManager(boolean includeDefaults) {
        this(true, includeDefaults);
    }

    public ToolManager(boolean autoConfig, boolean includeDefaults) {
        if (autoConfig) {
            this.autoConfigure(includeDefaults);
        }
    }

    public void autoConfigure(boolean includeDefaults) {
        FactoryConfiguration config = ConfigurationUtils.getAutoLoaded(includeDefaults);
        FactoryConfiguration sys = ConfigurationUtils.findFromSystemProperty();
        if (sys != null) {
            config.addConfiguration(sys);
        }
        this.configure(config);
    }

    public void configure(FactoryConfiguration config) {
        this.application = null;
        this.factory.configure(config);
    }

    public void configure(String path) {
        FactoryConfiguration config = this.findConfig(path);
        if (config == null) {
            throw new RuntimeException("Could not find any configuration at " + path);
        }
        this.configure(config);
    }

    protected FactoryConfiguration findConfig(String path) {
        return ConfigurationUtils.find(path);
    }

    public ToolboxFactory getToolboxFactory() {
        return this.factory;
    }

    public void setToolboxFactory(ToolboxFactory factory) {
        if (this.factory != factory) {
            if (factory == null) {
                throw new NullPointerException("ToolboxFactory cannot be null");
            }
            this.debug("ToolboxFactory instance was changed to %s", factory);
            this.factory = factory;
        }
    }

    public void setVelocityEngine(VelocityEngine engine) {
        if (this.velocity != engine) {
            this.debug("VelocityEngine instance was changed to %s", engine);
            this.velocity = engine;
        }
    }

    public VelocityEngine getVelocityEngine() {
        return this.velocity;
    }

    public void setUserCanOverwriteTools(boolean overwrite) {
        this.userOverwrite = overwrite;
    }

    public boolean getUserCanOverwriteTools() {
        return this.userOverwrite;
    }

    public Log getLog() {
        if (this.velocity == null) {
            return null;
        }
        return this.velocity.getLog();
    }

    protected final void debug(String msg, Object ... args) {
        Log log = this.getLog();
        if (log != null && log.isDebugEnabled()) {
            log.debug((Object)String.format(msg, args));
        }
    }

    public ToolContext createContext() {
        return this.createContext(null);
    }

    public ToolContext createContext(Map<String, Object> toolProps) {
        ToolContext context = new ToolContext(toolProps);
        this.prepareContext(context);
        return context;
    }

    protected void prepareContext(ToolContext context) {
        context.setUserCanOverwriteTools(this.userOverwrite);
        if (this.velocity != null) {
            context.putVelocityEngine(this.velocity);
        }
        this.addToolboxes(context);
    }

    protected void addToolboxes(ToolContext context) {
        if (this.hasApplicationTools()) {
            context.addToolbox(this.getApplicationToolbox());
        }
        if (this.hasRequestTools()) {
            context.addToolbox(this.getRequestToolbox());
        }
    }

    protected boolean hasTools(String scope) {
        return this.factory.hasTools(scope);
    }

    protected Toolbox createToolbox(String scope) {
        return this.factory.createToolbox(scope);
    }

    protected boolean hasRequestTools() {
        return this.hasTools("request");
    }

    protected Toolbox getRequestToolbox() {
        return this.createToolbox("request");
    }

    protected boolean hasApplicationTools() {
        return this.hasTools("application");
    }

    protected Toolbox getApplicationToolbox() {
        if (this.application == null && this.hasApplicationTools()) {
            this.application = this.createToolbox("application");
        }
        return this.application;
    }
}

