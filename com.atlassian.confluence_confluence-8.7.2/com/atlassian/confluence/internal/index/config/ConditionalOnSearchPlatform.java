/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.ConfigurationCondition
 *  org.springframework.context.annotation.ConfigurationCondition$ConfigurationPhase
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.confluence.internal.index.config;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.search.SearchPlatform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
@Conditional(value={OnSearchPlatformCondition.class})
public @interface ConditionalOnSearchPlatform {
    public SearchPlatform value();

    public static class OnSearchPlatformCondition
    implements ConfigurationCondition {
        public ConfigurationCondition.ConfigurationPhase getConfigurationPhase() {
            return ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION;
        }

        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigurableListableBeanFactory beanFactory = Objects.requireNonNull(context.getBeanFactory());
            ApplicationConfiguration config = (ApplicationConfiguration)beanFactory.getBean(ApplicationConfiguration.class);
            Map platformAttribute = Objects.requireNonNull(metadata.getAnnotationAttributes(ConditionalOnSearchPlatform.class.getName()));
            SearchPlatform requestedPlatform = (SearchPlatform)((Object)platformAttribute.get("value"));
            return Objects.equals((Object)requestedPlatform, (Object)SearchPlatform.getSearchPlatform(config));
        }
    }
}

