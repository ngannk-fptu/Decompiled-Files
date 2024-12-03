/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.AutowireCandidateResolver
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.support.RootBeanDefinition
 *  org.springframework.core.annotation.AnnotationAttributes
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.core.type.AnnotatedTypeMetadata
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.context.annotation;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopedProxyCreator;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.DefaultEventListenerFactory;
import org.springframework.context.event.EventListenerMethodProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public abstract class AnnotationConfigUtils {
    public static final String CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalConfigurationAnnotationProcessor";
    public static final String CONFIGURATION_BEAN_NAME_GENERATOR = "org.springframework.context.annotation.internalConfigurationBeanNameGenerator";
    public static final String AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalAutowiredAnnotationProcessor";
    @Deprecated
    public static final String REQUIRED_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalRequiredAnnotationProcessor";
    public static final String COMMON_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalCommonAnnotationProcessor";
    public static final String PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME = "org.springframework.context.annotation.internalPersistenceAnnotationProcessor";
    private static final String PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME = "org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor";
    public static final String EVENT_LISTENER_PROCESSOR_BEAN_NAME = "org.springframework.context.event.internalEventListenerProcessor";
    public static final String EVENT_LISTENER_FACTORY_BEAN_NAME = "org.springframework.context.event.internalEventListenerFactory";
    private static final boolean jsr250Present;
    private static final boolean jpaPresent;

    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        AnnotationConfigUtils.registerAnnotationConfigProcessors(registry, null);
    }

    public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(BeanDefinitionRegistry registry, @Nullable Object source) {
        RootBeanDefinition def;
        DefaultListableBeanFactory beanFactory = AnnotationConfigUtils.unwrapDefaultListableBeanFactory(registry);
        if (beanFactory != null) {
            if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
                beanFactory.setDependencyComparator((Comparator)AnnotationAwareOrderComparator.INSTANCE);
            }
            if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
                beanFactory.setAutowireCandidateResolver((AutowireCandidateResolver)new ContextAnnotationAutowireCandidateResolver());
            }
        }
        LinkedHashSet<BeanDefinitionHolder> beanDefs = new LinkedHashSet<BeanDefinitionHolder>(8);
        if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (jsr250Present && !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            def = new RootBeanDefinition();
            try {
                def.setBeanClass(ClassUtils.forName((String)PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, (ClassLoader)AnnotationConfigUtils.class.getClassLoader()));
            }
            catch (ClassNotFoundException ex) {
                throw new IllegalStateException("Cannot load optional framework class: org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor", ex);
            }
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
        }
        if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
            def = new RootBeanDefinition(EventListenerMethodProcessor.class);
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
        }
        if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
            def = new RootBeanDefinition(DefaultEventListenerFactory.class);
            def.setSource(source);
            beanDefs.add(AnnotationConfigUtils.registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
        }
        return beanDefs;
    }

    private static BeanDefinitionHolder registerPostProcessor(BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {
        definition.setRole(2);
        registry.registerBeanDefinition(beanName, (BeanDefinition)definition);
        return new BeanDefinitionHolder((BeanDefinition)definition, beanName);
    }

    @Nullable
    private static DefaultListableBeanFactory unwrapDefaultListableBeanFactory(BeanDefinitionRegistry registry) {
        if (registry instanceof DefaultListableBeanFactory) {
            return (DefaultListableBeanFactory)registry;
        }
        if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext)registry).getDefaultListableBeanFactory();
        }
        return null;
    }

    public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd, (AnnotatedTypeMetadata)abd.getMetadata());
    }

    static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
        AnnotationAttributes description;
        AnnotationAttributes role;
        AnnotationAttributes dependsOn;
        AnnotationAttributes lazy = AnnotationConfigUtils.attributesFor(metadata, Lazy.class);
        if (lazy != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        } else if (abd.getMetadata() != metadata && (lazy = AnnotationConfigUtils.attributesFor((AnnotatedTypeMetadata)abd.getMetadata(), Lazy.class)) != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
        if (metadata.isAnnotated(Primary.class.getName())) {
            abd.setPrimary(true);
        }
        if ((dependsOn = AnnotationConfigUtils.attributesFor(metadata, DependsOn.class)) != null) {
            abd.setDependsOn(dependsOn.getStringArray("value"));
        }
        if ((role = AnnotationConfigUtils.attributesFor(metadata, Role.class)) != null) {
            abd.setRole(role.getNumber("value").intValue());
        }
        if ((description = AnnotationConfigUtils.attributesFor(metadata, Description.class)) != null) {
            abd.setDescription(description.getString("value"));
        }
    }

    static BeanDefinitionHolder applyScopedProxyMode(ScopeMetadata metadata, BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
        ScopedProxyMode scopedProxyMode = metadata.getScopedProxyMode();
        if (scopedProxyMode.equals((Object)ScopedProxyMode.NO)) {
            return definition;
        }
        boolean proxyTargetClass = scopedProxyMode.equals((Object)ScopedProxyMode.TARGET_CLASS);
        return ScopedProxyCreator.createScopedProxy(definition, registry, proxyTargetClass);
    }

    @Nullable
    static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationClass) {
        return AnnotationConfigUtils.attributesFor(metadata, annotationClass.getName());
    }

    @Nullable
    static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, String annotationClassName) {
        return AnnotationAttributes.fromMap((Map)metadata.getAnnotationAttributes(annotationClassName));
    }

    static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, Class<?> containerClass, Class<?> annotationClass) {
        return AnnotationConfigUtils.attributesForRepeatable(metadata, containerClass.getName(), annotationClass.getName());
    }

    static Set<AnnotationAttributes> attributesForRepeatable(AnnotationMetadata metadata, String containerClassName, String annotationClassName) {
        LinkedHashSet<AnnotationAttributes> result = new LinkedHashSet<AnnotationAttributes>();
        AnnotationConfigUtils.addAttributesIfNotNull(result, metadata.getAnnotationAttributes(annotationClassName));
        Map container = metadata.getAnnotationAttributes(containerClassName);
        if (container != null && container.containsKey("value")) {
            for (Map containedAttributes : (Map[])container.get("value")) {
                AnnotationConfigUtils.addAttributesIfNotNull(result, containedAttributes);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    private static void addAttributesIfNotNull(Set<AnnotationAttributes> result, @Nullable Map<String, Object> attributes) {
        if (attributes != null) {
            result.add(AnnotationAttributes.fromMap(attributes));
        }
    }

    static {
        ClassLoader classLoader = AnnotationConfigUtils.class.getClassLoader();
        jsr250Present = ClassUtils.isPresent((String)"javax.annotation.Resource", (ClassLoader)classLoader);
        jpaPresent = ClassUtils.isPresent((String)"javax.persistence.EntityManagerFactory", (ClassLoader)classLoader) && ClassUtils.isPresent((String)PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, (ClassLoader)classLoader);
    }
}

