/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.List;
import org.eclipse.gemini.blueprint.blueprint.reflect.MetadataUtils;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleComponentMetadata;
import org.eclipse.gemini.blueprint.blueprint.reflect.SimpleRefMetadata;
import org.osgi.service.blueprint.reflect.BeanArgument;
import org.osgi.service.blueprint.reflect.BeanMetadata;
import org.osgi.service.blueprint.reflect.BeanProperty;
import org.osgi.service.blueprint.reflect.Target;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.StringUtils;

class SimpleBeanMetadata
extends SimpleComponentMetadata
implements BeanMetadata {
    private final List<BeanArgument> arguments;
    private final List<BeanProperty> properties;
    private final String factoryMethod;
    private final Target factoryComponent;
    private final String scope;

    public SimpleBeanMetadata(String name, BeanDefinition definition) {
        super(name, definition);
        String factoryMtd = definition.getFactoryMethodName();
        if (StringUtils.hasText((String)factoryMtd)) {
            this.factoryMethod = factoryMtd;
            String factory = definition.getFactoryBeanName();
            this.factoryComponent = StringUtils.hasText((String)factory) ? new SimpleRefMetadata(factory) : null;
        } else {
            this.factoryComponent = null;
            this.factoryMethod = null;
        }
        this.arguments = MetadataUtils.getBeanArguments(definition);
        this.properties = MetadataUtils.getBeanProperties(definition);
        boolean hasAttribute = definition.hasAttribute("org.eclipse.gemini.blueprint.blueprint.xml.bean.declared.scope");
        this.scope = hasAttribute ? (StringUtils.hasText((String)name) ? this.beanDefinition.getScope() : null) : null;
    }

    @Override
    public List<BeanArgument> getArguments() {
        return this.arguments;
    }

    @Override
    public String getClassName() {
        return this.beanDefinition.getBeanClassName();
    }

    @Override
    public String getDestroyMethod() {
        return this.beanDefinition.getDestroyMethodName();
    }

    @Override
    public Target getFactoryComponent() {
        return this.factoryComponent;
    }

    @Override
    public String getFactoryMethod() {
        return this.factoryMethod;
    }

    @Override
    public String getInitMethod() {
        return this.beanDefinition.getInitMethodName();
    }

    @Override
    public List<BeanProperty> getProperties() {
        return this.properties;
    }

    public Class<?> getRuntimeClass() {
        return this.beanDefinition.getBeanClass();
    }

    @Override
    public String getScope() {
        return this.scope;
    }
}

