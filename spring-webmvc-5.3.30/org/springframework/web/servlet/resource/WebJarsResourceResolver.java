/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.webjars.WebJarAssetLocator
 */
package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.webjars.WebJarAssetLocator;

public class WebJarsResourceResolver
extends AbstractResourceResolver {
    private static final String WEBJARS_LOCATION = "META-INF/resources/webjars/";
    private static final int WEBJARS_LOCATION_LENGTH = "META-INF/resources/webjars/".length();
    private final WebJarAssetLocator webJarAssetLocator;

    public WebJarsResourceResolver() {
        this(new WebJarAssetLocator());
    }

    public WebJarsResourceResolver(WebJarAssetLocator webJarAssetLocator) {
        this.webJarAssetLocator = webJarAssetLocator;
    }

    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String webJarResourcePath;
        Resource resolved = chain.resolveResource(request, requestPath, locations);
        if (resolved == null && (webJarResourcePath = this.findWebJarResourcePath(requestPath)) != null) {
            return chain.resolveResource(request, webJarResourcePath, locations);
        }
        return resolved;
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String webJarResourcePath;
        String path = chain.resolveUrlPath(resourceUrlPath, locations);
        if (path == null && (webJarResourcePath = this.findWebJarResourcePath(resourceUrlPath)) != null) {
            return chain.resolveUrlPath(webJarResourcePath, locations);
        }
        return path;
    }

    @Nullable
    protected String findWebJarResourcePath(String path) {
        String partialPath;
        String webjar;
        String webJarPath;
        int startOffset = path.startsWith("/") ? 1 : 0;
        int endOffset = path.indexOf(47, 1);
        if (endOffset != -1 && (webJarPath = this.webJarAssetLocator.getFullPathExact(webjar = path.substring(startOffset, endOffset), partialPath = path.substring(endOffset + 1))) != null) {
            return webJarPath.substring(WEBJARS_LOCATION_LENGTH);
        }
        return null;
    }
}

