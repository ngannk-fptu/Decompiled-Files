/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar
 *  com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy
 *  com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider
 *  com.atlassian.plugin.util.Assertions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.ClassUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.support.AopUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanIsAbstractException
 *  org.springframework.beans.factory.HierarchicalBeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.config.AbstractFactoryBean
 *  org.springframework.core.annotation.AnnotationUtils
 */
package com.atlassian.plugin.spring;

import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentProvider;
import com.atlassian.plugin.spring.AvailableToPlugins;
import com.atlassian.plugin.spring.SpringHostComponentProviderConfig;
import com.atlassian.plugin.util.Assertions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.annotation.AnnotationUtils;

public class SpringHostComponentProviderFactoryBean
extends AbstractFactoryBean {
    private static final Logger log = LoggerFactory.getLogger(SpringHostComponentProviderFactoryBean.class);
    private SpringHostComponentProviderConfig springHostComponentProviderConfig;
    private Set<String> beanNames = Collections.emptySet();
    private Map<String, Class[]> beanInterfaces = Collections.emptyMap();
    private Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies = Collections.emptyMap();
    private Set<String> bundleTrackingBeans = Collections.emptySet();

    public Class getObjectType() {
        return HostComponentProvider.class;
    }

    protected Object createInstance() {
        if (this.springHostComponentProviderConfig == null) {
            return new SpringHostComponentProvider(this.getBeanFactory(), this.beanNames, this.beanInterfaces, this.beanContextClassLoaderStrategies, this.bundleTrackingBeans, false);
        }
        return new SpringHostComponentProvider(this.getBeanFactory(), (Set<String>)Sets.union(this.beanNames, this.springHostComponentProviderConfig.getBeanNames()), (Map<String, Class[]>)ImmutableMap.builder().putAll(this.beanInterfaces).putAll(this.springHostComponentProviderConfig.getBeanInterfaces()).build(), (Map<String, ContextClassLoaderStrategy>)ImmutableMap.builder().putAll(this.beanContextClassLoaderStrategies).putAll(this.springHostComponentProviderConfig.getBeanContextClassLoaderStrategies()).build(), (Set<String>)ImmutableSet.builder().addAll(this.bundleTrackingBeans).addAll(this.springHostComponentProviderConfig.getBundleTrackingBeans()).build(), this.springHostComponentProviderConfig.isUseAnnotation());
    }

    @Autowired(required=false)
    public void setSpringHostComponentProviderConfig(SpringHostComponentProviderConfig springHostComponentProviderConfig) {
        this.springHostComponentProviderConfig = springHostComponentProviderConfig;
    }

    public void setBeanNames(Set<String> beanNames) {
        this.beanNames = beanNames;
    }

    public void setBeanInterfaces(Map<String, Class[]> beanInterfaces) {
        this.beanInterfaces = beanInterfaces;
    }

    public void setBeanContextClassLoaderStrategies(Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies) {
        this.beanContextClassLoaderStrategies = beanContextClassLoaderStrategies;
    }

    public void setBundleTrackingBeans(Set<String> bundleTrackingBeans) {
        this.bundleTrackingBeans = bundleTrackingBeans;
    }

    private static class SpringHostComponentProvider
    implements HostComponentProvider {
        private final BeanFactory beanFactory;
        private boolean useAnnotation;
        private final Set<String> beanNames;
        private final Map<String, Class[]> beanInterfaces;
        private final Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies;
        private final Set<String> bundleTrackingBeans;

        public SpringHostComponentProvider(BeanFactory beanFactory, Set<String> beanNames, Map<String, Class[]> beanInterfaces, Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies, Set<String> bundleTrackingBeans, boolean useAnnotation) {
            this.beanFactory = (BeanFactory)Assertions.notNull((String)"beanFactory", (Object)beanFactory);
            this.useAnnotation = useAnnotation;
            this.beanNames = beanNames != null ? beanNames : new HashSet();
            this.beanInterfaces = beanInterfaces != null ? beanInterfaces : new HashMap();
            this.beanContextClassLoaderStrategies = beanContextClassLoaderStrategies != null ? beanContextClassLoaderStrategies : new HashMap();
            this.bundleTrackingBeans = bundleTrackingBeans != null ? bundleTrackingBeans : new HashSet();
        }

        public void provide(ComponentRegistrar registrar) {
            BeanFactory parentBeanFactory;
            HashSet<String> beansToProvide = new HashSet<String>(this.beanNames);
            HashMap<String, Class[]> interfacesToProvide = new HashMap<String, Class[]>(this.beanInterfaces);
            HashMap<String, ContextClassLoaderStrategy> contextClassLoaderStrategiesToProvide = new HashMap<String, ContextClassLoaderStrategy>(this.beanContextClassLoaderStrategies);
            HashSet<String> bundleTrackingBeansToProvide = new HashSet<String>(this.bundleTrackingBeans);
            if (this.useAnnotation) {
                this.scanForAnnotatedBeans(beansToProvide, interfacesToProvide, contextClassLoaderStrategiesToProvide, bundleTrackingBeansToProvide);
            }
            this.provideBeans(registrar, beansToProvide, interfacesToProvide, contextClassLoaderStrategiesToProvide, bundleTrackingBeansToProvide);
            if (this.beanFactory instanceof HierarchicalBeanFactory && (parentBeanFactory = ((HierarchicalBeanFactory)this.beanFactory).getParentBeanFactory()) != null) {
                try {
                    HostComponentProvider provider = (HostComponentProvider)parentBeanFactory.getBean("hostComponentProvider");
                    if (provider != null) {
                        provider.provide(registrar);
                    }
                }
                catch (NoSuchBeanDefinitionException e) {
                    log.debug("Unable to find '{}' in the parent bean factory {}", (Object)"hostComponentProvider", (Object)parentBeanFactory);
                }
            }
        }

        private void provideBeans(ComponentRegistrar registrar, Set<String> beanNames, Map<String, Class[]> beanInterfaces, Map<String, ContextClassLoaderStrategy> beanContextClassLoaderStrategies, Set<String> bundleTrackingBeans) {
            for (String beanName : beanNames) {
                if (this.beanFactory.isSingleton(beanName)) {
                    Object bean = this.beanFactory.getBean(beanName);
                    Class[] interfaces = beanInterfaces.get(beanName);
                    if (interfaces == null) {
                        interfaces = this.findInterfaces(this.getBeanClass(bean));
                    }
                    registrar.register(interfaces).forInstance(bean).withName(beanName).withContextClassLoaderStrategy(beanContextClassLoaderStrategies.getOrDefault(beanName, ContextClassLoaderStrategy.USE_HOST)).withTrackBundleEnabled(bundleTrackingBeans.contains(beanName));
                    continue;
                }
                log.warn("Cannot register bean '{}' as it's scope is not singleton", (Object)beanName);
            }
        }

        private void scanForAnnotatedBeans(Set<String> beansToProvide, Map<String, Class[]> interfacesToProvide, Map<String, ContextClassLoaderStrategy> contextClassLoaderStrategiesToProvide, Set<String> bundleTrackingBeansToProvide) {
            if (this.beanFactory instanceof ListableBeanFactory) {
                for (String beanName : ((ListableBeanFactory)this.beanFactory).getBeanDefinitionNames()) {
                    try {
                        if (this.beanFactory.isSingleton(beanName)) {
                            Class beanClass = this.getBeanClass(this.beanFactory.getBean(beanName));
                            AvailableToPlugins annotation = (AvailableToPlugins)AnnotationUtils.findAnnotation((Class)beanClass, AvailableToPlugins.class);
                            if (annotation == null) continue;
                            beansToProvide.add(beanName);
                            if (annotation.value() != Void.class || annotation.interfaces().length != 0) {
                                if (!interfacesToProvide.containsKey(beanName)) {
                                    ArrayList<Class> effectiveInterfaces = new ArrayList<Class>();
                                    if (annotation.value() != Void.class) {
                                        effectiveInterfaces.add(annotation.value());
                                    }
                                    if (annotation.interfaces().length != 0) {
                                        effectiveInterfaces.addAll(Arrays.asList(annotation.interfaces()));
                                    }
                                    interfacesToProvide.put(beanName, effectiveInterfaces.toArray(new Class[0]));
                                } else {
                                    log.debug("Interfaces for bean '{}' have been defined in XML or in a Module definition, ignoring the interface defined in the annotation", (Object)beanName);
                                }
                            }
                            if (!contextClassLoaderStrategiesToProvide.containsKey(beanName)) {
                                contextClassLoaderStrategiesToProvide.put(beanName, annotation.contextClassLoaderStrategy());
                            } else {
                                log.debug("Context class loader strategy for bean '{}' has been defined in XML or in a Module definition, ignoring the one defined in the annotation", (Object)beanName);
                            }
                            if (!annotation.trackBundle()) continue;
                            bundleTrackingBeansToProvide.add(beanName);
                            continue;
                        }
                        log.debug("Bean: {} skipped during @AvailableToPlugins scanning since it's not a singleton bean", (Object)beanName);
                    }
                    catch (BeanIsAbstractException beanIsAbstractException) {
                        // empty catch block
                    }
                }
            } else {
                log.warn("Could not scan bean factory for beans to make available to plugins, bean factory is not 'listable'");
            }
        }

        private Class[] findInterfaces(Class cls) {
            ArrayList validInterfaces = new ArrayList();
            for (Class<?> inf : this.getAllInterfaces(cls)) {
                if (inf.getName().startsWith("org.springframework")) continue;
                validInterfaces.add(inf);
            }
            return validInterfaces.toArray(new Class[0]);
        }

        private List<Class<?>> getAllInterfaces(Class cls) {
            return ClassUtils.getAllInterfaces((Class)cls);
        }

        private Class getBeanClass(Object bean) {
            return AopUtils.getTargetClass((Object)bean);
        }
    }
}

