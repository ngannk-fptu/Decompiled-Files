/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public class ChildBeanDefinition
extends AbstractBeanDefinition {
    @Nullable
    private String parentName;

    public ChildBeanDefinition(String parentName) {
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, MutablePropertyValues pvs) {
        super(null, pvs);
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
    }

    public ChildBeanDefinition(String parentName, Class<?> beanClass, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        this.setBeanClass(beanClass);
    }

    public ChildBeanDefinition(String parentName, String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
        super(cargs, pvs);
        this.parentName = parentName;
        this.setBeanClassName(beanClassName);
    }

    public ChildBeanDefinition(ChildBeanDefinition original) {
        super(original);
    }

    @Override
    public void setParentName(@Nullable String parentName) {
        this.parentName = parentName;
    }

    @Override
    @Nullable
    public String getParentName() {
        return this.parentName;
    }

    @Override
    public void validate() throws BeanDefinitionValidationException {
        super.validate();
        if (this.parentName == null) {
            throw new BeanDefinitionValidationException("'parentName' must be set in ChildBeanDefinition");
        }
    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        return new ChildBeanDefinition(this);
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ChildBeanDefinition)) {
            return false;
        }
        ChildBeanDefinition that = (ChildBeanDefinition)other;
        return ObjectUtils.nullSafeEquals((Object)this.parentName, (Object)that.parentName) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.parentName) * 29 + super.hashCode();
    }

    @Override
    public String toString() {
        return "Child bean with parent '" + this.parentName + "': " + super.toString();
    }
}

