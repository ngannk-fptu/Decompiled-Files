/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  org.codehaus.jackson.map.Module
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.migration.agent.AppBeanConfiguration;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.testsupport.BackdoorService;
import com.atlassian.migration.app.AppCloudMigrationGateway;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.codehaus.jackson.map.Module;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={AppBeanConfiguration.class})
@Configuration
public class ExportOsgiServiceBeans {
    @Bean
    public Module customSerializers() {
        return Jsons.createModule();
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportAppCloudMigrationGateway(AppCloudMigrationGateway appCloudMigrationGateway) {
        return OsgiServices.exportOsgiService((Object)appCloudMigrationGateway, (ExportOptions)ExportOptions.as(AppCloudMigrationGateway.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportBackdoorService(BackdoorService backdoorService) {
        return OsgiServices.exportOsgiService((Object)backdoorService, (ExportOptions)ExportOptions.as(BackdoorService.class, (Class[])new Class[0]));
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportModuleService(Module module) {
        return OsgiServices.exportOsgiService((Object)module, (ExportOptions)ExportOptions.as(Module.class, (Class[])new Class[0]));
    }
}

