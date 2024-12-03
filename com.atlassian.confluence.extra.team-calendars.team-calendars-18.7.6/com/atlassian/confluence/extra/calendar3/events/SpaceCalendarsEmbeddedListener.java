/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor$Propagation
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.events.SpaceCalendarsEmbeddedEvent;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpaceCalendarsEmbeddedListener
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(SpaceCalendarsEmbeddedListener.class);
    private final EventPublisher eventPublisher;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final CalendarManager calendarManager;

    @Autowired
    public SpaceCalendarsEmbeddedListener(@ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionalHostContextAccessor hostContextAccessor, CalendarManager calendarManager) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.hostContextAccessor = Objects.requireNonNull(hostContextAccessor);
        this.calendarManager = Objects.requireNonNull(calendarManager);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onSpaceCalendarsEmbeddedEvent(SpaceCalendarsEmbeddedEvent event) {
        this.hostContextAccessor.doInTransaction(TransactionalHostContextAccessor.Propagation.REQUIRES_NEW, () -> {
            try {
                this.calendarManager.addCalendarsToSpaceView(event.getSubCalendarIds(), event.getSpaceKey());
            }
            catch (Exception e) {
                log.debug("Error when adding calendar {} to space view {}", new Object[]{event.getSubCalendarIds(), event.getSpaceKey(), e});
                log.warn("Error {} when adding calendar {} to space view {}", new Object[]{e.getMessage(), event.getSubCalendarIds(), event.getSpaceKey()});
            }
            return null;
        });
    }
}

