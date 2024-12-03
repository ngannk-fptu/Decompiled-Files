/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.elements.ResourceLocation
 */
package com.atlassian.plugins.less;

import com.atlassian.plugin.elements.ResourceLocation;
import java.net.URI;
import java.net.URISyntaxException;

public class UriUtils {
    private UriUtils() {
        throw new UnsupportedOperationException();
    }

    public static URI resolvePluginUri(String pluginKey, ResourceLocation resourceLocation) {
        return UriUtils.resolvePluginUri(pluginKey, resourceLocation.getLocation());
    }

    public static URI resolvePluginUri(String pluginKey, String location) {
        try {
            return new URI("plugin", pluginKey, UriUtils.normalizeLocation(location), null);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public static URI resolveUri(String pluginKey, ResourceLocation resourceLocation) {
        String sourceParam = resourceLocation.getParameter("source");
        URI uri = "webContextStatic".equalsIgnoreCase(sourceParam) ? UriUtils.resolveWebStaticUri(resourceLocation) : UriUtils.resolvePluginUri(pluginKey, resourceLocation);
        return uri;
    }

    public static URI resolveWebStaticUri(ResourceLocation resourceLocation) {
        return UriUtils.resolveWebStaticUri(resourceLocation.getLocation());
    }

    public static URI resolveWebStaticUri(String location) {
        try {
            return new URI("webstatic", null, UriUtils.normalizeLocation(location), null);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static String normalizeLocation(String location) {
        if (!location.startsWith("/")) {
            location = "/" + location;
        }
        return location;
    }
}

