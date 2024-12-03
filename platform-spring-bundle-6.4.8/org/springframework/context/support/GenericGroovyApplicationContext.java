/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovySystem
 *  groovy.lang.MetaClass
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
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class GenericGroovyApplicationContext
extends GenericApplicationContext
implements GroovyObject {
    private final GroovyBeanDefinitionReader reader = new GroovyBeanDefinitionReader(this);
    private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(this.getClass());

    public GenericGroovyApplicationContext() {
    }

    public GenericGroovyApplicationContext(Resource ... resources2) {
        this.load(resources2);
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
        this.reader.setEnvironment(this.getEnvironment());
    }

    public void load(Resource ... resources2) {
        this.reader.loadBeanDefinitions(resources2);
    }

    public void load(String ... resourceLocations) {
        this.reader.loadBeanDefinitions(resourceLocations);
    }

    public void load(Class<?> relativeClass, String ... resourceNames) {
        Resource[] resources2 = new Resource[resourceNames.length];
        for (int i2 = 0; i2 < resourceNames.length; ++i2) {
            resources2[i2] = new ClassPathResource(resourceNames[i2], relativeClass);
        }
        this.load(resources2);
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

