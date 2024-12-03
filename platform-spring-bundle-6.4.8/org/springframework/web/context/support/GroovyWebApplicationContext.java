/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovySystem
 *  groovy.lang.MetaClass
 */
package org.springframework.web.context.support;

import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.IOException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.AbstractRefreshableWebApplicationContext;

public class GroovyWebApplicationContext
extends AbstractRefreshableWebApplicationContext
implements GroovyObject {
    public static final String DEFAULT_CONFIG_LOCATION = "/WEB-INF/applicationContext.groovy";
    public static final String DEFAULT_CONFIG_LOCATION_PREFIX = "/WEB-INF/";
    public static final String DEFAULT_CONFIG_LOCATION_SUFFIX = ".groovy";
    private final BeanWrapper contextWrapper = new BeanWrapperImpl(this);
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(this.getClass());

    @Override
    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
        GroovyBeanDefinitionReader beanDefinitionReader = new GroovyBeanDefinitionReader(beanFactory);
        beanDefinitionReader.setEnvironment(this.getEnvironment());
        beanDefinitionReader.setResourceLoader(this);
        this.initBeanDefinitionReader(beanDefinitionReader);
        this.loadBeanDefinitions(beanDefinitionReader);
    }

    protected void initBeanDefinitionReader(GroovyBeanDefinitionReader beanDefinitionReader) {
    }

    protected void loadBeanDefinitions(GroovyBeanDefinitionReader reader) throws IOException {
        String[] configLocations = this.getConfigLocations();
        if (configLocations != null) {
            for (String configLocation : configLocations) {
                reader.loadBeanDefinitions(configLocation);
            }
        }
    }

    @Override
    protected String[] getDefaultConfigLocations() {
        if (this.getNamespace() != null) {
            return new String[]{DEFAULT_CONFIG_LOCATION_PREFIX + this.getNamespace() + DEFAULT_CONFIG_LOCATION_SUFFIX};
        }
        return new String[]{DEFAULT_CONFIG_LOCATION};
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
        this.metaClass.setProperty((Object)this, property, newValue);
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

