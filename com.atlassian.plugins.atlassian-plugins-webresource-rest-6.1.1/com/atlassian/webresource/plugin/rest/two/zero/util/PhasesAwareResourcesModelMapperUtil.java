/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.webresource.models.Requestable
 *  com.atlassian.plugin.webresource.models.WebResourceContextKey
 *  com.atlassian.plugin.webresource.models.WebResourceKey
 *  com.atlassian.webresource.api.assembler.resource.ResourcePhase
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webresource.plugin.rest.two.zero.util;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.webresource.models.Requestable;
import com.atlassian.plugin.webresource.models.WebResourceContextKey;
import com.atlassian.plugin.webresource.models.WebResourceKey;
import com.atlassian.webresource.api.assembler.resource.ResourcePhase;
import com.atlassian.webresource.plugin.async.model.OutputShape;
import com.atlassian.webresource.plugin.async.model.ResourceTypeAndUrl;
import com.atlassian.webresource.plugin.rest.two.zero.model.ResourceType;
import com.atlassian.webresource.plugin.rest.two.zero.model.UrlFetchableResourceJson;
import com.atlassian.webresource.plugin.rest.two.zero.model.UrlFetchableResourcesWithDataJson;
import com.atlassian.webresource.plugin.util.function.IsIEOnlyResource;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhasesAwareResourcesModelMapperUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhasesAwareResourcesModelMapperUtil.class);
    @VisibleForTesting
    public static final String WEB_RESOURCE_CONTEXT_PREFIX = "wrc!";
    private static final String WEB_RESOURCE_PREFIX = "wr!";

    private PhasesAwareResourcesModelMapperUtil() throws Exception {
        throw new Exception("Static util class is not meant to be instantiated");
    }

    public static <T extends Requestable> Map<ResourcePhase, Set<T>> byPhase(Set<T> require, Set<T> requireForInteraction) {
        HashMap<ResourcePhase, Set<T>> requestablesPerPhase = new HashMap<ResourcePhase, Set<T>>();
        requestablesPerPhase.put(ResourcePhase.REQUIRE, require);
        requestablesPerPhase.put(ResourcePhase.INTERACTION, requireForInteraction);
        return requestablesPerPhase;
    }

    public static UrlFetchableResourcesWithDataJson transformOutputShapeToUrlFetchableResourcesWithDataJson(@Nonnull OutputShape outputShape) {
        return new UrlFetchableResourcesWithDataJson(outputShape.getResources().stream().filter(resource -> {
            boolean isIEOnly = IsIEOnlyResource.getInstance().test(resource.getPluginUrlResource());
            if (isIEOnly && LOGGER.isDebugEnabled()) {
                String warnMessage = "The resource {} is IE-only." + "IE-only resources will be deprecated and will not be used anymore in the future." + "Please, consider removing this resource in the future.";
                LOGGER.debug(warnMessage, (Object)resource.getKey());
            }
            return !isIEOnly;
        }).map(PhasesAwareResourcesModelMapperUtil::transformResourceTypeAndUrlToUrlFetchableResourceJson).collect(Collectors.toList()), outputShape.getUnparsedData(), outputShape.getUnparsedErrors());
    }

    private static UrlFetchableResourceJson transformResourceTypeAndUrlToUrlFetchableResourceJson(@Nonnull ResourceTypeAndUrl resourceTypeAndUrl) {
        return new UrlFetchableResourceJson(resourceTypeAndUrl.getBatchType(), resourceTypeAndUrl.getKey(), ResourceType.valueOf(resourceTypeAndUrl.getResourceType().name().toUpperCase()), resourceTypeAndUrl.getUrl());
    }

    public static <T extends Requestable> Set<T> transformStringsToRequestable(Collection<String> requestableStrings, String identifyingPrefix, Function<String, T> constructor) {
        return requestableStrings.stream().filter(requestableString -> requestableString.startsWith(identifyingPrefix)).map(requestableString -> requestableString.replaceFirst(identifyingPrefix, "")).filter(StringUtils::isNotBlank).map(constructor).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<WebResourceContextKey> transformStringsToFallbackRequestable(Collection<String> requestableStrings) {
        return requestableStrings.stream().filter(requestableString -> !requestableString.startsWith(WEB_RESOURCE_PREFIX) && !requestableString.startsWith(WEB_RESOURCE_CONTEXT_PREFIX)).filter(StringUtils::isNotBlank).map(WebResourceContextKey::new).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<WebResourceKey> transformStringsToWebResourceKeysSet(Collection<String> requestableStrings) {
        return PhasesAwareResourcesModelMapperUtil.transformStringsToRequestable(requestableStrings, WEB_RESOURCE_PREFIX, WebResourceKey::new);
    }

    public static Set<WebResourceContextKey> transformStringsToWebResourceContextKeysSet(Collection<String> requestableStrings) {
        Set<WebResourceContextKey> setOfWebResourceContextKeys = PhasesAwareResourcesModelMapperUtil.transformStringsToRequestable(requestableStrings, WEB_RESOURCE_CONTEXT_PREFIX, WebResourceContextKey::new);
        setOfWebResourceContextKeys.addAll(PhasesAwareResourcesModelMapperUtil.transformStringsToFallbackRequestable(requestableStrings));
        return setOfWebResourceContextKeys;
    }
}

