/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.resource;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;

public abstract class ResourceTransformerSupport
implements ResourceTransformer {
    @Nullable
    private ResourceUrlProvider resourceUrlProvider;

    public void setResourceUrlProvider(@Nullable ResourceUrlProvider resourceUrlProvider) {
        this.resourceUrlProvider = resourceUrlProvider;
    }

    @Nullable
    public ResourceUrlProvider getResourceUrlProvider() {
        return this.resourceUrlProvider;
    }

    @Nullable
    protected String resolveUrlPath(String resourcePath, HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) {
        if (resourcePath.startsWith("/")) {
            ResourceUrlProvider urlProvider = this.findResourceUrlProvider(request);
            return urlProvider != null ? urlProvider.getForRequestUrl(request, resourcePath) : null;
        }
        return transformerChain.getResolverChain().resolveUrlPath(resourcePath, Collections.singletonList(resource));
    }

    protected String toAbsolutePath(String path, HttpServletRequest request) {
        String absolutePath = path;
        if (!path.startsWith("/")) {
            ResourceUrlProvider urlProvider = this.findResourceUrlProvider(request);
            Assert.state(urlProvider != null, "No ResourceUrlProvider");
            String requestPath = urlProvider.getUrlPathHelper().getRequestUri(request);
            absolutePath = StringUtils.applyRelativePath(requestPath, path);
        }
        return StringUtils.cleanPath(absolutePath);
    }

    @Nullable
    private ResourceUrlProvider findResourceUrlProvider(HttpServletRequest request) {
        if (this.resourceUrlProvider != null) {
            return this.resourceUrlProvider;
        }
        return (ResourceUrlProvider)request.getAttribute(ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
    }
}

