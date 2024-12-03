/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.tiles.request.ApplicationResource
 *  org.apache.tiles.request.locale.URLApplicationResource
 *  org.apache.tiles.request.servlet.ServletApplicationContext
 */
package org.springframework.web.servlet.view.tiles3;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import javax.servlet.ServletContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class SpringWildcardServletTilesApplicationContext
extends ServletApplicationContext {
    private final ResourcePatternResolver resolver;

    public SpringWildcardServletTilesApplicationContext(ServletContext servletContext) {
        super(servletContext);
        this.resolver = new ServletContextResourcePatternResolver(servletContext);
    }

    @Nullable
    public ApplicationResource getResource(String localePath) {
        Collection<ApplicationResource> urlSet = this.getResources(localePath);
        if (!CollectionUtils.isEmpty(urlSet)) {
            return urlSet.iterator().next();
        }
        return null;
    }

    @Nullable
    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        Collection<ApplicationResource> urlSet = this.getResources(base.getLocalePath(locale));
        if (!CollectionUtils.isEmpty(urlSet)) {
            return urlSet.iterator().next();
        }
        return null;
    }

    public Collection<ApplicationResource> getResources(String path) {
        Object[] resources2;
        try {
            resources2 = this.resolver.getResources(path);
        }
        catch (IOException ex) {
            ((ServletContext)this.getContext()).log("Resource retrieval failed for path: " + path, (Throwable)ex);
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(resources2)) {
            ((ServletContext)this.getContext()).log("No resources found for path pattern: " + path);
            return Collections.emptyList();
        }
        ArrayList<ApplicationResource> resourceList = new ArrayList<ApplicationResource>(resources2.length);
        for (Object resource : resources2) {
            try {
                URL url = resource.getURL();
                resourceList.add((ApplicationResource)new URLApplicationResource(url.toExternalForm(), url));
            }
            catch (IOException ex) {
                throw new IllegalArgumentException("No URL for " + resource, ex);
            }
        }
        return resourceList;
    }
}

