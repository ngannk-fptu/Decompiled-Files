/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

public interface ConfigurableBeanFactory
extends HierarchicalBeanFactory,
SingletonBeanRegistry {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";

    public void setParentBeanFactory(BeanFactory var1) throws IllegalStateException;

    public void setBeanClassLoader(@Nullable ClassLoader var1);

    @Nullable
    public ClassLoader getBeanClassLoader();

    public void setTempClassLoader(@Nullable ClassLoader var1);

    @Nullable
    public ClassLoader getTempClassLoader();

    public void setCacheBeanMetadata(boolean var1);

    public boolean isCacheBeanMetadata();

    public void setBeanExpressionResolver(@Nullable BeanExpressionResolver var1);

    @Nullable
    public BeanExpressionResolver getBeanExpressionResolver();

    public void setConversionService(@Nullable ConversionService var1);

    @Nullable
    public ConversionService getConversionService();

    public void addPropertyEditorRegistrar(PropertyEditorRegistrar var1);

    public void registerCustomEditor(Class<?> var1, Class<? extends PropertyEditor> var2);

    public void copyRegisteredEditorsTo(PropertyEditorRegistry var1);

    public void setTypeConverter(TypeConverter var1);

    public TypeConverter getTypeConverter();

    public void addEmbeddedValueResolver(StringValueResolver var1);

    public boolean hasEmbeddedValueResolver();

    @Nullable
    public String resolveEmbeddedValue(String var1);

    public void addBeanPostProcessor(BeanPostProcessor var1);

    public int getBeanPostProcessorCount();

    public void registerScope(String var1, Scope var2);

    public String[] getRegisteredScopeNames();

    @Nullable
    public Scope getRegisteredScope(String var1);

    public AccessControlContext getAccessControlContext();

    public void copyConfigurationFrom(ConfigurableBeanFactory var1);

    public void registerAlias(String var1, String var2) throws BeanDefinitionStoreException;

    public void resolveAliases(StringValueResolver var1);

    public BeanDefinition getMergedBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    public boolean isFactoryBean(String var1) throws NoSuchBeanDefinitionException;

    public void setCurrentlyInCreation(String var1, boolean var2);

    public boolean isCurrentlyInCreation(String var1);

    public void registerDependentBean(String var1, String var2);

    public String[] getDependentBeans(String var1);

    public String[] getDependenciesForBean(String var1);

    public void destroyBean(String var1, Object var2);

    public void destroyScopedBean(String var1);

    public void destroySingletons();
}

