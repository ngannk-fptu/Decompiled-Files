/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.plugins.mobile.filter;

import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerBuilder;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SuperBatchExclusionFilter
extends AbstractHttpFilter {
    private final PageBuilderService pageBuilderService;
    private final WebResourceAssemblerBuilder webResourceAssemblerBuilder;

    public SuperBatchExclusionFilter(PageBuilderService pageBuilderService, WebResourceAssemblerFactory webResourceAssemblerFactory) {
        this.pageBuilderService = pageBuilderService;
        this.webResourceAssemblerBuilder = webResourceAssemblerFactory.create();
        this.webResourceAssemblerBuilder.includeSuperbatchResources(false);
    }

    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        this.pageBuilderService.seed(this.webResourceAssemblerBuilder.build());
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
}

