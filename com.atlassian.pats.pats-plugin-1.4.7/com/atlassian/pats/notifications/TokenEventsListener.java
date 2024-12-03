/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.pats.notifications;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.pats.api.TokenMailSenderService;
import com.atlassian.pats.events.token.TokenEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class TokenEventsListener
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final TokenMailSenderService tokenMailSenderService;

    public TokenEventsListener(EventPublisher eventPublisher, TokenMailSenderService tokenMailSenderService) {
        this.eventPublisher = eventPublisher;
        this.tokenMailSenderService = tokenMailSenderService;
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onTokenEvent(TokenEvent event) {
        this.tokenMailSenderService.sendTokenEventMail(event);
    }
}

