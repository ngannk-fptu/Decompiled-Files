/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public interface ResourceResolver {
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest var1, String var2, List<? extends Resource> var3, ResourceResolverChain var4);

    @Nullable
    public String resolveUrlPath(String var1, List<? extends Resource> var2, ResourceResolverChain var3);
}

