/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListenerRegistrar
 *  com.atlassian.plugins.osgi.javaconfig.ExportOptions
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.history.InvocationHistoryService
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.webhooks.internal.spring;

import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.history.InvocationHistoryService;
import com.atlassian.webhooks.internal.dao.InvocationHistoryDao;
import com.atlassian.webhooks.internal.history.DefaultInvocationHistoryService;
import com.atlassian.webhooks.internal.history.InternalInvocationHistoryService;
import com.atlassian.webhooks.internal.history.WebhookInvocationHistorian;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistorySpringBeans {
    @Bean
    public InternalInvocationHistoryService invocationHistoryService(@Qualifier(value="asyncInvocationHistoryDao") InvocationHistoryDao dao, EventListenerRegistrar eventListenerRegistrar, SchedulerService schedulerService, TransactionTemplate txTemplate, WebhookService webhookService) {
        return new DefaultInvocationHistoryService(dao, eventListenerRegistrar, schedulerService, txTemplate, webhookService);
    }

    @Bean
    public WebhookInvocationHistorian webhookInvocationHistorian(InternalInvocationHistoryService historyService) {
        return new WebhookInvocationHistorian(historyService);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportInvocationHistoryService(InvocationHistoryService invocationHistoryService) {
        return OsgiServices.exportOsgiService((Object)invocationHistoryService, (ExportOptions)ExportOptions.as(InvocationHistoryService.class, (Class[])new Class[0]));
    }
}

