/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.osgi.framework.ServiceRegistration
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugins.rest.module.ApiVersion;
import com.atlassian.plugins.rest.module.InvalidVersionException;
import com.atlassian.plugins.rest.module.RestApiContext;
import com.atlassian.plugins.rest.module.RestServletFilterModuleDescriptor;
import com.atlassian.plugins.rest.module.servlet.RestServletModuleManager;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.osgi.framework.ServiceRegistration;

public class RestModuleDescriptor
extends AbstractModuleDescriptor<Object> {
    private final RestServletModuleManager servletModuleManager;
    private final String restContext;
    private RestApiContext restApiContext;
    private ServiceRegistration serviceRegistration;
    private RestServletFilterModuleDescriptor restServletFilterModuleDescriptor;
    private OsgiPlugin osgiPlugin;
    private Element element;

    public RestModuleDescriptor(ModuleFactory moduleFactory, RestServletModuleManager servletModuleManager, String restContext) {
        super(moduleFactory);
        this.servletModuleManager = Objects.requireNonNull(servletModuleManager, "servletModuleManager can't be null");
        this.restContext = Objects.requireNonNull(restContext, "restContext can't be null");
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        this.restApiContext = new RestApiContext(this.restContext, this.parsePath(element), this.parseVersion(element), this.parsePackages(element), this.parseIndexBundledPluginsFlag(element));
        this.osgiPlugin = (OsgiPlugin)plugin;
        this.element = element;
    }

    private boolean parseIndexBundledPluginsFlag(Element element) {
        return Boolean.parseBoolean(element.attributeValue("index-bundled-jars"));
    }

    public RestApiContext getRestApiContext() {
        return this.restApiContext;
    }

    private String parsePath(Element element) {
        return element.attributeValue("path");
    }

    private Set<String> parsePackages(Element rootElement) {
        HashSet<String> packages = new HashSet<String>();
        for (Element pkgElement : rootElement.elements("package")) {
            packages.add(pkgElement.getTextTrim());
        }
        return packages;
    }

    private ApiVersion parseVersion(Element element) {
        try {
            return new ApiVersion(element.attributeValue("version"));
        }
        catch (InvalidVersionException e) {
            throw new InvalidVersionException(this.plugin, this, e);
        }
    }

    private Element updateElementForFilterConfiguration(Element element) {
        Element copy = element.createCopy();
        copy.addAttribute("location", FilterLocation.BEFORE_DISPATCH.name());
        copy.addElement("url-pattern").addText(this.restApiContext.getContextlessPathToVersion() + "/*");
        copy.addAttribute("key", copy.attributeValue("key") + "-filter");
        return copy;
    }

    public Object getModule() {
        return null;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        RestModuleDescriptor that = (RestModuleDescriptor)((Object)o);
        return that.getCompleteKey().equals(this.getCompleteKey());
    }

    public int hashCode() {
        return this.getCompleteKey().hashCode();
    }

    public String toString() {
        return super.toString() + '/' + this.restContext + (this.restApiContext != null ? '/' + this.restApiContext.getApiPath() + '/' + this.restApiContext.getVersion() : "");
    }

    public void disabled() {
        if (this.restServletFilterModuleDescriptor != null) {
            this.restServletFilterModuleDescriptor.disabled();
            this.restServletFilterModuleDescriptor = null;
        }
        this.restApiContext.disabled();
        if (this.serviceRegistration != null) {
            try {
                this.serviceRegistration.unregister();
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
            this.serviceRegistration = null;
        }
        super.disabled();
    }

    public void enabled() {
        super.enabled();
        this.restServletFilterModuleDescriptor = new RestServletFilterModuleDescriptor(this.osgiPlugin, this.moduleFactory, this.servletModuleManager, this.restApiContext);
        this.restServletFilterModuleDescriptor.init(this.plugin, this.updateElementForFilterConfiguration(this.element));
        this.restServletFilterModuleDescriptor.enabled();
        this.serviceRegistration = this.osgiPlugin.getBundle().getBundleContext().registerService(new String[]{((Object)((Object)this.restServletFilterModuleDescriptor)).getClass().getName(), ModuleDescriptor.class.getName()}, (Object)this.restServletFilterModuleDescriptor, null);
    }
}

