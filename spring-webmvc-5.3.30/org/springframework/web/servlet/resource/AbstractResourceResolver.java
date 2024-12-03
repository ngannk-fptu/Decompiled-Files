/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public abstract class AbstractResourceResolver
implements ResourceResolver {
    protected final Log logger = LogFactory.getLog(this.getClass());

    @Override
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return this.resolveResourceInternal(request, requestPath, locations, chain);
    }

    @Override
    @Nullable
    public String resolveUrlPath(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return this.resolveUrlPathInternal(resourceUrlPath, locations, chain);
    }

    @Nullable
    protected abstract Resource resolveResourceInternal(@Nullable HttpServletRequest var1, String var2, List<? extends Resource> var3, ResourceResolverChain var4);

    @Nullable
    protected abstract String resolveUrlPathInternal(String var1, List<? extends Resource> var2, ResourceResolverChain var3);
}

