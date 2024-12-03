/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public interface ResourceTransformerChain {
    public ResourceResolverChain getResolverChain();

    public Resource transform(HttpServletRequest var1, Resource var2) throws IOException;
}

