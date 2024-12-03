/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.atlassian.soy.impl.DevMode
 *  com.atlassian.soy.spi.TemplateSetFactory
 *  com.google.common.base.Charsets
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.io.Resources
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.soy.impl;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.atlassian.soy.impl.DevMode;
import com.atlassian.soy.spi.TemplateSetFactory;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WebResourceTemplateSetFactory
implements TemplateSetFactory {
    private static final Logger log = LoggerFactory.getLogger(WebResourceTemplateSetFactory.class);
    private final PluginAccessor pluginAccessor;
    private final ServletContextFactory servletContextFactory;
    @TenantAware(value=TenancyScope.TENANTLESS, comment="completeKey to URLs mapping, same for all tenants.")
    private final LoadingCache<String, Set<URL>> templateSetCache;

    public WebResourceTemplateSetFactory(PluginAccessor pluginAccessor, ServletContextFactory servletContextFactory) {
        this.pluginAccessor = pluginAccessor;
        this.servletContextFactory = servletContextFactory;
        CacheLoader<String, Set<URL>> findTemplatesFunction = new CacheLoader<String, Set<URL>>(){

            public Set<URL> load(String moduleKey) throws IOException {
                return WebResourceTemplateSetFactory.this.findRequiredTemplates(moduleKey);
            }
        };
        this.templateSetCache = CacheBuilder.newBuilder().build((CacheLoader)findTemplatesFunction);
    }

    public Set<URL> get(String completeModuleKey) {
        return (Set)this.templateSetCache.getUnchecked((Object)completeModuleKey);
    }

    public void clear() {
        this.templateSetCache.invalidateAll();
    }

    private Set<URL> findRequiredTemplates(String pluginModuleKey) throws IOException {
        log.debug("Found Soy template files for '{}'", (Object)pluginModuleKey);
        TemplateSetBuilder templateSetBuilder = new TemplateSetBuilder();
        templateSetBuilder.addTemplatesForTree(pluginModuleKey);
        List missingModuleDescriptors = templateSetBuilder.missingModuleDescriptors();
        if (missingModuleDescriptors.size() > 0) {
            log.warn("Some module descriptors are either missing or disabled; soy compilation may fail. Missing descriptors: {}", (Object)missingModuleDescriptors);
        }
        Set result = templateSetBuilder.build();
        log.debug("Found Soy template files for '{}' was {}", (Object)pluginModuleKey, (Object)result);
        return result;
    }

    private class TemplateSetBuilder {
        private final Set<URL> fileSet = new HashSet<URL>();
        private final Set<String> alreadyAddedModules = new HashSet<String>();
        private final List<String> missingDescriptors = new ArrayList<String>();

        private TemplateSetBuilder() {
        }

        private void addTemplatesForTree(String completeModuleKey) throws IOException {
            if (this.alreadyAddedModules.contains(completeModuleKey)) {
                return;
            }
            this.alreadyAddedModules.add(completeModuleKey);
            ModuleDescriptor moduleDescriptor = WebResourceTemplateSetFactory.this.pluginAccessor.getEnabledPluginModule(completeModuleKey);
            if (moduleDescriptor == null) {
                this.missingDescriptors.add(completeModuleKey);
                log.debug("Required plugin module " + completeModuleKey + " was either missing or disabled");
            } else {
                if (moduleDescriptor instanceof WebResourceModuleDescriptor) {
                    this.addTemplatesForTree((WebResourceModuleDescriptor)moduleDescriptor);
                }
                this.addSoyTemplateResources(moduleDescriptor);
            }
        }

        private void addTemplatesForTree(WebResourceModuleDescriptor webResourceModuleDescriptor) throws IOException {
            for (String dependencyModuleKey : webResourceModuleDescriptor.getDependencies()) {
                this.addTemplatesForTree(dependencyModuleKey);
            }
        }

        private void addSoyTemplateResources(ModuleDescriptor<?> moduleDescriptor) throws IOException {
            for (ResourceDescriptor resource : moduleDescriptor.getResourceDescriptors()) {
                URL url;
                if (!this.isSoyTemplate(resource) || (url = this.getSoyResourceURL(moduleDescriptor, resource)) == null) continue;
                if (DevMode.isDevMode()) {
                    try (Reader reader = Resources.asCharSource((URL)url, (Charset)Charsets.UTF_8).openStream();){
                        if (!reader.ready()) {
                            throw new IOException("Empty file for resource " + resource.getLocation() + " in module descriptor " + moduleDescriptor.getCompleteKey());
                        }
                    }
                }
                this.fileSet.add(url);
            }
        }

        private boolean isSoyTemplate(ResourceDescriptor resource) {
            return StringUtils.endsWith((CharSequence)resource.getLocation(), (CharSequence)".soy");
        }

        private URL getSoyResourceURL(ModuleDescriptor moduleDescriptor, ResourceDescriptor resource) {
            String sourceParam = resource.getParameter("source");
            if ("webContextStatic".equalsIgnoreCase(sourceParam)) {
                try {
                    return WebResourceTemplateSetFactory.this.servletContextFactory.getServletContext().getResource(resource.getLocation());
                }
                catch (MalformedURLException e) {
                    log.error("Ignoring soy resource. Could not locate soy with location: " + resource.getLocation());
                    return null;
                }
            }
            return moduleDescriptor.getPlugin().getResource(resource.getLocation());
        }

        private Set<URL> build() {
            return ImmutableSet.copyOf(this.fileSet);
        }

        private List<String> missingModuleDescriptors() {
            return this.missingDescriptors;
        }
    }
}

