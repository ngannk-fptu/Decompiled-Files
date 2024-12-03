/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.context.ApplicationListener
 *  org.springframework.context.event.ContextRefreshedEvent
 *  org.springframework.core.Ordered
 */
package org.springframework.data.repository.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.data.repository.Repository;

class DeferredRepositoryInitializationListener
implements ApplicationListener<ContextRefreshedEvent>,
Ordered {
    private static final Log logger = LogFactory.getLog(DeferredRepositoryInitializationListener.class);
    private final ListableBeanFactory beanFactory;

    DeferredRepositoryInitializationListener(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info((Object)"Triggering deferred initialization of Spring Data repositories\u2026");
        this.beanFactory.getBeansOfType(Repository.class);
        logger.info((Object)"Spring Data repositories initialized!");
    }

    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}

