/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssembler
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.assembler.LegacyPageBuilderService;
import com.atlassian.plugin.webresource.prebake.PrebakeWebResourceAssemblerFactory;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssembler;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import java.util.Map;

public class DefaultPageBuilderService
implements LegacyPageBuilderService,
PageBuilderService {
    private static final String REQUEST_CACHE_REQUIRED_RESOURCES = "plugin.webresource.required.resources";
    private final WebResourceIntegration webResourceIntegration;
    private final WebResourceAssemblerFactory webResourceAssemblerFactory;

    public DefaultPageBuilderService(WebResourceIntegration webResourceIntegration, WebResourceAssemblerFactory webResourceAssemblerFactory) {
        this.webResourceIntegration = webResourceIntegration;
        this.webResourceAssemblerFactory = webResourceAssemblerFactory;
    }

    public DefaultPageBuilderService(WebResourceIntegration webResourceIntegration, PrebakeWebResourceAssemblerFactory webResourceAssemblerFactory) {
        this.webResourceIntegration = webResourceIntegration;
        this.webResourceAssemblerFactory = webResourceAssemblerFactory;
    }

    public WebResourceAssembler assembler() {
        return this.cachedAssembler();
    }

    public void seed(WebResourceAssembler assembler) {
        Map<String, Object> cache = this.webResourceIntegration.getRequestCache();
        if (cache.containsKey(REQUEST_CACHE_REQUIRED_RESOURCES)) {
            throw new IllegalStateException("Request-local WebResourceAssembler has already been initialised");
        }
        cache.put(REQUEST_CACHE_REQUIRED_RESOURCES, assembler);
    }

    private WebResourceAssembler cachedAssembler() {
        Map<String, Object> cache = this.webResourceIntegration.getRequestCache();
        WebResourceAssembler assembler = (WebResourceAssembler)cache.get(REQUEST_CACHE_REQUIRED_RESOURCES);
        if (assembler == null) {
            assembler = this.webResourceAssemblerFactory.create().build();
            cache.put(REQUEST_CACHE_REQUIRED_RESOURCES, assembler);
        }
        return assembler;
    }

    @Override
    public void clearRequestLocal() {
        this.webResourceIntegration.getRequestCache().remove(REQUEST_CACHE_REQUIRED_RESOURCES);
    }
}

