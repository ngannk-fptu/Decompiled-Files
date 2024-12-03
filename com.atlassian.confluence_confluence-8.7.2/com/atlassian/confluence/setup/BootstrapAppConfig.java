/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.AvailableToPluginsBeanDefinitionRegistryProcessor
 *  org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.ImportResource
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.impl.setup.BootstrapCommonAppConfig;
import com.atlassian.confluence.impl.setup.JohnsonAppConfig;
import com.atlassian.confluence.impl.setup.PackageScannerConfigurationAppConfig;
import com.atlassian.confluence.impl.struts.StrutsAppConfig;
import com.atlassian.plugin.spring.AvailableToPluginsBeanDefinitionRegistryProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations={"classpath*:bootstrapContext.xml", "classpath:bootstrapHealthCheckContext.xml", "classpath:bootstrapInstrumentationContext.xml", "classpath:zipkinContext.xml"})
@Import(value={BootstrapCommonAppConfig.class, PackageScannerConfigurationAppConfig.class, JohnsonAppConfig.class, StrutsAppConfig.class})
public class BootstrapAppConfig {
    @Bean
    BeanDefinitionRegistryPostProcessor availableToPluginsBeanDefinitionRegistryProcessor() {
        return new AvailableToPluginsBeanDefinitionRegistryProcessor();
    }
}

