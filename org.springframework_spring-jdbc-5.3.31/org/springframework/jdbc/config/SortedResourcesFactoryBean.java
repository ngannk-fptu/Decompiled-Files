/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 */
package org.springframework.jdbc.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

public class SortedResourcesFactoryBean
extends AbstractFactoryBean<Resource[]>
implements ResourceLoaderAware {
    private final List<String> locations;
    private ResourcePatternResolver resourcePatternResolver;

    public SortedResourcesFactoryBean(List<String> locations) {
        this.locations = locations;
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public SortedResourcesFactoryBean(ResourceLoader resourceLoader, List<String> locations) {
        this.locations = locations;
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
    }

    public Class<? extends Resource[]> getObjectType() {
        return Resource[].class;
    }

    protected Resource[] createInstance() throws Exception {
        ArrayList<Resource> scripts = new ArrayList<Resource>();
        for (String location : this.locations) {
            ArrayList<Resource> resources = new ArrayList<Resource>(Arrays.asList(this.resourcePatternResolver.getResources(location)));
            resources.sort((r1, r2) -> {
                try {
                    return r1.getURL().toString().compareTo(r2.getURL().toString());
                }
                catch (IOException ex) {
                    return 0;
                }
            });
            scripts.addAll(resources);
        }
        return scripts.toArray(new Resource[0]);
    }
}

