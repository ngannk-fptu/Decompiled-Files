/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.AttributeAccessor
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface BeanDefinition
extends AttributeAccessor,
BeanMetadataElement {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";
    public static final int ROLE_APPLICATION = 0;
    public static final int ROLE_SUPPORT = 1;
    public static final int ROLE_INFRASTRUCTURE = 2;

    public void setParentName(@Nullable String var1);

    @Nullable
    public String getParentName();

    public void setBeanClassName(@Nullable String var1);

    @Nullable
    public String getBeanClassName();

    public void setScope(@Nullable String var1);

    @Nullable
    public String getScope();

    public void setLazyInit(boolean var1);

    public boolean isLazyInit();

    public void setDependsOn(String ... var1);

    @Nullable
    public String[] getDependsOn();

    public void setAutowireCandidate(boolean var1);

    public boolean isAutowireCandidate();

    public void setPrimary(boolean var1);

    public boolean isPrimary();

    public void setFactoryBeanName(@Nullable String var1);

    @Nullable
    public String getFactoryBeanName();

    public void setFactoryMethodName(@Nullable String var1);

    @Nullable
    public String getFactoryMethodName();

    public ConstructorArgumentValues getConstructorArgumentValues();

    default public boolean hasConstructorArgumentValues() {
        return !this.getConstructorArgumentValues().isEmpty();
    }

    public MutablePropertyValues getPropertyValues();

    default public boolean hasPropertyValues() {
        return !this.getPropertyValues().isEmpty();
    }

    public void setInitMethodName(@Nullable String var1);

    @Nullable
    public String getInitMethodName();

    public void setDestroyMethodName(@Nullable String var1);

    @Nullable
    public String getDestroyMethodName();

    public void setRole(int var1);

    public int getRole();

    public void setDescription(@Nullable String var1);

    @Nullable
    public String getDescription();

    public ResolvableType getResolvableType();

    public boolean isSingleton();

    public boolean isPrototype();

    public boolean isAbstract();

    @Nullable
    public String getResourceDescription();

    @Nullable
    public BeanDefinition getOriginatingBeanDefinition();
}

