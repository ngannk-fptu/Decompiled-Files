/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.search.SearchPlatform
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.ConfigurationCondition
 *  org.springframework.context.annotation.ConfigurationCondition$ConfigurationPhase
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.confluence.plugins.opensearch.spring;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.search.SearchPlatform;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Objects;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.METHOD})
@Conditional(value={OpenSearchCondition.class})
public @interface ConditionalForOpenSearch {

    public static class OpenSearchCondition
    implements ConfigurationCondition {
        public ConfigurationCondition.ConfigurationPhase getConfigurationPhase() {
            return ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION;
        }

        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            ConfigurableListableBeanFactory beanFactory = Objects.requireNonNull(context.getBeanFactory());
            ApplicationConfiguration config = (ApplicationConfiguration)beanFactory.getBean(ApplicationConfiguration.class);
            return SearchPlatform.OPENSEARCH.equals((Object)SearchPlatform.getSearchPlatform((ApplicationConfiguration)config));
        }
    }
}

