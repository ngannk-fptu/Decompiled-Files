/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.CssWebResource
 *  com.atlassian.plugin.webresource.JavascriptWebResource
 *  com.atlassian.plugin.webresource.WebResourceFilter
 *  com.atlassian.plugin.webresource.assembler.DefaultWebResourceSet
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.AssembledResources
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.google.common.base.Predicates
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.confluence.plugins.mobile.webresource.MobileResourceServerServletUrlRewriter;
import com.atlassian.confluence.plugins.mobile.webresource.WebResourceSupplier;
import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.atlassian.plugin.webresource.assembler.DefaultWebResourceSet;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.AssembledResources;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.google.common.base.Predicates;
import java.io.StringWriter;
import java.io.Writer;

public class ConfluenceMobileWebResourceSupplier
implements WebResourceSupplier {
    private static final WebResourceFilter CSS_FILTER = new CssWebResource();
    private static final WebResourceFilter JS_FILTER = new JavascriptWebResource();
    private final PageBuilderService pageBuilderService;

    private ConfluenceMobileWebResourceSupplier(PageBuilderService pageBuilderService) {
        this.pageBuilderService = pageBuilderService;
    }

    @Override
    @HtmlSafe
    public String getCssResourcesHtml() {
        return this.getResources(CSS_FILTER);
    }

    @Override
    @HtmlSafe
    public String getJsResourcesHtml() {
        return this.getResources(JS_FILTER);
    }

    private String getResources(WebResourceFilter filter) {
        WebResourceAssembler assembler = this.pageBuilderService.assembler();
        AssembledResources assembled = assembler.assembled();
        DefaultWebResourceSet webResourceSet = (DefaultWebResourceSet)assembled.peek();
        StringWriter writer = new StringWriter();
        webResourceSet.writeHtmlTags((Writer)writer, UrlMode.RELATIVE, Predicates.alwaysTrue(), input -> filter.matches(input.getName()));
        return MobileResourceServerServletUrlRewriter.apply(writer.toString());
    }
}

