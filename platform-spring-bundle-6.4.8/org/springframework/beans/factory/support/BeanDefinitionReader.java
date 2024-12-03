/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

public interface BeanDefinitionReader {
    public BeanDefinitionRegistry getRegistry();

    @Nullable
    public ResourceLoader getResourceLoader();

    @Nullable
    public ClassLoader getBeanClassLoader();

    public BeanNameGenerator getBeanNameGenerator();

    public int loadBeanDefinitions(Resource var1) throws BeanDefinitionStoreException;

    public int loadBeanDefinitions(Resource ... var1) throws BeanDefinitionStoreException;

    public int loadBeanDefinitions(String var1) throws BeanDefinitionStoreException;

    public int loadBeanDefinitions(String ... var1) throws BeanDefinitionStoreException;
}

