/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.framework.autoproxy.AutoProxyUtils
 *  org.springframework.aop.scope.ScopedObject
 *  org.springframework.aop.scope.ScopedProxyUtils
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanInitializationException
 *  org.springframework.beans.factory.SmartInitializingSingleton
 *  org.springframework.beans.factory.config.BeanFactoryPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.MethodIntrospector
 *  org.springframework.core.SpringProperties
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.context.event;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ApplicationListenerMethodAdapter;
import org.springframework.context.event.EventExpressionEvaluator;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.SpringProperties;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

public class EventListenerMethodProcessor
implements SmartInitializingSingleton,
ApplicationContextAware,
BeanFactoryPostProcessor {
    private static final boolean shouldIgnoreSpel = SpringProperties.getFlag((String)"spring.spel.ignore");
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private ConfigurableApplicationContext applicationContext;
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;
    @Nullable
    private List<EventListenerFactory> eventListenerFactories;
    @Nullable
    private final EventExpressionEvaluator evaluator;
    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));

    public EventListenerMethodProcessor() {
        this.evaluator = shouldIgnoreSpel ? null : new EventExpressionEvaluator();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        Assert.isTrue((boolean)(applicationContext instanceof ConfigurableApplicationContext), (String)"ApplicationContext does not implement ConfigurableApplicationContext");
        this.applicationContext = (ConfigurableApplicationContext)applicationContext;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        Map beans2 = beanFactory.getBeansOfType(EventListenerFactory.class, false, false);
        ArrayList factories = new ArrayList(beans2.values());
        AnnotationAwareOrderComparator.sort(factories);
        this.eventListenerFactories = factories;
    }

    public void afterSingletonsInstantiated() {
        String[] beanNames;
        ConfigurableListableBeanFactory beanFactory = this.beanFactory;
        Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"No ConfigurableListableBeanFactory set");
        for (String beanName : beanNames = beanFactory.getBeanNamesForType(Object.class)) {
            Class type;
            block10: {
                block9: {
                    if (ScopedProxyUtils.isScopedTarget((String)beanName)) continue;
                    type = null;
                    try {
                        type = AutoProxyUtils.determineTargetClass((ConfigurableListableBeanFactory)beanFactory, (String)beanName);
                    }
                    catch (Throwable ex) {
                        if (!this.logger.isDebugEnabled()) break block9;
                        this.logger.debug((Object)("Could not resolve target class for bean with name '" + beanName + "'"), ex);
                    }
                }
                if (type == null) continue;
                if (ScopedObject.class.isAssignableFrom(type)) {
                    try {
                        Class targetClass = AutoProxyUtils.determineTargetClass((ConfigurableListableBeanFactory)beanFactory, (String)ScopedProxyUtils.getTargetBeanName((String)beanName));
                        if (targetClass != null) {
                            type = targetClass;
                        }
                    }
                    catch (Throwable ex) {
                        if (!this.logger.isDebugEnabled()) break block10;
                        this.logger.debug((Object)("Could not resolve target bean for scoped proxy '" + beanName + "'"), ex);
                    }
                }
            }
            try {
                this.processBean(beanName, type);
            }
            catch (Throwable ex) {
                throw new BeanInitializationException("Failed to process @EventListener annotation on bean with name '" + beanName + "'", ex);
            }
        }
    }

    private void processBean(String beanName, Class<?> targetType) {
        if (!this.nonAnnotatedClasses.contains(targetType) && AnnotationUtils.isCandidateClass(targetType, EventListener.class) && !EventListenerMethodProcessor.isSpringContainerClass(targetType)) {
            Map annotatedMethods;
            block10: {
                annotatedMethods = null;
                try {
                    annotatedMethods = MethodIntrospector.selectMethods(targetType, method -> (EventListener)AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, EventListener.class));
                }
                catch (Throwable ex) {
                    if (!this.logger.isDebugEnabled()) break block10;
                    this.logger.debug((Object)("Could not resolve methods for bean with name '" + beanName + "'"), ex);
                }
            }
            if (CollectionUtils.isEmpty((Map)annotatedMethods)) {
                this.nonAnnotatedClasses.add(targetType);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("No @EventListener annotations found on bean class: " + targetType.getName()));
                }
            } else {
                ConfigurableApplicationContext context = this.applicationContext;
                Assert.state((context != null ? 1 : 0) != 0, (String)"No ApplicationContext set");
                List<EventListenerFactory> factories = this.eventListenerFactories;
                Assert.state((factories != null ? 1 : 0) != 0, (String)"EventListenerFactory List not initialized");
                block2: for (Method method2 : annotatedMethods.keySet()) {
                    for (EventListenerFactory factory : factories) {
                        if (!factory.supportsMethod(method2)) continue;
                        Method methodToUse = AopUtils.selectInvocableMethod((Method)method2, (Class)context.getType(beanName));
                        ApplicationListener<?> applicationListener = factory.createApplicationListener(beanName, targetType, methodToUse);
                        if (applicationListener instanceof ApplicationListenerMethodAdapter) {
                            ((ApplicationListenerMethodAdapter)applicationListener).init(context, this.evaluator);
                        }
                        context.addApplicationListener(applicationListener);
                        continue block2;
                    }
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)(annotatedMethods.size() + " @EventListener methods processed on bean '" + beanName + "': " + annotatedMethods));
                }
            }
        }
    }

    private static boolean isSpringContainerClass(Class<?> clazz) {
        return clazz.getName().startsWith("org.springframework.") && !AnnotatedElementUtils.isAnnotated((AnnotatedElement)ClassUtils.getUserClass(clazz), Component.class);
    }
}

