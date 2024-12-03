/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.config.JohnsonConfig
 *  com.atlassian.seraph.config.SecurityConfig
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.confluence.impl.health.HealthCheckRunner;
import com.atlassian.confluence.impl.health.web.DefaultJohnsonEventSerializer;
import com.atlassian.confluence.impl.health.web.JohnsonEventCollectionSerializer;
import com.atlassian.confluence.impl.health.web.JohnsonEventSerializerFactory;
import com.atlassian.confluence.impl.health.web.JohnsonPageDataProvider;
import com.atlassian.confluence.impl.health.web.JohnsonPageI18NProvider;
import com.atlassian.confluence.impl.health.web.LegacyJohnsonEventSerializer;
import com.atlassian.confluence.impl.upgrade.UpgradeEventRegistry;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.spring.johnson.JohnsonConfigFactoryBean;
import com.atlassian.confluence.spring.johnson.JohnsonEventContainerFactoryBean;
import com.atlassian.confluence.util.i18n.DynamicI18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.config.JohnsonConfig;
import com.atlassian.seraph.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JohnsonAppConfig {
    @Bean
    JohnsonConfigFactoryBean johnsonConfig() {
        return new JohnsonConfigFactoryBean();
    }

    @Bean
    JohnsonEventContainerFactoryBean johnsonEventContainer() {
        return new JohnsonEventContainerFactoryBean();
    }

    @Bean
    JohnsonEventCollectionSerializer johnsonEventCollectionSerializer(@Qualifier(value="johnsonI18NBeanFactory") I18NBeanFactory i18NBeanFactory, BootstrapManager bootstrapManager) {
        return new JohnsonEventCollectionSerializer(new JohnsonEventSerializerFactory(new DefaultJohnsonEventSerializer(), new LegacyJohnsonEventSerializer(i18NBeanFactory, bootstrapManager)));
    }

    @Bean
    JohnsonPageDataProvider johnsonPageDataProvider(HealthCheckRunner healthCheckRunner, JohnsonConfig johnsonConfig, JohnsonEventCollectionSerializer johnsonEventCollectionSerializer, JohnsonEventContainer johnsonEventContainer, SecurityConfig securityConfig) {
        return new JohnsonPageDataProvider(healthCheckRunner, johnsonConfig, johnsonEventCollectionSerializer, johnsonEventContainer, securityConfig);
    }

    @Bean
    I18NBeanFactory johnsonI18NBeanFactory() {
        return new DynamicI18NBeanFactory();
    }

    @Bean
    JohnsonPageI18NProvider johnsonPageI18NProvider() {
        return new JohnsonPageI18NProvider(this.johnsonI18NBeanFactory());
    }

    @Bean
    UpgradeEventRegistry upgradeEventRegistry() {
        return new UpgradeEventRegistry();
    }
}

