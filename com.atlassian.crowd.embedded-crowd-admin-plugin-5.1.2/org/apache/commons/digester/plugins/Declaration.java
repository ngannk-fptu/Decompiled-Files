/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.digester.plugins;

import java.util.Map;
import java.util.Properties;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.plugins.PluginAssertionFailure;
import org.apache.commons.digester.plugins.PluginException;
import org.apache.commons.digester.plugins.PluginManager;
import org.apache.commons.digester.plugins.RuleLoader;
import org.apache.commons.logging.Log;

public class Declaration {
    private Class pluginClass;
    private String pluginClassName;
    private String id;
    private Properties properties = new Properties();
    private boolean initialized = false;
    private RuleLoader ruleLoader = null;

    public Declaration(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }

    public Declaration(Class pluginClass) {
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
    }

    public Declaration(Class pluginClass, RuleLoader ruleLoader) {
        this.pluginClass = pluginClass;
        this.pluginClassName = pluginClass.getName();
        this.ruleLoader = ruleLoader;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setProperties(Properties p) {
        this.properties.putAll((Map<?, ?>)p);
    }

    public Class getPluginClass() {
        return this.pluginClass;
    }

    public void init(Digester digester, PluginManager pm) throws PluginException {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)"init being called!");
        }
        if (this.initialized) {
            throw new PluginAssertionFailure("Init called multiple times.");
        }
        if (this.pluginClass == null && this.pluginClassName != null) {
            try {
                this.pluginClass = digester.getClassLoader().loadClass(this.pluginClassName);
            }
            catch (ClassNotFoundException cnfe) {
                throw new PluginException("Unable to load class " + this.pluginClassName, cnfe);
            }
        }
        if (this.ruleLoader == null) {
            log.debug((Object)"Searching for ruleloader...");
            this.ruleLoader = pm.findLoader(digester, this.id, this.pluginClass, this.properties);
        } else {
            log.debug((Object)"This declaration has an explicit ruleLoader.");
        }
        if (debug) {
            if (this.ruleLoader == null) {
                log.debug((Object)("No ruleLoader found for plugin declaration id [" + this.id + "]" + ", class [" + this.pluginClass.getClass().getName() + "]."));
            } else {
                log.debug((Object)("RuleLoader of type [" + this.ruleLoader.getClass().getName() + "] associated with plugin declaration" + " id [" + this.id + "]" + ", class [" + this.pluginClass.getClass().getName() + "]."));
            }
        }
        this.initialized = true;
    }

    public void configure(Digester digester, String pattern) throws PluginException {
        Log log = digester.getLogger();
        boolean debug = log.isDebugEnabled();
        if (debug) {
            log.debug((Object)"configure being called!");
        }
        if (!this.initialized) {
            throw new PluginAssertionFailure("Not initialized.");
        }
        if (this.ruleLoader != null) {
            this.ruleLoader.addRules(digester, pattern);
        }
    }
}

