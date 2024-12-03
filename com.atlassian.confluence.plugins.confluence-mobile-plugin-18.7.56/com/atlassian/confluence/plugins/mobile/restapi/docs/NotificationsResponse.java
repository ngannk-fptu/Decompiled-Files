/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl$Builder
 */
package com.atlassian.confluence.plugins.mobile.restapi.docs;

import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.plugins.mobile.dto.NotificationDto;

public class NotificationsResponse
extends PageResponseImpl<NotificationDto> {
    protected NotificationsResponse(PageResponseImpl.Builder<NotificationDto, ? extends PageResponseImpl.Builder> builder) {
        super(builder);
    }
}

