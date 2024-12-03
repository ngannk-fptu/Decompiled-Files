/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

public interface BeanDefinitionRegistry
extends AliasRegistry {
    public void registerBeanDefinition(String var1, BeanDefinition var2) throws BeanDefinitionStoreException;

    public void removeBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    public BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    public boolean containsBeanDefinition(String var1);

    public String[] getBeanDefinitionNames();

    public int getBeanDefinitionCount();

    public boolean isBeanNameInUse(String var1);
}

