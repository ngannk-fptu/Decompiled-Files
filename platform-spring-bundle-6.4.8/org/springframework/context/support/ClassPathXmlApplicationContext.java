/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ClassPathXmlApplicationContext
extends AbstractXmlApplicationContext {
    @Nullable
    private Resource[] configResources;

    public ClassPathXmlApplicationContext() {
    }

    public ClassPathXmlApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
        this(new String[]{configLocation}, true, null);
    }

    public ClassPathXmlApplicationContext(String ... configLocations) throws BeansException {
        this(configLocations, true, null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, @Nullable ApplicationContext parent) throws BeansException {
        this(configLocations, true, parent);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
        this(configLocations, refresh, null);
    }

    public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, @Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        this.setConfigLocations(configLocations);
        if (refresh) {
            this.refresh();
        }
    }

    public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
        this(new String[]{path}, clazz);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
        this(paths, clazz, null);
    }

    public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        Assert.notNull((Object)paths, "Path array must not be null");
        Assert.notNull(clazz, "Class argument must not be null");
        this.configResources = new Resource[paths.length];
        for (int i2 = 0; i2 < paths.length; ++i2) {
            this.configResources[i2] = new ClassPathResource(paths[i2], clazz);
        }
        this.refresh();
    }

    @Override
    @Nullable
    protected Resource[] getConfigResources() {
        return this.configResources;
    }
}

