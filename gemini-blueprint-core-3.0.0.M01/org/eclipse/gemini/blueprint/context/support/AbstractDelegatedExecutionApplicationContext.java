/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.context.event.ApplicationEventMulticaster
 *  org.springframework.core.OrderComparator
 *  org.springframework.core.Ordered
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.context.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.gemini.blueprint.context.DelegatedExecutionOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.DependencyAwareBeanFactoryPostProcessor;
import org.eclipse.gemini.blueprint.context.DependencyInitializationAwareBeanPostProcessor;
import org.eclipse.gemini.blueprint.context.OsgiBundleApplicationContextExecutor;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticasterAdapter;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextClosedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;
import org.eclipse.gemini.blueprint.context.support.AbstractOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.ContextClassLoaderProvider;
import org.eclipse.gemini.blueprint.context.support.DefaultContextClassLoaderProvider;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.eclipse.gemini.blueprint.util.internal.PrivilegedUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class AbstractDelegatedExecutionApplicationContext
extends AbstractOsgiBundleApplicationContext
implements DelegatedExecutionOsgiBundleApplicationContext {
    private OsgiBundleApplicationContextExecutor executor = new NoDependenciesWaitRefreshExecutor(this);
    private final Object startupShutdownMonitor = new Object();
    private OsgiBundleApplicationContextEventMulticaster delegatedMulticaster;
    private ContextClassLoaderProvider cclProvider;

    public AbstractDelegatedExecutionApplicationContext() {
    }

    public AbstractDelegatedExecutionApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    public void refresh() throws BeansException, IllegalStateException {
        this.executor.refresh();
    }

    @Override
    public void normalRefresh() {
        Assert.notNull((Object)this.getBundleContext(), (String)"bundle context should be set before refreshing the application context");
        try {
            PrivilegedUtils.executeWithCustomTCCL(this.contextClassLoaderProvider().getContextClassLoader(), new PrivilegedUtils.UnprivilegedExecution(){

                public Object run() {
                    AbstractDelegatedExecutionApplicationContext.super.refresh();
                    AbstractDelegatedExecutionApplicationContext.this.sendRefreshedEvent();
                    return null;
                }
            });
        }
        catch (Throwable th) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Refresh error", th);
            }
            this.sendFailedEvent(th);
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            throw (Error)th;
        }
    }

    @Override
    public void normalClose() {
        try {
            PrivilegedUtils.executeWithCustomTCCL(this.contextClassLoaderProvider().getContextClassLoader(), new PrivilegedUtils.UnprivilegedExecution(){

                public Object run() {
                    AbstractDelegatedExecutionApplicationContext.super.doClose();
                    AbstractDelegatedExecutionApplicationContext.this.sendClosedEvent();
                    return null;
                }
            });
        }
        catch (Throwable th) {
            this.sendClosedEvent(th);
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            throw (Error)th;
        }
    }

    @Override
    protected void doClose() {
        this.executor.close();
    }

    @Override
    public void startRefresh() {
        try {
            PrivilegedUtils.executeWithCustomTCCL(this.contextClassLoaderProvider().getContextClassLoader(), new PrivilegedUtils.UnprivilegedExecution<Object>(){

                @Override
                public Object run() {
                    Object object = AbstractDelegatedExecutionApplicationContext.this.startupShutdownMonitor;
                    synchronized (object) {
                        if (ObjectUtils.isEmpty((Object[])AbstractDelegatedExecutionApplicationContext.this.getConfigLocations())) {
                            AbstractDelegatedExecutionApplicationContext.this.setConfigLocations(AbstractDelegatedExecutionApplicationContext.this.getDefaultConfigLocations());
                        }
                        if (!OsgiBundleUtils.isBundleActive(AbstractDelegatedExecutionApplicationContext.this.getBundle()) && !OsgiBundleUtils.isBundleLazyActivated(AbstractDelegatedExecutionApplicationContext.this.getBundle())) {
                            throw new ApplicationContextException("Unable to refresh application context: bundle is neither active nor lazy-activated but " + OsgiStringUtils.bundleStateAsString(AbstractDelegatedExecutionApplicationContext.this.getBundle()));
                        }
                        ConfigurableListableBeanFactory beanFactory = null;
                        AbstractDelegatedExecutionApplicationContext.this.prepareRefresh();
                        beanFactory = AbstractDelegatedExecutionApplicationContext.this.obtainFreshBeanFactory();
                        AbstractDelegatedExecutionApplicationContext.this.prepareBeanFactory(beanFactory);
                        try {
                            AbstractDelegatedExecutionApplicationContext.this.postProcessBeanFactory(beanFactory);
                            AbstractDelegatedExecutionApplicationContext.this.invokeBeanFactoryPostProcessors(beanFactory);
                            AbstractDelegatedExecutionApplicationContext.this.registerBeanPostProcessors(beanFactory, DependencyInitializationAwareBeanPostProcessor.class, null, false);
                            return null;
                        }
                        catch (BeansException ex) {
                            beanFactory.destroySingletons();
                            AbstractDelegatedExecutionApplicationContext.this.cancelRefresh(ex);
                            throw ex;
                        }
                    }
                }
            });
        }
        catch (Throwable th) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Pre refresh error", th);
            }
            this.sendFailedEvent(th);
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            throw (Error)th;
        }
    }

    @Override
    public void completeRefresh() {
        try {
            PrivilegedUtils.executeWithCustomTCCL(this.contextClassLoaderProvider().getContextClassLoader(), new PrivilegedUtils.UnprivilegedExecution<Object>(){

                @Override
                public Object run() {
                    Object object = AbstractDelegatedExecutionApplicationContext.this.startupShutdownMonitor;
                    synchronized (object) {
                        try {
                            ConfigurableListableBeanFactory beanFactory = AbstractDelegatedExecutionApplicationContext.this.getBeanFactory();
                            AbstractDelegatedExecutionApplicationContext.this.invokeBeanFactoryPostProcessors(beanFactory, DependencyAwareBeanFactoryPostProcessor.class, null);
                            AbstractDelegatedExecutionApplicationContext.this.registerBeanPostProcessors(beanFactory);
                            AbstractDelegatedExecutionApplicationContext.this.initMessageSource();
                            AbstractDelegatedExecutionApplicationContext.this.initApplicationEventMulticaster();
                            AbstractDelegatedExecutionApplicationContext.this.onRefresh();
                            AbstractDelegatedExecutionApplicationContext.this.registerListeners();
                            AbstractDelegatedExecutionApplicationContext.this.finishBeanFactoryInitialization(beanFactory);
                            AbstractDelegatedExecutionApplicationContext.this.finishRefresh();
                            AbstractDelegatedExecutionApplicationContext.this.sendRefreshedEvent();
                            return null;
                        }
                        catch (BeansException ex) {
                            AbstractDelegatedExecutionApplicationContext.this.getBeanFactory().destroySingletons();
                            AbstractDelegatedExecutionApplicationContext.this.cancelRefresh(ex);
                            throw ex;
                        }
                    }
                }
            });
        }
        catch (Throwable th) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Post refresh error", th);
            }
            this.sendFailedEvent(th);
            if (th instanceof RuntimeException) {
                throw (RuntimeException)th;
            }
            throw (Error)th;
        }
    }

    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        this.invokeBeanFactoryPostProcessors(beanFactory, BeanFactoryPostProcessor.class, DependencyAwareBeanFactoryPostProcessor.class);
    }

    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, Class<? extends BeanFactoryPostProcessor> include, Class<? extends BeanFactoryPostProcessor> exclude) {
        HashSet<String> processedBeans = new HashSet<String>();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            LinkedList<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<BeanFactoryPostProcessor>();
            LinkedList<Object> registryPostProcessors = new LinkedList<Object>();
            for (BeanFactoryPostProcessor postProcessor : this.getBeanFactoryPostProcessors()) {
                if (this.isExcluded(include, exclude, postProcessor)) continue;
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    String[] registryPostProcessor = (String[])postProcessor;
                    registryPostProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryPostProcessors.add(registryPostProcessor);
                    continue;
                }
                regularPostProcessors.add(postProcessor);
            }
            if (include.isAssignableFrom(BeanDefinitionRegistryPostProcessor.class)) {
                String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                ArrayList priorityOrderedPostProcessors = new ArrayList();
                for (String ppName : postProcessorNames) {
                    if (!beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) continue;
                    priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
                AbstractDelegatedExecutionApplicationContext.sortPostProcessors(beanFactory, priorityOrderedPostProcessors);
                registryPostProcessors.addAll(priorityOrderedPostProcessors);
                AbstractDelegatedExecutionApplicationContext.invokeBeanDefinitionRegistryPostProcessors(priorityOrderedPostProcessors, registry);
                postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                ArrayList<Object> orderedPostProcessors = new ArrayList<Object>();
                for (String ppName : postProcessorNames) {
                    if (processedBeans.contains(ppName) || !beanFactory.isTypeMatch(ppName, Ordered.class)) continue;
                    orderedPostProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
                AbstractDelegatedExecutionApplicationContext.sortPostProcessors(beanFactory, orderedPostProcessors);
                registryPostProcessors.addAll(orderedPostProcessors);
                AbstractDelegatedExecutionApplicationContext.invokeBeanDefinitionRegistryPostProcessors(orderedPostProcessors, registry);
                int reiterate = 1;
                while (reiterate != 0) {
                    reiterate = 0;
                    for (String ppName : postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false)) {
                        if (processedBeans.contains(ppName)) continue;
                        BeanDefinitionRegistryPostProcessor pp = (BeanDefinitionRegistryPostProcessor)beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class);
                        registryPostProcessors.add(pp);
                        processedBeans.add(ppName);
                        pp.postProcessBeanDefinitionRegistry(registry);
                        reiterate = 1;
                    }
                }
                AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(registryPostProcessors, beanFactory);
            }
            AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        } else {
            AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(this.getBeanFactoryPostProcessors(), beanFactory);
        }
        String[] postProcessorNames = beanFactory.getBeanNamesForType(include, true, false);
        ArrayList<Object> priorityOrderedPostProcessors = new ArrayList<Object>();
        ArrayList<String> orderedPostProcessorNames = new ArrayList<String>();
        ArrayList<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (String ppName : postProcessorNames) {
            if (processedBeans.contains(ppName) || exclude != null && this.isTypeMatch(ppName, exclude)) continue;
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, include));
                continue;
            }
            if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
                continue;
            }
            nonOrderedPostProcessorNames.add(ppName);
        }
        AbstractDelegatedExecutionApplicationContext.sortPostProcessors(beanFactory, priorityOrderedPostProcessors);
        AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
        ArrayList<Object> orderedPostProcessors = new ArrayList<Object>();
        for (String postProcessorName : orderedPostProcessorNames) {
            orderedPostProcessors.add(beanFactory.getBean(postProcessorName, include));
        }
        AbstractDelegatedExecutionApplicationContext.sortPostProcessors(beanFactory, orderedPostProcessors);
        AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
        ArrayList<Object> nonOrderedPostProcessors = new ArrayList<Object>();
        for (String postProcessorName : nonOrderedPostProcessorNames) {
            nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, include));
        }
        AbstractDelegatedExecutionApplicationContext.invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
        beanFactory.clearMetadataCache();
    }

    private boolean isExcluded(Class<? extends BeanFactoryPostProcessor> include, Class<? extends BeanFactoryPostProcessor> exclude, BeanFactoryPostProcessor postProcessor) {
        return !include.isInstance(postProcessor) || exclude != null && exclude.isInstance(postProcessor);
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
        for (BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor : postProcessors) {
            beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(registry);
        }
    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : postProcessors) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private static void sortPostProcessors(ConfigurableListableBeanFactory beanFactory, List<?> postProcessors) {
        Comparator comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory)beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        Collections.sort(postProcessors, comparatorToUse);
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        this.registerBeanPostProcessors(beanFactory, BeanPostProcessor.class, DependencyInitializationAwareBeanPostProcessor.class, true);
    }

    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, Class<?> type, Class<?> exclude, boolean check) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(type, true, false);
        if (check) {
            int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
            beanFactory.addBeanPostProcessor((BeanPostProcessor)new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        }
        ArrayList<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
        ArrayList<String> orderedPostProcessorNames = new ArrayList<String>();
        ArrayList<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (int i = 0; i < postProcessorNames.length; ++i) {
            if (exclude != null && this.isTypeMatch(postProcessorNames[i], exclude)) continue;
            if (this.isTypeMatch(postProcessorNames[i], PriorityOrdered.class)) {
                priorityOrderedPostProcessors.add((BeanPostProcessor)beanFactory.getBean(postProcessorNames[i], BeanPostProcessor.class));
                continue;
            }
            if (this.isTypeMatch(postProcessorNames[i], Ordered.class)) {
                orderedPostProcessorNames.add(postProcessorNames[i]);
                continue;
            }
            nonOrderedPostProcessorNames.add(postProcessorNames[i]);
        }
        Collections.sort(priorityOrderedPostProcessors, new OrderComparator());
        this.registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
        ArrayList<BeanPostProcessor> orderedPostProcessors = new ArrayList<BeanPostProcessor>();
        for (String postProcessorName : orderedPostProcessorNames) {
            orderedPostProcessors.add((BeanPostProcessor)this.getBean(postProcessorName, BeanPostProcessor.class));
        }
        Collections.sort(orderedPostProcessors, new OrderComparator());
        this.registerBeanPostProcessors(beanFactory, orderedPostProcessors);
        ArrayList<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
        for (String postProcessorName : nonOrderedPostProcessorNames) {
            nonOrderedPostProcessors.add((BeanPostProcessor)this.getBean(postProcessorName, BeanPostProcessor.class));
        }
        this.registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
        for (BeanPostProcessor postProcessor : postProcessors) {
            beanFactory.addBeanPostProcessor(postProcessor);
        }
    }

    @Override
    public void setExecutor(OsgiBundleApplicationContextExecutor executor) {
        this.executor = executor;
    }

    protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException, BeansException {
    }

    @Override
    public void setDelegatedEventMulticaster(OsgiBundleApplicationContextEventMulticaster multicaster) {
        this.delegatedMulticaster = multicaster;
    }

    public void setDelegatedEventMulticaster(ApplicationEventMulticaster multicaster) {
        this.delegatedMulticaster = new OsgiBundleApplicationContextEventMulticasterAdapter(multicaster);
    }

    @Override
    public OsgiBundleApplicationContextEventMulticaster getDelegatedEventMulticaster() {
        return this.delegatedMulticaster;
    }

    private void sendFailedEvent(Throwable cause) {
        if (this.delegatedMulticaster != null) {
            this.delegatedMulticaster.multicastEvent(new OsgiBundleContextFailedEvent((ApplicationContext)this, this.getBundle(), cause));
        }
    }

    private void sendRefreshedEvent() {
        if (this.delegatedMulticaster != null) {
            this.delegatedMulticaster.multicastEvent(new OsgiBundleContextRefreshedEvent((ApplicationContext)this, this.getBundle()));
        }
    }

    private void sendClosedEvent() {
        if (this.delegatedMulticaster != null) {
            this.delegatedMulticaster.multicastEvent(new OsgiBundleContextClosedEvent((ApplicationContext)this, this.getBundle()));
        }
    }

    private void sendClosedEvent(Throwable cause) {
        if (this.delegatedMulticaster != null) {
            this.delegatedMulticaster.multicastEvent(new OsgiBundleContextClosedEvent((ApplicationContext)this, this.getBundle(), cause));
        }
    }

    private ContextClassLoaderProvider contextClassLoaderProvider() {
        if (this.cclProvider == null) {
            DefaultContextClassLoaderProvider defaultProvider = new DefaultContextClassLoaderProvider();
            defaultProvider.setBeanClassLoader(this.getClassLoader());
            this.cclProvider = defaultProvider;
        }
        return this.cclProvider;
    }

    public void setContextClassLoaderProvider(ContextClassLoaderProvider contextClassLoaderProvider) {
        this.cclProvider = contextClassLoaderProvider;
    }

    private class BeanPostProcessorChecker
    implements BeanPostProcessor {
        private final ConfigurableListableBeanFactory beanFactory;
        private final int beanPostProcessorTargetCount;

        public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }

        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (!(bean instanceof BeanPostProcessor) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && AbstractDelegatedExecutionApplicationContext.this.logger.isInfoEnabled()) {
                AbstractDelegatedExecutionApplicationContext.this.logger.info((Object)("Bean '" + beanName + "' is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)"));
            }
            return bean;
        }
    }

    private static class NoDependenciesWaitRefreshExecutor
    implements OsgiBundleApplicationContextExecutor {
        private final DelegatedExecutionOsgiBundleApplicationContext context;

        private NoDependenciesWaitRefreshExecutor(DelegatedExecutionOsgiBundleApplicationContext ctx) {
            this.context = ctx;
        }

        @Override
        public void refresh() throws BeansException, IllegalStateException {
            this.context.normalRefresh();
        }

        @Override
        public void close() {
            this.context.normalClose();
        }
    }
}

