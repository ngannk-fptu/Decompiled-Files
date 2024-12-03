/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractBeanDefinitionReader
implements BeanDefinitionReader,
EnvironmentCapable {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final BeanDefinitionRegistry registry;
    @Nullable
    private ResourceLoader resourceLoader;
    @Nullable
    private ClassLoader beanClassLoader;
    private Environment environment;
    private BeanNameGenerator beanNameGenerator = DefaultBeanNameGenerator.INSTANCE;

    protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull((Object)registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        this.resourceLoader = this.registry instanceof ResourceLoader ? (ResourceLoader)((Object)this.registry) : new PathMatchingResourcePatternResolver();
        this.environment = this.registry instanceof EnvironmentCapable ? ((EnvironmentCapable)((Object)this.registry)).getEnvironment() : new StandardEnvironment();
    }

    public final BeanDefinitionRegistry getBeanFactory() {
        return this.registry;
    }

    @Override
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    @Nullable
    public ResourceLoader getResourceLoader() {
        return this.resourceLoader;
    }

    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void setEnvironment(Environment environment2) {
        Assert.notNull((Object)environment2, "Environment must not be null");
        this.environment = environment2;
    }

    @Override
    public Environment getEnvironment() {
        return this.environment;
    }

    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = beanNameGenerator != null ? beanNameGenerator : DefaultBeanNameGenerator.INSTANCE;
    }

    @Override
    public BeanNameGenerator getBeanNameGenerator() {
        return this.beanNameGenerator;
    }

    @Override
    public int loadBeanDefinitions(Resource ... resources) throws BeanDefinitionStoreException {
        Assert.notNull((Object)resources, "Resource array must not be null");
        int count = 0;
        for (Resource resource : resources) {
            count += this.loadBeanDefinitions(resource);
        }
        return count;
    }

    @Override
    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(location, (Set<Resource>)null);
    }

    public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
        ResourceLoader resourceLoader = this.getResourceLoader();
        if (resourceLoader == null) {
            throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
        }
        if (resourceLoader instanceof ResourcePatternResolver) {
            try {
                Resource[] resources = ((ResourcePatternResolver)resourceLoader).getResources(location);
                int count = this.loadBeanDefinitions(resources);
                if (actualResources != null) {
                    Collections.addAll(actualResources, resources);
                }
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
                }
                return count;
            }
            catch (IOException ex) {
                throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
            }
        }
        Resource resource = resourceLoader.getResource(location);
        int count = this.loadBeanDefinitions(resource);
        if (actualResources != null) {
            actualResources.add(resource);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
        }
        return count;
    }

    @Override
    public int loadBeanDefinitions(String ... locations) throws BeanDefinitionStoreException {
        Assert.notNull((Object)locations, "Location array must not be null");
        int count = 0;
        for (String location : locations) {
            count += this.loadBeanDefinitions(location);
        }
        return count;
    }
}

