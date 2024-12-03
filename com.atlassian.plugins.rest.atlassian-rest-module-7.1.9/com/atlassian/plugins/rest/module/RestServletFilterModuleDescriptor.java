/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.osgi.factory.OsgiPlugin
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor
 *  com.atlassian.plugin.util.validation.ValidationPattern
 *  javax.servlet.Filter
 *  org.dom4j.Element
 */
package com.atlassian.plugins.rest.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.osgi.factory.OsgiPlugin;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.util.validation.ValidationPattern;
import com.atlassian.plugins.rest.doclet.generators.AtlassianWadlGeneratorConfig;
import com.atlassian.plugins.rest.module.ApiVersion;
import com.atlassian.plugins.rest.module.RestApiContext;
import com.atlassian.plugins.rest.module.RestDelegatingServletFilter;
import java.util.Objects;
import javax.servlet.Filter;
import org.dom4j.Element;

public class RestServletFilterModuleDescriptor
extends ServletFilterModuleDescriptor {
    private final RestDelegatingServletFilter restDelegatingServletFilter;
    private final RestApiContext restApiContext;
    private static final String DISABLE_WADL_PROPERTY = "com.sun.jersey.config.feature.DisableWADL";

    RestServletFilterModuleDescriptor(OsgiPlugin plugin, ModuleFactory moduleFactory, ServletModuleManager servletModuleManager, RestApiContext restApiContext) {
        super(Objects.requireNonNull(moduleFactory, "moduleFactory can't be null"), Objects.requireNonNull(servletModuleManager, "servletModuleManager can't be nul"));
        this.restApiContext = Objects.requireNonNull(restApiContext, "restApiContext can't be null");
        this.restDelegatingServletFilter = new RestDelegatingServletFilter(plugin, restApiContext);
    }

    public void init(Plugin plugin, Element element) {
        super.init(plugin, element);
        this.getInitParams().put(DISABLE_WADL_PROPERTY, System.getProperty(DISABLE_WADL_PROPERTY, "false"));
        if (RestServletFilterModuleDescriptor.resourcesAvailable(plugin, "application-doc.xml", "application-grammars.xml", "resourcedoc.xml")) {
            this.getInitParams().put("com.sun.jersey.config.property.WadlGeneratorConfig", AtlassianWadlGeneratorConfig.class.getName());
        }
    }

    protected void provideValidationRules(ValidationPattern pattern) {
    }

    public String getName() {
        return "Rest Servlet Filter";
    }

    public Filter getModule() {
        return this.restDelegatingServletFilter;
    }

    public String getBasePath() {
        return this.restApiContext.getApiPath();
    }

    public ApiVersion getVersion() {
        return this.restApiContext.getVersion();
    }

    private static boolean resourcesAvailable(Plugin plugin, String ... resources) {
        for (String resource : resources) {
            if (plugin.getResource(resource) != null) continue;
            return false;
        }
        return true;
    }
}

