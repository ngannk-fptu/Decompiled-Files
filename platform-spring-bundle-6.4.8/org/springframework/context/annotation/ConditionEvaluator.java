/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationClassUtils;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MultiValueMap;

class ConditionEvaluator {
    private final ConditionContextImpl context;

    public ConditionEvaluator(@Nullable BeanDefinitionRegistry registry, @Nullable Environment environment2, @Nullable ResourceLoader resourceLoader) {
        this.context = new ConditionContextImpl(registry, environment2, resourceLoader);
    }

    public boolean shouldSkip(AnnotatedTypeMetadata metadata) {
        return this.shouldSkip(metadata, null);
    }

    public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationCondition.ConfigurationPhase phase) {
        if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
            return false;
        }
        if (phase == null) {
            if (metadata instanceof AnnotationMetadata && ConfigurationClassUtils.isConfigurationCandidate((AnnotationMetadata)metadata)) {
                return this.shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION);
            }
            return this.shouldSkip(metadata, ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }
        ArrayList<Condition> conditions = new ArrayList<Condition>();
        for (String[] conditionClasses : this.getConditionClasses(metadata)) {
            for (String conditionClass : conditionClasses) {
                Condition condition = this.getCondition(conditionClass, this.context.getClassLoader());
                conditions.add(condition);
            }
        }
        AnnotationAwareOrderComparator.sort(conditions);
        for (Condition condition : conditions) {
            ConfigurationCondition.ConfigurationPhase requiredPhase = null;
            if (condition instanceof ConfigurationCondition) {
                requiredPhase = ((ConfigurationCondition)condition).getConfigurationPhase();
            }
            if (requiredPhase != null && requiredPhase != phase || condition.matches(this.context, metadata)) continue;
            return true;
        }
        return false;
    }

    private List<String[]> getConditionClasses(AnnotatedTypeMetadata metadata) {
        MultiValueMap<String, Object> attributes = metadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        List<String[]> values = attributes != null ? (List<String[]>)attributes.get("value") : null;
        return values != null ? values : Collections.emptyList();
    }

    private Condition getCondition(String conditionClassName, @Nullable ClassLoader classloader) {
        Class<?> conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return (Condition)BeanUtils.instantiateClass(conditionClass);
    }

    private static class ConditionContextImpl
    implements ConditionContext {
        @Nullable
        private final BeanDefinitionRegistry registry;
        @Nullable
        private final ConfigurableListableBeanFactory beanFactory;
        private final Environment environment;
        private final ResourceLoader resourceLoader;
        @Nullable
        private final ClassLoader classLoader;

        public ConditionContextImpl(@Nullable BeanDefinitionRegistry registry, @Nullable Environment environment2, @Nullable ResourceLoader resourceLoader) {
            this.registry = registry;
            this.beanFactory = this.deduceBeanFactory(registry);
            this.environment = environment2 != null ? environment2 : this.deduceEnvironment(registry);
            this.resourceLoader = resourceLoader != null ? resourceLoader : this.deduceResourceLoader(registry);
            this.classLoader = this.deduceClassLoader(resourceLoader, this.beanFactory);
        }

        @Nullable
        private ConfigurableListableBeanFactory deduceBeanFactory(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ConfigurableListableBeanFactory) {
                return (ConfigurableListableBeanFactory)((Object)source);
            }
            if (source instanceof ConfigurableApplicationContext) {
                return ((ConfigurableApplicationContext)((Object)source)).getBeanFactory();
            }
            return null;
        }

        private Environment deduceEnvironment(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof EnvironmentCapable) {
                return ((EnvironmentCapable)((Object)source)).getEnvironment();
            }
            return new StandardEnvironment();
        }

        private ResourceLoader deduceResourceLoader(@Nullable BeanDefinitionRegistry source) {
            if (source instanceof ResourceLoader) {
                return (ResourceLoader)((Object)source);
            }
            return new DefaultResourceLoader();
        }

        @Nullable
        private ClassLoader deduceClassLoader(@Nullable ResourceLoader resourceLoader, @Nullable ConfigurableListableBeanFactory beanFactory) {
            ClassLoader classLoader;
            if (resourceLoader != null && (classLoader = resourceLoader.getClassLoader()) != null) {
                return classLoader;
            }
            if (beanFactory != null) {
                return beanFactory.getBeanClassLoader();
            }
            return ClassUtils.getDefaultClassLoader();
        }

        @Override
        public BeanDefinitionRegistry getRegistry() {
            Assert.state(this.registry != null, "No BeanDefinitionRegistry available");
            return this.registry;
        }

        @Override
        @Nullable
        public ConfigurableListableBeanFactory getBeanFactory() {
            return this.beanFactory;
        }

        @Override
        public Environment getEnvironment() {
            return this.environment;
        }

        @Override
        public ResourceLoader getResourceLoader() {
            return this.resourceLoader;
        }

        @Override
        @Nullable
        public ClassLoader getClassLoader() {
            return this.classLoader;
        }
    }
}

