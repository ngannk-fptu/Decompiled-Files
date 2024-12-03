/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.listeners;

import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.services.RequestStorage;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageCreateEventListener {
    private static final Logger log = LoggerFactory.getLogger(PageCreateEventListener.class);
    private final EventPublisher eventPublisher;
    private final RequestStorage requestStorage;

    @Autowired
    public PageCreateEventListener(@ComponentImport EventPublisher eventPublisher, RequestStorage requestStorage) {
        this.eventPublisher = eventPublisher;
        this.requestStorage = requestStorage;
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onPageCreateEvent(PageCreateEvent event) {
        log.debug("Draft with ID {} was published. Clearing any Create Request for it.", (Object)event.getPage().getId());
        this.requestStorage.clear(event.getContent());
    }
}

