/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.NotificationFilter
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.mywork.service.PermissionException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.impl;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.plugins.mobile.dto.NotificationDto;
import com.atlassian.confluence.plugins.mobile.service.MobileNotificationService;
import com.atlassian.confluence.plugins.mobile.service.converter.MobileNotificationConverter;
import com.atlassian.confluence.rest.api.model.RestList;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.NotificationFilter;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.mywork.service.PermissionException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class MobileNotificationServiceImpl
implements MobileNotificationService {
    private final LocalNotificationService notificationService;
    private final MobileNotificationConverter notificationConverter;

    @Autowired
    public MobileNotificationServiceImpl(@ComponentImport LocalNotificationService notificationService, MobileNotificationConverter notificationConverter) {
        this.notificationService = notificationService;
        this.notificationConverter = notificationConverter;
    }

    @Override
    public NotificationDto getNotification(long id) {
        if (AuthenticatedUserThreadLocal.isAnonymousUser()) {
            throw new com.atlassian.confluence.api.service.exceptions.PermissionException("Anonymous is not allowed to retrieve notification");
        }
        try {
            Notification notification = this.notificationService.find(AuthenticatedUserThreadLocal.getUsername(), id);
            if (notification == null) {
                throw new NotFoundException("Cannot find notification with id: " + id);
            }
            return this.notificationConverter.to(notification);
        }
        catch (PermissionException ex) {
            throw new NotFoundException("Cannot find notification with id: " + id);
        }
    }

    @Override
    @Nonnull
    public PageResponse<NotificationDto> getNotifications(@Nonnull NotificationFilter filter, @Nonnull PageRequest pageRequest) {
        List notifications = this.notificationService.findAllWithCurrentUser(filter, pageRequest.getStart(), pageRequest.getLimit());
        boolean hasMore = notifications.size() == pageRequest.getLimit();
        return RestList.newRestList((PageRequest)pageRequest).results(this.notificationConverter.to(notifications), hasMore).build();
    }

    @Override
    public void readNotifications(@Nonnull NotificationFilter filter) {
        this.notificationService.setReadWithCurrentUser(filter);
    }

    @Override
    public void deleteNotification(@Nonnull NotificationFilter filter) {
        this.notificationService.deleteWithCurrentUser(filter);
    }
}

