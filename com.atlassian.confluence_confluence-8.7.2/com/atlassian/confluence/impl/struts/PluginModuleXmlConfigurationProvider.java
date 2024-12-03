/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.opensymphony.xwork2.config.ConfigurationException
 *  com.opensymphony.xwork2.config.entities.ActionConfig
 *  com.opensymphony.xwork2.config.entities.InterceptorConfig
 *  com.opensymphony.xwork2.config.entities.PackageConfig$Builder
 *  com.opensymphony.xwork2.config.entities.ResultConfig
 *  com.opensymphony.xwork2.config.entities.ResultTypeConfig
 *  com.opensymphony.xwork2.config.providers.XmlDocConfigurationProvider
 *  com.opensymphony.xwork2.inject.ContainerBuilder
 *  com.opensymphony.xwork2.util.location.LocatableProperties
 *  com.opensymphony.xwork2.util.location.Location
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.plugin.struts.PluginAwareActionConfig;
import com.atlassian.confluence.plugin.struts.PluginAwareInterceptorConfig;
import com.atlassian.confluence.plugin.struts.PluginAwareResultConfig;
import com.atlassian.plugin.Plugin;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.config.providers.XmlDocConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PluginModuleXmlConfigurationProvider
extends XmlDocConfigurationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PluginModuleXmlConfigurationProvider.class);
    private final Plugin plugin;
    private final Runnable disableModuleRunnable;
    private List<ResultTypeConfig> addedResultTypes;

    public PluginModuleXmlConfigurationProvider(Document document, Plugin plugin, Runnable disableModuleRunnable) {
        super(new Document[]{document});
        this.plugin = plugin;
        this.disableModuleRunnable = disableModuleRunnable;
    }

    public void register(ContainerBuilder containerBuilder, LocatableProperties locatableProperties) throws ConfigurationException {
    }

    public void loadPackages() throws ConfigurationException {
        try {
            this.addedResultTypes = new ArrayList<ResultTypeConfig>();
            super.loadPackages();
        }
        catch (Exception e) {
            LOG.error("Failed to load packages for XWork module of plugin {}.", (Object)this.plugin.getKey());
            this.disableModuleRunnable.run();
            throw e;
        }
    }

    protected Class<?> loadClass(String className) throws ClassNotFoundException {
        return this.plugin.loadClass(className, ((Object)((Object)this)).getClass());
    }

    protected ActionConfig buildActionConfig(Element actionElement, Location location, PackageConfig.Builder packageContext, Map<String, ResultConfig> results) {
        ActionConfig actionConfig = super.buildActionConfig(actionElement, location, packageContext, results);
        return new PluginAwareActionConfig(actionConfig, this.plugin);
    }

    protected ResultTypeConfig buildResultTypeConfig(Element resultTypeElement, Location location, String paramName) {
        ResultTypeConfig resultTypeConfig = super.buildResultTypeConfig(resultTypeElement, location, paramName);
        this.addedResultTypes.add(resultTypeConfig);
        return resultTypeConfig;
    }

    protected ResultConfig buildResultConfig(String name, ResultTypeConfig config, Location location, Map<String, String> params) {
        ResultConfig resultConfig = super.buildResultConfig(name, config, location, params);
        if (this.addedResultTypes.contains(config)) {
            resultConfig = new PluginAwareResultConfig(resultConfig, this.plugin);
        }
        return resultConfig;
    }

    protected InterceptorConfig buildInterceptorConfig(Element interceptorElement) {
        InterceptorConfig interceptorConfig = super.buildInterceptorConfig(interceptorElement);
        return new PluginAwareInterceptorConfig(interceptorConfig, this.plugin);
    }
}

