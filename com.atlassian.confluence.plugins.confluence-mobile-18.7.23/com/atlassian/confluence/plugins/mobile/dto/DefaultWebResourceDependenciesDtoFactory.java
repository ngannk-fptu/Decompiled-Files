/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.util.StringUtils
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDto;
import com.atlassian.confluence.plugins.mobile.dto.WebResourceDependenciesDtoFactory;
import com.atlassian.confluence.plugins.mobile.webresource.WebResourceSupplier;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.springframework.util.StringUtils;

public final class DefaultWebResourceDependenciesDtoFactory
implements WebResourceDependenciesDtoFactory {
    private final PageBuilderService pageBuilderService;
    private final WebResourceSupplier mobileWebResourceSupplier;

    public DefaultWebResourceDependenciesDtoFactory(PageBuilderService pageBuilderService, WebResourceSupplier mobileWebResourceSupplier) {
        this.pageBuilderService = pageBuilderService;
        this.mobileWebResourceSupplier = mobileWebResourceSupplier;
    }

    @Override
    public WebResourceDependenciesDto getWebResourceDependenciesDto(Set<String> knownResourceKeys, Set<String> knownContexts) {
        HashSet<String> excludedContexts = new HashSet<String>(knownContexts);
        excludedContexts.add("_context:_super");
        RequiredResources requiredResources = this.pageBuilderService.assembler().resources();
        requiredResources.exclude(knownResourceKeys, excludedContexts);
        return new WebResourceDependenciesDto(this.mobileWebResourceSupplier.getCssResourcesHtml(), this.mobileWebResourceSupplier.getJsResourcesHtml());
    }

    @Override
    public WebResourceDependenciesDto getWebResourceDependenciesDto(String knownResourceKeys, String knownContexts) {
        ImmutableSet keys = null;
        keys = StringUtils.hasLength((String)knownResourceKeys) ? ImmutableSet.copyOf((Object[])StringUtils.tokenizeToStringArray((String)knownResourceKeys, (String)",")) : Collections.emptySet();
        ImmutableSet contexts = null;
        contexts = StringUtils.hasLength((String)knownContexts) ? ImmutableSet.copyOf((Object[])StringUtils.tokenizeToStringArray((String)knownContexts, (String)",")) : Collections.emptySet();
        return this.getWebResourceDependenciesDto((Set<String>)keys, (Set<String>)contexts);
    }
}

