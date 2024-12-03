/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
 *  org.springframework.core.OrderComparator
 *  org.springframework.core.Ordered
 *  org.springframework.core.PriorityOrdered
 *  org.springframework.core.metrics.ApplicationStartup
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ApplicationListenerDetector;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;

final class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate() {
    }

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        HashSet<String> processedBeans = new HashSet<String>();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            String[] postProcessorNames;
            String[] registryProcessor;
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry)beanFactory;
            ArrayList<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
            ArrayList<Object> registryProcessors = new ArrayList<Object>();
            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    registryProcessor = (String[])postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                    continue;
                }
                regularPostProcessors.add(postProcessor);
            }
            ArrayList<Object> currentRegistryProcessors = new ArrayList<Object>();
            for (String ppName : postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false)) {
                if (!beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) continue;
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
            PostProcessorRegistrationDelegate.sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
            registryProcessor = postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            int n = registryProcessor.length;
            for (int i = 0; i < n; ++i) {
                String ppName;
                ppName = registryProcessor[i];
                if (processedBeans.contains(ppName) || !beanFactory.isTypeMatch(ppName, Ordered.class)) continue;
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
            }
            PostProcessorRegistrationDelegate.sortPostProcessors(currentRegistryProcessors, beanFactory);
            registryProcessors.addAll(currentRegistryProcessors);
            PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
            currentRegistryProcessors.clear();
            int reiterate = 1;
            while (reiterate != 0) {
                reiterate = 0;
                for (String ppName : postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false)) {
                    if (processedBeans.contains(ppName)) continue;
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                    reiterate = 1;
                }
                PostProcessorRegistrationDelegate.sortPostProcessors(currentRegistryProcessors, beanFactory);
                registryProcessors.addAll(currentRegistryProcessors);
                PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry, beanFactory.getApplicationStartup());
                currentRegistryProcessors.clear();
            }
            PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
            PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        } else {
            PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
        }
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        ArrayList<Object> priorityOrderedPostProcessors = new ArrayList<Object>();
        ArrayList<String> orderedPostProcessorNames = new ArrayList<String>();
        ArrayList<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (String ppName : postProcessorNames) {
            if (processedBeans.contains(ppName)) continue;
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
                continue;
            }
            if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
                continue;
            }
            nonOrderedPostProcessorNames.add(ppName);
        }
        PostProcessorRegistrationDelegate.sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
        ArrayList<Object> orderedPostProcessors = new ArrayList<Object>(orderedPostProcessorNames.size());
        for (String postProcessorName : orderedPostProcessorNames) {
            orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }
        PostProcessorRegistrationDelegate.sortPostProcessors(orderedPostProcessors, beanFactory);
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
        ArrayList<Object> nonOrderedPostProcessors = new ArrayList<Object>(nonOrderedPostProcessorNames.size());
        for (String postProcessorName : nonOrderedPostProcessorNames) {
            nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }
        PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
        beanFactory.clearMetadataCache();
    }

    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        BeanPostProcessor pp;
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        ArrayList<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
        ArrayList<BeanPostProcessor> internalPostProcessors = new ArrayList<BeanPostProcessor>();
        ArrayList<String> orderedPostProcessorNames = new ArrayList<String>();
        ArrayList<String> nonOrderedPostProcessorNames = new ArrayList<String>();
        for (String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (!(pp instanceof MergedBeanDefinitionPostProcessor)) continue;
                internalPostProcessors.add(pp);
                continue;
            }
            if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
                continue;
            }
            nonOrderedPostProcessorNames.add(ppName);
        }
        PostProcessorRegistrationDelegate.sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
        ArrayList<BeanPostProcessor> orderedPostProcessors = new ArrayList<BeanPostProcessor>(orderedPostProcessorNames.size());
        for (String ppName : orderedPostProcessorNames) {
            BeanPostProcessor pp2 = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
            orderedPostProcessors.add(pp2);
            if (!(pp2 instanceof MergedBeanDefinitionPostProcessor)) continue;
            internalPostProcessors.add(pp2);
        }
        PostProcessorRegistrationDelegate.sortPostProcessors(orderedPostProcessors, beanFactory);
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, orderedPostProcessors);
        ArrayList<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanPostProcessor>(nonOrderedPostProcessorNames.size());
        for (String ppName : nonOrderedPostProcessorNames) {
            pp = (BeanPostProcessor)beanFactory.getBean(ppName, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp);
            if (!(pp instanceof MergedBeanDefinitionPostProcessor)) continue;
            internalPostProcessors.add(pp);
        }
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
        PostProcessorRegistrationDelegate.sortPostProcessors(internalPostProcessors, beanFactory);
        PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, internalPostProcessors);
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new ApplicationListenerDetector(applicationContext));
    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        if (postProcessors.size() <= 1) {
            return;
        }
        Comparator comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory)beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        postProcessors.sort(comparatorToUse);
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry, ApplicationStartup applicationStartup) {
        for (BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor : postProcessors) {
            StartupStep postProcessBeanDefRegistry = applicationStartup.start("spring.context.beandef-registry.post-process").tag("postProcessor", beanDefinitionRegistryPostProcessor::toString);
            beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(registry);
            postProcessBeanDefRegistry.end();
        }
    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : postProcessors) {
            StartupStep postProcessBeanFactory = beanFactory.getApplicationStartup().start("spring.context.bean-factory.post-process").tag("postProcessor", beanFactoryPostProcessor::toString);
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
            postProcessBeanFactory.end();
        }
    }

    private static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
        if (beanFactory instanceof AbstractBeanFactory) {
            ((AbstractBeanFactory)beanFactory).addBeanPostProcessors(postProcessors);
        } else {
            for (BeanPostProcessor postProcessor : postProcessors) {
                beanFactory.addBeanPostProcessor(postProcessor);
            }
        }
    }

    private static final class BeanPostProcessorChecker
    implements BeanPostProcessor {
        private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);
        private final ConfigurableListableBeanFactory beanFactory;
        private final int beanPostProcessorTargetCount;

        public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }

        public Object postProcessBeforeInitialization(Object bean2, String beanName) {
            return bean2;
        }

        public Object postProcessAfterInitialization(Object bean2, String beanName) {
            if (!(bean2 instanceof BeanPostProcessor) && !this.isInfrastructureBean(beanName) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && logger.isInfoEnabled()) {
                logger.info((Object)("Bean '" + beanName + "' of type [" + bean2.getClass().getName() + "] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)"));
            }
            return bean2;
        }

        private boolean isInfrastructureBean(@Nullable String beanName) {
            if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
                BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
                return bd.getRole() == 2;
            }
            return false;
        }
    }
}

