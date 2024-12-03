/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions.user;

import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class UserWatchingSpaceForContentTypeCondition
extends BaseConfluenceCondition {
    private NotificationManager notificationManager;
    private ContentTypeEnum defaultType = null;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        try {
            String typeStr = params.get("type");
            if (StringUtils.isNotBlank((CharSequence)typeStr)) {
                this.defaultType = ContentTypeEnum.getByRepresentation(typeStr);
            }
        }
        catch (Exception e) {
            throw new PluginParseException("Could not determine content type for condition. " + e.getMessage(), (Throwable)e);
        }
        super.init(params);
    }

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        if (context.getCurrentUser() == null) {
            return false;
        }
        Space space = context.getSpace();
        if (space == null) {
            return false;
        }
        ContentTypeEnum type = this.defaultType;
        AbstractPage abstractPage = context.getPage();
        if (type == null && abstractPage != null) {
            type = abstractPage.getTypeEnum();
        }
        return this.notificationManager.getNotificationByUserAndSpaceAndType(context.getCurrentUser(), space, type) != null;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}

