/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.Flags
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResource
 *  com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams
 *  com.atlassian.webresource.api.assembler.resource.PluginJsResource
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webresource.plugin.async.model;

import com.atlassian.plugin.webresource.Flags;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginCssResource;
import com.atlassian.webresource.api.assembler.resource.PluginCssResourceParams;
import com.atlassian.webresource.api.assembler.resource.PluginJsResource;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ResourceType {
    JS,
    CSS;

    private static final Logger LOGGER;

    public static ResourceType getResourceType(PluginUrlResource<?> pluginUrlResource) {
        if (pluginUrlResource instanceof PluginJsResource) {
            return JS;
        }
        if (pluginUrlResource instanceof PluginCssResource) {
            ResourceType.checkLogMediaQueryError((PluginCssResource)pluginUrlResource);
            return CSS;
        }
        String errorMessage = String.format("The provided class type %s is not supported.", pluginUrlResource.getClass());
        throw new IllegalArgumentException(errorMessage);
    }

    private static void checkLogMediaQueryError(PluginCssResource cssResource) {
        String mediaQueryParameters = ((PluginCssResourceParams)cssResource.getParams()).media();
        Optional.ofNullable(mediaQueryParameters).filter(params -> StringUtils.isNotEmpty((CharSequence)params) && ObjectUtils.notEqual((Object)"all", (Object)params) && Flags.isDevMode()).ifPresent(params -> LOGGER.warn("WARN: asynchronously loading a CSS resource containing a media query: {}", (Object)cssResource.getStaticUrl(UrlMode.RELATIVE)));
    }

    static {
        LOGGER = LoggerFactory.getLogger(ResourceType.class);
    }
}

