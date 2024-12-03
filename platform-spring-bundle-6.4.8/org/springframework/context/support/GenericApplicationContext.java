/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class GenericApplicationContext
extends AbstractApplicationContext
implements BeanDefinitionRegistry {
    private final DefaultListableBeanFactory beanFactory;
    @Nullable
    private ResourceLoader resourceLoader;
    private boolean customClassLoader = false;
    private final AtomicBoolean refreshed = new AtomicBoolean();

    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public GenericApplicationContext(@Nullable ApplicationContext parent) {
        this();
        this.setParent(parent);
    }

    public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
        this(beanFactory);
        this.setParent(parent);
    }

    @Override
    public void setParent(@Nullable ApplicationContext parent) {
        super.setParent(parent);
        this.beanFactory.setParentBeanFactory(this.getInternalParentBeanFactory());
    }

    @Override
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        super.setApplicationStartup(applicationStartup);
        this.beanFactory.setApplicationStartup(applicationStartup);
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.beanFactory.setAllowCircularReferences(allowCircularReferences);
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Resource getResource(String location) {
        if (this.resourceLoader != null) {
            for (ProtocolResolver protocolResolver : this.getProtocolResolvers()) {
                Resource resource = protocolResolver.resolve(location, this);
                if (resource == null) continue;
                return resource;
            }
            return this.resourceLoader.getResource(location);
        }
        return super.getResource(location);
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        if (this.resourceLoader instanceof ResourcePatternResolver) {
            return ((ResourcePatternResolver)this.resourceLoader).getResources(locationPattern);
        }
        return super.getResources(locationPattern);
    }

    @Override
    public void setClassLoader(@Nullable ClassLoader classLoader) {
        super.setClassLoader(classLoader);
        this.customClassLoader = true;
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        if (this.resourceLoader != null && !this.customClassLoader) {
            return this.resourceLoader.getClassLoader();
        }
        return super.getClassLoader();
    }

    @Override
    protected final void refreshBeanFactory() throws IllegalStateException {
        if (!this.refreshed.compareAndSet(false, true)) {
            throw new IllegalStateException("GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
        }
        this.beanFactory.setSerializationId(this.getId());
    }

    @Override
    protected void cancelRefresh(BeansException ex) {
        this.beanFactory.setSerializationId(null);
        super.cancelRefresh(ex);
    }

    @Override
    protected final void closeBeanFactory() {
        this.beanFactory.setSerializationId(null);
    }

    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return this.beanFactory;
    }

    @Override
    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        this.assertBeanFactoryActive();
        return this.beanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public boolean isBeanNameInUse(String beanName) {
        return this.beanFactory.isBeanNameInUse(beanName);
    }

    @Override
    public void registerAlias(String beanName, String alias) {
        this.beanFactory.registerAlias(beanName, alias);
    }

    @Override
    public void removeAlias(String alias) {
        this.beanFactory.removeAlias(alias);
    }

    @Override
    public boolean isAlias(String beanName) {
        return this.beanFactory.isAlias(beanName);
    }

    public <T> void registerBean(Class<T> beanClass, Object ... constructorArgs) {
        this.registerBean(null, beanClass, constructorArgs);
    }

    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Object ... constructorArgs) {
        this.registerBean(beanName, beanClass, (Supplier)null, bd -> {
            for (Object arg : constructorArgs) {
                bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
            }
        });
    }

    public final <T> void registerBean(Class<T> beanClass, BeanDefinitionCustomizer ... customizers) {
        this.registerBean((String)null, beanClass, (Supplier<T>)null, customizers);
    }

    public final <T> void registerBean(@Nullable String beanName, Class<T> beanClass, BeanDefinitionCustomizer ... customizers) {
        this.registerBean(beanName, beanClass, (Supplier<T>)null, customizers);
    }

    public final <T> void registerBean(Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer ... customizers) {
        this.registerBean(null, beanClass, supplier, customizers);
    }

    public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier, BeanDefinitionCustomizer ... customizers) {
        ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
        if (supplier != null) {
            beanDefinition.setInstanceSupplier(supplier);
        }
        for (BeanDefinitionCustomizer customizer2 : customizers) {
            customizer2.customize(beanDefinition);
        }
        String nameToUse = beanName != null ? beanName : beanClass.getName();
        this.registerBeanDefinition(nameToUse, beanDefinition);
    }

    private static class ClassDerivedBeanDefinition
    extends RootBeanDefinition {
        public ClassDerivedBeanDefinition(Class<?> beanClass) {
            super(beanClass);
        }

        public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
            super(original);
        }

        @Override
        @Nullable
        public Constructor<?>[] getPreferredConstructors() {
            Class<?> clazz = this.getBeanClass();
            Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
            if (primaryCtor != null) {
                return new Constructor[]{primaryCtor};
            }
            Constructor<?>[] publicCtors = clazz.getConstructors();
            if (publicCtors.length > 0) {
                return publicCtors;
            }
            return null;
        }

        @Override
        public RootBeanDefinition cloneBeanDefinition() {
            return new ClassDerivedBeanDefinition(this);
        }
    }
}

