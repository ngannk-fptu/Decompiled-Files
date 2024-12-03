/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import java.io.StringWriter;
import java.io.Writer;

public class VelocityFriendlyPageBuilderService {
    private final PageBuilderService pageBuilderService;
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final ConfluenceWebResourceService confluenceWebResourceService;

    public VelocityFriendlyPageBuilderService(PageBuilderService pageBuilderService, WebResourceAssemblerFactory webResourceAssemblerFactory, WebResourceUrlProvider webResourceUrlProvider, ConfluenceWebResourceService confluenceWebResourceService) {
        this.pageBuilderService = pageBuilderService;
        this.webResourceAssemblerFactory = webResourceAssemblerFactory;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.confluenceWebResourceService = confluenceWebResourceService;
    }

    public void requireResource(String moduleCompleteKey) {
        this.pageBuilderService.assembler().resources().requireWebResource(moduleCompleteKey);
    }

    public void requireResourcesForContext(String context) {
        this.pageBuilderService.assembler().resources().requireContext(context);
    }

    public void includeResources(Writer writer) {
        this.pageBuilderService.assembler().assembled().drainIncludedResources().writeHtmlTags(writer, com.atlassian.webresource.api.UrlMode.AUTO);
    }

    @HtmlSafe
    public String getRequiredResources() {
        StringWriter stringWriter = new StringWriter();
        this.includeResources(stringWriter);
        return stringWriter.toString();
    }

    public void requireResource(String moduleCompleteKey, Writer writer) {
        WebResourceAssembler assembler = this.webResourceAssemblerFactory.create().includeSuperbatchResources(false).build();
        assembler.resources().requireWebResource(moduleCompleteKey);
        assembler.assembled().drainIncludedResources().writeHtmlTags(writer, com.atlassian.webresource.api.UrlMode.AUTO);
    }

    @HtmlSafe
    public String getResourceTags(String moduleCompleteKey) {
        StringWriter stringWriter = new StringWriter();
        this.requireResource(moduleCompleteKey, stringWriter);
        return stringWriter.toString();
    }

    @HtmlSafe
    public String getStaticResourcePrefix() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO);
    }

    @HtmlSafe
    public String getStaticResourcePrefix(String resourceCounter) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(resourceCounter, UrlMode.AUTO);
    }

    @HtmlSafe
    public String getStaticResourcePrefix(String resourceCounter, UrlMode urlMode) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(resourceCounter, urlMode);
    }

    @HtmlSafe
    public String getStaticPluginResource(String moduleCompleteKey, String resourceName) {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleCompleteKey, resourceName, UrlMode.AUTO);
    }

    @HtmlSafe
    public String getStaticPluginResource(ModuleDescriptor<?> moduleDescriptor, String resourceName) {
        return this.webResourceUrlProvider.getStaticPluginResourceUrl(moduleDescriptor, resourceName, UrlMode.AUTO);
    }

    @HtmlSafe
    public String getConfluenceResourceTags(String styleSheetAction, String spaceKey) {
        HttpRequestStats.elapse("confluenceResourceTagsStart");
        try {
            String string;
            block9: {
                Ticker ignored = Timers.start((String)"VelocityFriendlyPageBuilderService.getConfluenceResourceTags");
                try {
                    ConfluenceWebResourceService.Style style = "admin".equals(styleSheetAction) ? ConfluenceWebResourceService.Style.ADMIN : null;
                    StringWriter writer = new StringWriter();
                    this.confluenceWebResourceService.writeConfluenceResourceTags(writer, style, spaceKey);
                    string = writer.toString();
                    if (ignored == null) break block9;
                }
                catch (Throwable throwable) {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ignored.close();
            }
            return string;
        }
        finally {
            HttpRequestStats.elapse("confluenceResourceTagsEnd");
        }
    }
}

