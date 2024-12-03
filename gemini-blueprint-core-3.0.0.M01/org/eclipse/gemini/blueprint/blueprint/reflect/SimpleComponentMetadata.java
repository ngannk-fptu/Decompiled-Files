/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.blueprint.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class SimpleComponentMetadata
implements ComponentMetadata {
    private final String name;
    protected final AbstractBeanDefinition beanDefinition;
    private final List<String> dependsOn;
    private final int activation;

    public SimpleComponentMetadata(String name, BeanDefinition definition) {
        if (!(definition instanceof AbstractBeanDefinition)) {
            throw new IllegalArgumentException("Unknown bean definition passed in" + definition);
        }
        this.name = name;
        this.beanDefinition = (AbstractBeanDefinition)definition;
        Object[] dpdOn = this.beanDefinition.getDependsOn();
        if (ObjectUtils.isEmpty((Object[])dpdOn)) {
            this.dependsOn = Collections.emptyList();
        } else {
            ArrayList dependencies = new ArrayList(dpdOn.length);
            CollectionUtils.mergeArrayIntoCollection((Object)dpdOn, dependencies);
            Collection syntheticDependsOn = (Collection)this.beanDefinition.getAttribute("org.eclipse.gemini.blueprint.blueprint.container.support.internal.config.dependson");
            if (syntheticDependsOn != null) {
                dependencies.removeAll(syntheticDependsOn);
            }
            this.dependsOn = Collections.unmodifiableList(dependencies);
        }
        this.activation = !StringUtils.hasText((String)name) ? 2 : (this.beanDefinition.isSingleton() ? (this.beanDefinition.isLazyInit() ? 2 : 1) : 2);
    }

    public BeanDefinition getBeanDefinition() {
        return this.beanDefinition;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public List<String> getDependsOn() {
        return this.dependsOn;
    }

    @Override
    public int getActivation() {
        return this.activation;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.beanDefinition == null ? 0 : this.beanDefinition.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SimpleComponentMetadata) {
            SimpleComponentMetadata other = (SimpleComponentMetadata)obj;
            if (this.beanDefinition == null && other.beanDefinition != null) {
                return false;
            }
            return this.beanDefinition == other.beanDefinition;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ComponentMetadata for bean name=");
        sb.append(this.name);
        sb.append("; activation=");
        sb.append(this.activation);
        sb.append("; dependsOn=");
        sb.append(this.dependsOn);
        sb.append("; target definition");
        sb.append(this.beanDefinition);
        return sb.toString();
    }
}

