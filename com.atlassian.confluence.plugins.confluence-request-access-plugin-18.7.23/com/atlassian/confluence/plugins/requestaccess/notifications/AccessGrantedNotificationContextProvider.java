/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.requestaccess.notifications;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.requestaccess.notifications.AbstractAccessNotificationContextProvider;
import com.atlassian.confluence.plugins.requestaccess.notifications.DefaultAccessNotificationPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AccessGrantedNotificationContextProvider
extends AbstractAccessNotificationContextProvider {
    @Autowired
    public AccessGrantedNotificationContextProvider(@ComponentImport UserAccessor userAccessor, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        super(userAccessor, i18NBeanFactory, localeManager, contentEntityManager);
    }

    @Override
    protected String getRelativeActionUrl(DefaultAccessNotificationPayload payload, Content content, @Nullable ConfluenceUser actingUser) {
        Objects.requireNonNull(payload);
        Objects.requireNonNull(content);
        LinkType linkType = payload.isDraft() || "edit".equals(payload.getAccessType()) ? LinkType.EDIT_UI : LinkType.WEB_UI;
        return ((Link)content.getLinks().get(linkType)).getPath();
    }
}

