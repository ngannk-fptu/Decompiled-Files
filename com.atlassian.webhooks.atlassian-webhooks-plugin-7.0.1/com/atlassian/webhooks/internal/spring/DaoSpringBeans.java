/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.webhooks.internal.spring;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.dao.AoInvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.AoWebhookDao;
import com.atlassian.webhooks.internal.dao.AsyncInvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.InvocationHistoryDao;
import com.atlassian.webhooks.internal.dao.WebhookDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoSpringBeans {
    @Bean(name={"aoInvocationHistoryDao"})
    public InvocationHistoryDao aoInvocationHistoryDao(ActiveObjects ao) {
        return new AoInvocationHistoryDao(ao);
    }

    @Bean(name={"asyncInvocationHistoryDao"})
    public InvocationHistoryDao asyncInvocationHistoryDao(@Qualifier(value="aoInvocationHistoryDao") InvocationHistoryDao dao, WebhookHostAccessor hostAccessor, TransactionTemplate txTemplate) {
        return new AsyncInvocationHistoryDao(dao, hostAccessor, txTemplate);
    }

    @Bean
    public WebhookDao webhookDao(ActiveObjects ao) {
        return new AoWebhookDao(ao);
    }
}

