/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.MauEvent$Builder
 *  com.atlassian.analytics.client.api.mobile.MobileEvent
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.analytics.api.events.MauEvent;
import com.atlassian.analytics.client.api.mobile.MobileEvent;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.plugins.mobile.dto.MobileAnalyticEventDto;
import com.atlassian.confluence.plugins.mobile.service.MobileAnalyticService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MobileAnalyticServiceImpl
implements MobileAnalyticService {
    private static final String MAU_EVENT_NAME = "UserActivity";
    private final EventPublisher eventPublisher;

    @Autowired
    public MobileAnalyticServiceImpl(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publish(@Nonnull List<MobileAnalyticEventDto> events) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new PermissionException("Anonymous is not permitted to publish analytics.");
        }
        events.stream().map(this::convert).forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    private Object convert(MobileAnalyticEventDto eventDto) {
        if (MAU_EVENT_NAME.equals(eventDto.getName())) {
            return new MauEvent.Builder().application("confluence-" + eventDto.getOs()).build(AuthenticatedUserThreadLocal.get().getEmail());
        }
        Map<Object, Object> properties = eventDto.getProperties() == null ? new HashMap() : eventDto.getProperties();
        properties.put("os", eventDto.getOs());
        properties.put("osVersion", eventDto.getOsVersion());
        properties.put("appVersion", eventDto.getAppVersion());
        properties.put("deviceModel", eventDto.getDeviceModel());
        return new MobileEvent(eventDto.getName(), properties, eventDto.getClientTime());
    }
}

