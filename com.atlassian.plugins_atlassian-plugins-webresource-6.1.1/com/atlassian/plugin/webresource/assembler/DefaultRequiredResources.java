/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.RequiredResources
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.webresource.assembler;

import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.helpers.url.UrlGenerationHelpers;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.api.assembler.RequiredResources;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

class DefaultRequiredResources
implements RequiredResources {
    private final RequestState requestState;

    DefaultRequiredResources(@Nonnull RequestState requestState) {
        this.requestState = Objects.requireNonNull(requestState, "The request state object is mandatory to build a required resources.");
    }

    @Nonnull
    @Deprecated
    public RequiredResources requireWebResource(@Nonnull String completeKey) {
        return this.requireWebResource(ResourcePhase.defaultPhase(), completeKey);
    }

    @Nonnull
    public RequiredResources requireWebResource(@Nonnull ResourcePhase resourcePhase, @Nonnull String completeKey) {
        if (Config.isNotWebResourceKey(completeKey)) {
            Support.LOGGER.warn("requiring something that doesn't look like the web resource \"{}\", it will be ignored.", (Object)completeKey);
            return this;
        }
        this.requestState.getRawRequest().include(resourcePhase, new WebResourceKey(completeKey));
        return this;
    }

    @Nonnull
    @Deprecated
    public RequiredResources requireModule(@Nonnull String name) {
        return this.requireModule(ResourcePhase.defaultPhase(), name);
    }

    @Nonnull
    public RequiredResources requireModule(@Nonnull ResourcePhase resourcePhase, @Nonnull String name) {
        if (Config.isWebResourceKey(name)) {
            Support.LOGGER.warn("requiring web resource \"{}\" as a module, it will be ignored.", (Object)name);
        }
        return this;
    }

    @Nonnull
    @Deprecated
    public RequiredResources requireContext(@Nonnull String context) {
        return this.requireContext(ResourcePhase.defaultPhase(), context);
    }

    @Nonnull
    public RequiredResources excludeSuperbatch() {
        this.requestState.getSuperbatchConfiguration().setEnabled(false);
        return this;
    }

    @Nonnull
    public RequiredResources requireSuperbatch(@Nonnull ResourcePhase resourcePhase) {
        this.requestState.getSuperbatchConfiguration().setEnabled(true);
        this.requestState.getSuperbatchConfiguration().setResourcePhase(resourcePhase);
        return this;
    }

    @Nonnull
    public RequiredResources requireContext(@Nonnull ResourcePhase resourcePhase, @Nonnull String context) {
        this.requestState.getRawRequest().include(resourcePhase, new WebResourceContextKey(context));
        return this;
    }

    @Nonnull
    public RequiredResources exclude(@Nullable Set<String> excludeWebResources, @Nullable Set<String> excludeContexts) {
        HashSet requestablesToExclude = new HashSet();
        Optional.ofNullable(excludeWebResources).map(Collection::stream).orElseGet(Stream::empty).filter(StringUtils::isNotBlank).map(WebResourceKey::new).forEach(requestablesToExclude::add);
        Optional.ofNullable(excludeContexts).map(Collection::stream).orElseGet(Stream::empty).filter(StringUtils::isNotBlank).map(WebResourceContextKey::new).forEach(requestablesToExclude::add);
        Collection unresolvedExcludedKeys = requestablesToExclude.stream().map(Requestable::toLooseType).collect(Collectors.toCollection(HashSet::new));
        Set<String> resolvedExcludedKeys = UrlGenerationHelpers.resolveExcluded(this.requestState.getRequestCache(), this.requestState.getGlobals().getUrlCache(), this.requestState.getUrlStrategy(), unresolvedExcludedKeys, this.requestState.getExcluded());
        Set<Requestable> resolvedExcludedRequestables = resolvedExcludedKeys.stream().map(key -> key.contains("_context") ? new WebResourceContextKey((String)key) : new WebResourceKey((String)key)).collect(Collectors.toSet());
        this.requestState.getRawRequest().clearExcluded();
        this.requestState.getRawRequest().exclude(resolvedExcludedRequestables);
        return this;
    }

    @Nonnull
    @Deprecated
    public RequiredResources requirePage(@Nonnull String key) {
        return this.requirePage(ResourcePhase.defaultPhase(), key);
    }

    @Nonnull
    public RequiredResources requirePage(@Nonnull ResourcePhase resourcePhase, @Nonnull String key) {
        Collection includedResources = this.requestState.getSnapshot().getRootPage(key).getWebResource().getDependencies().stream().map(WebResourceKey::new).collect(Collectors.toCollection(LinkedHashSet::new));
        this.requestState.getRawRequest().include(resourcePhase, includedResources);
        return this;
    }
}

