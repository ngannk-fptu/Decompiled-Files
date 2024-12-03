/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovySystem
 *  groovy.lang.MetaClass
 *  org.springframework.beans.BeanWrapper
 *  org.springframework.beans.BeanWrapperImpl
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.env.ConfigurableEnvironment
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class GenericGroovyApplicationContext
extends GenericApplicationContext
implements GroovyObject {
    private final GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader((BeanDefinitionRegistry)this);
    private final BeanWrapper contextWrapper = new BeanWrapperImpl((Object)this);
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(this.getClass());

    public GenericGroovyApplicationContext() {
    }

    public GenericGroovyApplicationContext(Resource ... resources) {
        this.load(resources);
        this.refresh();
    }

    public GenericGroovyApplicationContext(String ... resourceLocations) {
        this.load(resourceLocations);
        this.refresh();
    }

    public GenericGroovyApplicationContext(Class<?> relativeClass, String ... resourceNames) {
        this.load(relativeClass, resourceNames);
        this.refresh();
    }

    public final GroovyBeanDefinitionReader getReader() {
        return this.reader;
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment2) {
        super.setEnvironment(environment2);
        this.reader.setEnvironment((Environment)this.getEnvironment());
    }

    public void load(Resource ... resources) {
        this.reader.loadBeanDefinitions(resources);
    }

    public void load(String ... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String ... resourceNames) {
        Resource[] resources = new Resource[resourceNames.length];
        for (int i = 0; i < resourceNames.length; ++i) {
            resources[i] = new ClassPathResource(resourceNames[i], relativeClass);
        }
        this.load(resources);
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    public Object invokeMethod(String name, Object args) {
        return this.metaClass.invokeMethod((Object)this, name, args);
    }

    public void setProperty(String property, Object newValue) {
        if (newValue instanceof BeanDefinition) {
            this.registerBeanDefinition(property, (BeanDefinition)newValue);
        } else {
            this.metaClass.setProperty((Object)this, property, newValue);
        }
    }

    @Nullable
    public Object getProperty(String property) {
        if (this.containsBean(property)) {
            return this.getBean(property);
        }
        if (this.contextWrapper.isReadableProperty(property)) {
            return this.contextWrapper.getPropertyValue(property);
        }
        throw new NoSuchBeanDefinitionException(property);
    }
}

