/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.CssWebResource
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceFilter
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PdfResourceManager {
    private static final Logger logger = LoggerFactory.getLogger(PdfResourceManager.class);
    private static final WebResourceFilter CSS_FILTER = new CssWebResource();
    private static final WebResourceFilter PDF_RESOURCE_FILTER = s -> CSS_FILTER.matches(s) && !s.endsWith("css/batch.css") && !s.startsWith("_super");
    private final DarkFeatureManager darkFeatureManager;
    private final ConfluenceWebResourceManager webResourceManager;
    private final WebResourceAssemblerFactory assemblerFactory;
    private boolean isRequireResourcesForPdf = false;

    public PdfResourceManager(@ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ConfluenceWebResourceManager webResourceManager, @ComponentImport WebResourceAssemblerFactory assemblerFactory) {
        this.darkFeatureManager = darkFeatureManager;
        this.webResourceManager = webResourceManager;
        this.assemblerFactory = assemblerFactory;
    }

    public void requireResourcesForPdf() {
        if (this.oldBehaviour()) {
            this.webResourceManager.requireResourcesForContext("pdf-export");
        } else {
            this.isRequireResourcesForPdf = true;
        }
    }

    private WebResourceAssembler createWebResourceAssembler() {
        WebResourceAssembler assembler = this.assemblerFactory.create().includeSuperbatchResources(false).build();
        if (this.isRequireResourcesForPdf) {
            assembler.resources().requireContext("pdf-export");
            assembler.resources().requireWebResource("confluence.extra.information:information-plugin-adg-styles");
            assembler.resources().exclude(Collections.emptySet(), Collections.singleton("_super"));
        }
        return assembler;
    }

    @HtmlSafe
    public String getResources() {
        try {
            if (this.oldBehaviour()) {
                return this.webResourceManager.getRequiredResources(UrlMode.AUTO, PDF_RESOURCE_FILTER);
            }
            StringWriter sb = new StringWriter();
            this.createWebResourceAssembler().assembled().peek().writeHtmlTags((Writer)sb, com.atlassian.webresource.api.UrlMode.AUTO, PluginCssResource.class::isInstance);
            return sb.toString();
        }
        catch (Exception e) {
            logger.debug("Error getting resources for PDF export", (Throwable)e);
            throw e;
        }
    }

    private boolean oldBehaviour() {
        return this.darkFeatureManager.isEnabledForAllUsers("CONFSRVDEV-21490.old.behaviour.enabled").orElse(false);
    }
}

