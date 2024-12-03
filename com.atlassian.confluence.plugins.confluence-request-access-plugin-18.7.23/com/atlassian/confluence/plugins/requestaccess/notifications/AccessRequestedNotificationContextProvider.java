/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.requestaccess.notifications.AbstractAccessNotificationContextProvider;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AccessRequestedNotificationContextProvider
extends AbstractAccessNotificationContextProvider {
    private final SpaceManager spaceManager;

    @Autowired
    public AccessRequestedNotificationContextProvider(@ComponentImport UserAccessor userAccessor, @ComponentImport SpaceManager spaceManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        super(userAccessor, i18NBeanFactory, localeManager, contentEntityManager);
        this.spaceManager = Objects.requireNonNull(spaceManager);
    }

    @Override
    protected NotificationContext extendedContext(NotificationContext context, DefaultAccessNotificationPayload payload) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(payload);
        NotificationContext extendedContext = super.extendedContext(context, payload);
        Space space = this.spaceManager.getSpace(payload.getSpaceKey());
        extendedContext.put("spaceName", (Object)HtmlUtil.htmlEncode((String)space.getName()));
        extendedContext.put("spaceLink", (Object)space.getUrlPath());
        return extendedContext;
    }

    @Override
    protected String getRelativeActionUrl(DefaultAccessNotificationPayload payload, Content content, ConfluenceUser actingUser) {
        Objects.requireNonNull(payload);
        Objects.requireNonNull(content);
        Objects.requireNonNull(actingUser);
        if (payload.isDraft()) {
            return this.helper.getAddDraftRestrictionActionUrlPath(content, actingUser, payload.getAccessType());
        }
        return this.helper.getAddPageRestrictionActionUrlPath(content, actingUser, payload.getAccessType());
    }
}

