/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.event;

import com.addonengine.addons.analytics.event.AsyncTrackedConfluenceEvent;
import com.addonengine.addons.analytics.service.EventService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\b\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\u000bH\u0016J\u0010\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0007R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2={"Lcom/addonengine/addons/analytics/event/AsyncEventListener;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "eventPublisher", "Lcom/atlassian/event/api/EventPublisher;", "eventService", "Lcom/addonengine/addons/analytics/service/EventService;", "(Lcom/atlassian/event/api/EventPublisher;Lcom/addonengine/addons/analytics/service/EventService;)V", "getEventPublisher", "()Lcom/atlassian/event/api/EventPublisher;", "afterPropertiesSet", "", "destroy", "onAsyncTrackedConfluenceEvent", "event", "Lcom/addonengine/addons/analytics/event/AsyncTrackedConfluenceEvent;", "analytics"})
public final class AsyncEventListener
implements InitializingBean,
DisposableBean {
    @NotNull
    private final EventPublisher eventPublisher;
    @NotNull
    private final EventService eventService;

    @Autowired
    public AsyncEventListener(@ComponentImport @NotNull EventPublisher eventPublisher, @NotNull EventService eventService) {
        Intrinsics.checkNotNullParameter((Object)eventPublisher, (String)"eventPublisher");
        Intrinsics.checkNotNullParameter((Object)eventService, (String)"eventService");
        this.eventPublisher = eventPublisher;
        this.eventService = eventService;
    }

    @NotNull
    public final EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @EventListener
    public final void onAsyncTrackedConfluenceEvent(@NotNull AsyncTrackedConfluenceEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        this.eventService.save(event.getEvent(), event.getUser(), event.getUserAgent());
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

