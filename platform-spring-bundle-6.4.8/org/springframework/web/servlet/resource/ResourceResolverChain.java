/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.resource;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public interface ResourceResolverChain {
    @Nullable
    public Resource resolveResource(@Nullable HttpServletRequest var1, String var2, List<? extends Resource> var3);

    @Nullable
    public String resolveUrlPath(String var1, List<? extends Resource> var2);
}

