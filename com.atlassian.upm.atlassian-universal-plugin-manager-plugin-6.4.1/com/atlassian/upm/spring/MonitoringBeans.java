/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.rest.monitor.representations.MonitorRepresentationFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringBeans {
    @Bean
    public MonitorRepresentationFactoryImpl monitorRepresentationFactory(PermissionService permissionService) {
        return new MonitorRepresentationFactoryImpl(permissionService);
    }
}

