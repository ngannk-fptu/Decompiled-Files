/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.StateAware
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ContainerManagedPlugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  org.dom4j.Attribute
 *  org.dom4j.Element
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.templaterenderer.plugins;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.StateAware;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ContainerManagedPlugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class TemplateContextItemModuleDescriptor
extends AbstractModuleDescriptor<Object>
implements StateAware {
    private Logger log = LoggerFactory.getLogger(TemplateContextItemModuleDescriptor.class);
    private boolean global = false;
    private String contextKey;
    private String componentRef = null;
    private Object component = null;
    private ApplicationContext applicationContext;

    public TemplateContextItemModuleDescriptor() {
        super(ModuleFactory.LEGACY_MODULE_FACTORY);
    }

    public void init(Plugin plugin, Element element) throws PluginParseException {
        Attribute contextKeyAtt;
        super.init(plugin, element);
        Attribute globalAtt = element.attribute("global");
        if (globalAtt != null) {
            this.global = Boolean.parseBoolean(globalAtt.getValue());
        }
        if ((contextKeyAtt = element.attribute("context-key")) == null) {
            throw new PluginParseException("context-key must be specified");
        }
        this.contextKey = contextKeyAtt.getValue();
        Attribute componentRefAttr = element.attribute("component-ref");
        Attribute classAttr = element.attribute("class");
        if (componentRefAttr != null) {
            if (classAttr != null) {
                throw new PluginParseException("You may not specify both a class and a component-ref");
            }
            this.componentRef = componentRefAttr.getValue();
        } else if (classAttr == null) {
            throw new PluginParseException("You must specify a class or a component-ref");
        }
    }

    public synchronized Object getModule() {
        if (this.componentRef != null) {
            return this.getApplicationContext().getBean(this.componentRef);
        }
        if (this.component == null) {
            this.component = ((ContainerManagedPlugin)this.getPlugin()).getContainerAccessor().createBean(this.getModuleClass());
        }
        return this.component;
    }

    private ApplicationContext getApplicationContext() {
        if (this.applicationContext == null) {
            OsgiPlugin osgiPlugin = (OsgiPlugin)this.getPlugin();
            BundleContext bundleContext = osgiPlugin.getBundle().getBundleContext();
            try {
                ServiceReference[] srs = bundleContext.getServiceReferences(ApplicationContext.class.getName(), "(org.springframework.context.service.name=" + osgiPlugin.getBundle().getSymbolicName() + ")");
                if (srs.length != 1) {
                    this.log.error("Spring DM is being evil, there is not exactly one ApplicationContext for the bundle " + osgiPlugin.getBundle().getSymbolicName() + ", there are " + srs.length);
                }
                this.applicationContext = (ApplicationContext)bundleContext.getService(srs[0]);
            }
            catch (InvalidSyntaxException ise) {
                this.log.error("Bad filter", (Throwable)ise);
            }
        }
        return this.applicationContext;
    }

    public synchronized void disabled() {
        super.disabled();
        this.component = null;
        this.applicationContext = null;
    }

    public boolean isGlobal() {
        return this.global;
    }

    public String getContextKey() {
        return this.contextKey;
    }
}

