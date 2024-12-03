/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.elements.ResourceDescriptor
 *  com.atlassian.plugin.webresource.CssWebResource
 *  com.atlassian.plugin.webresource.DefaultWebResourceFilter
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.plugin.webresource.ResourceBatchingConfiguration
 *  com.atlassian.plugin.webresource.ResourceDependencyResolver
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceFilter
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.WebResourceManagerImpl
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.assembler.WebResourceSet
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.google.common.io.CharStreams
 *  javax.annotation.PostConstruct
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugin.webresource.DefaultConfluenceWebResourceService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.elements.ResourceDescriptor;
import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.DefaultWebResourceFilter;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.plugin.webresource.ResourceBatchingConfiguration;
import com.atlassian.plugin.webresource.ResourceDependencyResolver;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceManagerImpl;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.WebResourceSet;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfluenceWebResourceManager
extends WebResourceManagerImpl
implements ConfluenceWebResourceManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultConfluenceWebResourceManager.class);
    private static final String REQUEST_CACHE_CSS_ADDED = "confluence.css.added";
    private static final String REQUEST_CACHE_JS_ADDED = "confluence.javascripts.added";
    private static final String REQUEST_CACHE_METADATA_ADDED = "confluence.metadata.added";
    private static final String REQUEST_CACHE_METADATA_KEY = "confluence.metadata.map";
    private CssWebResource cssFilter;
    private JavascriptWebResource jsFormatter;
    private WebResourceFilter defaultFilter;
    private final PluginAccessor pluginAccessor;
    private final ResourceAccessor resourceAccessor;
    private final SettingsManager settingsManager;
    private final DefaultConfluenceWebResourceService confluenceWebResourceService;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;

    public DefaultConfluenceWebResourceManager(WebResourceAssemblerFactory webResourceAssemblerFactory, LegacyPageBuilderService pageBuilderService, PluginResourceLocator pluginResourceLocator, WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, ResourceBatchingConfiguration batchingConfiguration, ResourceDependencyResolver resourceDependencyResolver, PluginAccessor pluginAccessor, ResourceAccessor resourceAccessor, SettingsManager settingsManager, DefaultConfluenceWebResourceService confluenceWebResourceService) {
        super(webResourceAssemblerFactory, pageBuilderService, pluginResourceLocator, webResourceIntegration, webResourceUrlProvider, batchingConfiguration, resourceDependencyResolver);
        this.webResourceUrlProvider = Objects.requireNonNull(webResourceUrlProvider);
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.resourceAccessor = Objects.requireNonNull(resourceAccessor);
        this.settingsManager = Objects.requireNonNull(settingsManager);
        this.confluenceWebResourceService = Objects.requireNonNull(confluenceWebResourceService);
        this.webResourceAssemblerFactory = Objects.requireNonNull(webResourceAssemblerFactory);
    }

    @PostConstruct
    public void init() {
        this.cssFilter = new CssWebResource();
        this.jsFormatter = new JavascriptWebResource();
        this.defaultFilter = new DefaultWebResourceFilter();
    }

    @Override
    public void includeResources(Writer writer) {
        this.includeResources(writer, UrlMode.AUTO);
    }

    @Override
    public String getRequiredResources() {
        return this.getRequiredResources(UrlMode.AUTO);
    }

    @Override
    public void requireResource(String moduleCompleteKey, Writer writer) {
        this.requireResource(moduleCompleteKey, writer, UrlMode.AUTO);
    }

    @Override
    public String getResourceTags(String moduleCompleteKey) {
        return this.getResourceTags(moduleCompleteKey, UrlMode.AUTO);
    }

    @Override
    public String getStaticResourcePrefix() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO);
    }

    @Override
    public String getStaticResourcePrefix(UrlMode urlMode) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(urlMode);
    }

    @Override
    public String getStaticResourcePrefix(String resourceCounter) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(resourceCounter, UrlMode.AUTO);
    }

    @Override
    public String getStaticResourcePrefix(String resourceCounter, UrlMode urlMode) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(resourceCounter, urlMode);
    }

    @Override
    @HtmlSafe
    public String getResources() {
        return this.getRequiredResources(UrlMode.AUTO, this.defaultFilter);
    }

    @Override
    @HtmlSafe
    public String getCssResources() {
        return this.getCssResources(null);
    }

    @Override
    @HtmlSafe
    public String getCssResources(String spaceKey) {
        String resource = this.getCommonCssResources();
        if (resource == null) {
            return null;
        }
        StringBuilder writer = new StringBuilder(resource);
        this.confluenceWebResourceService.writeCombinedCssTags(writer, spaceKey, null);
        return writer.toString();
    }

    private String getCommonCssResources() {
        Map cache = this.webResourceIntegration.getRequestCache();
        if (cache.get(REQUEST_CACHE_CSS_ADDED) != null) {
            log.info("CSS requested more than once in a request");
            return null;
        }
        cache.put(REQUEST_CACHE_CSS_ADDED, true);
        return this.getRequiredResources(UrlMode.AUTO, (WebResourceFilter)this.cssFilter);
    }

    @Override
    @HtmlSafe
    public String getJsResources() {
        Map cache = this.webResourceIntegration.getRequestCache();
        if (cache.get(REQUEST_CACHE_JS_ADDED) != null) {
            log.info("Javascripts requested more than once in a request");
            return null;
        }
        cache.put(REQUEST_CACHE_JS_ADDED, true);
        return this.getRequiredResources(UrlMode.AUTO, (WebResourceFilter)this.jsFormatter);
    }

    @Override
    @HtmlSafe
    public String getThemeJsResources(String spaceKey) {
        StringBuilder result = new StringBuilder();
        this.confluenceWebResourceService.writeThemeJsTags(result, spaceKey);
        return result.toString();
    }

    public void requireResource(String resourceName) {
        log.info("Requiring delayed resource: " + resourceName);
        super.requireResource(resourceName);
    }

    @Override
    public String getGlobalCssResourcePrefix() {
        return this.confluenceWebResourceService.getGlobalCssResourcePrefix();
    }

    @Override
    public String getSpaceCssPrefix(String spaceKey) {
        return this.confluenceWebResourceService.getSpaceCssPrefix(spaceKey);
    }

    @Override
    public String getResourceContent(String resourceName) {
        ModuleDescriptor descriptor = this.pluginAccessor.getEnabledPluginModule(resourceName);
        if (descriptor == null) {
            return "/* Error loading resource \"" + resourceName + "\".  Resource not found */\n";
        }
        StringBuilder sb = new StringBuilder();
        for (ResourceDescriptor resourceDescriptor : descriptor.getResourceDescriptors()) {
            try {
                InputStream is = this.getResourceStream(descriptor.getPlugin(), resourceDescriptor);
                try {
                    if (is == null) {
                        sb.append("/* Could not locate resource: ").append(resourceDescriptor.getLocation()).append(" */\n");
                        continue;
                    }
                    String content = CharStreams.toString((Readable)new InputStreamReader(is, this.settingsManager.getGlobalSettings().getDefaultEncoding()));
                    sb.append(content).append("\n");
                }
                finally {
                    if (is == null) continue;
                    is.close();
                }
            }
            catch (IOException e) {
                log.error(e.toString(), (Throwable)e);
                sb.append("/* Error reading resource at \"").append(resourceDescriptor.getLocation()).append("\" */\n");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean putMetadata(String key, String value) {
        Map cache = this.webResourceIntegration.getRequestCache();
        if (cache.get(REQUEST_CACHE_METADATA_ADDED) != null) {
            log.debug("Web Metadata already retrieved for this request - could not add key/value pair: '{}' / '{}'", (Object[])new String[]{key, value});
            return false;
        }
        this.getMetadataMap().put(key, value);
        return true;
    }

    @Override
    @HtmlSafe
    public Map<String, String> getMetadata() {
        Map cache = this.webResourceIntegration.getRequestCache();
        if (cache.get(REQUEST_CACHE_METADATA_ADDED) != null) {
            log.warn("Web Metadata cannot be retrieved more than once in a request");
            return null;
        }
        cache.put(REQUEST_CACHE_METADATA_ADDED, true);
        return this.getMetadataMap();
    }

    @Override
    @HtmlSafe
    public String getAdminCssResources() {
        String commonCss = this.getCommonCssResources();
        if (commonCss == null) {
            return null;
        }
        StringBuilder writer = new StringBuilder(commonCss);
        this.confluenceWebResourceService.writeAdminCssTags(writer, "");
        return writer.toString();
    }

    @Override
    @HtmlSafe
    public String getEditorCssResources(String spaceKey) {
        StringWriter resources = new StringWriter();
        WebResourceAssembler builder = this.webResourceAssemblerFactory.create().includeSuperbatchResources(false).build();
        builder.resources().requireContext("editor-content");
        WebResourceSet set = builder.assembled().drainIncludedResources();
        set.writeHtmlTags((Writer)resources, com.atlassian.webresource.api.UrlMode.AUTO, PluginCssResource.class::isInstance);
        this.confluenceWebResourceService.writeCombinedCssTags(resources, spaceKey, null);
        return resources.toString();
    }

    private Map<String, String> getMetadataMap() {
        Map cache = this.webResourceIntegration.getRequestCache();
        LinkedHashMap metadataMap = (LinkedHashMap)cache.get(REQUEST_CACHE_METADATA_KEY);
        if (metadataMap == null) {
            metadataMap = new LinkedHashMap();
            cache.put(REQUEST_CACHE_METADATA_KEY, metadataMap);
        }
        return metadataMap;
    }

    private InputStream getResourceStream(Plugin plugin, ResourceDescriptor resourceDescriptor) {
        String location = resourceDescriptor.getLocation();
        if ("webContextStatic".equalsIgnoreCase(resourceDescriptor.getParameter("source"))) {
            return this.resourceAccessor.getResource(location);
        }
        return plugin.getClassLoader().getResourceAsStream(location);
    }

    @Override
    public void requireResourcesForContext(String context) {
        log.info("Requiring context: " + context);
        super.requireResourcesForContext(context);
    }
}

