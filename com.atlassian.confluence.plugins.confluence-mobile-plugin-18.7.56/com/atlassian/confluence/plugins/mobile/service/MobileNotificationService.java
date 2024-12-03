/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.mywork.model.NotificationFilter
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.plugins.mobile.service;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.mobile.dto.NotificationDto;
import com.atlassian.mywork.model.NotificationFilter;
import javax.annotation.Nonnull;

public interface MobileNotificationService {
    public NotificationDto getNotification(long var1);

    @Nonnull
    public PageResponse<NotificationDto> getNotifications(@Nonnull NotificationFilter var1, @Nonnull PageRequest var2);

    public void readNotifications(@Nonnull NotificationFilter var1);

    public void deleteNotification(@Nonnull NotificationFilter var1);
}

