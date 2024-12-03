/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DataSourceFactory
 *  com.atlassian.confluence.mail.notification.listeners.NotificationData
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.TinyUrl
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.user.User
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.mentions;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.TinyUrl;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.mentions.NotificationDataService;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.commons.lang3.StringUtils;

public class NotificationDataServiceImpl
implements NotificationDataService {
    private final DataSourceFactory imageDataSourceFactory;

    public NotificationDataServiceImpl(DataSourceFactory imageDataSourceFactory) {
        this.imageDataSourceFactory = imageDataSourceFactory;
    }

    @Override
    public NotificationData prepareDecorationContext(ConfluenceUser user, ContentEntityObject contentEntityObject) {
        NotificationData notificationData = new NotificationData((User)user, true, null);
        NotificationContext context = notificationData.getCommonContext();
        context.setActor((User)user);
        context.setAction("mention");
        context.setContent((ConfluenceEntityObject)contentEntityObject);
        context.putAll(MacroUtils.defaultVelocityContext());
        DataHandler avatarDataHandler = this.createAvatarDataHandler(user);
        context.put("avatarCid", (Object)avatarDataHandler.getName());
        context.addTemplateImage(this.createAvatarDataHandler(user).getDataSource());
        context.put("sender", (Object)user);
        context.put("ceo", (Object)contentEntityObject);
        ContentEntityObject tinyUrlTarget = contentEntityObject;
        if ("comment".equals(contentEntityObject.getType())) {
            Comment comment = (Comment)contentEntityObject;
            tinyUrlTarget = comment.getContainer();
            context.put("comment", (Object)contentEntityObject);
        } else if ("page".equals(contentEntityObject.getType())) {
            context.put("page", (Object)contentEntityObject);
        } else if ("blogpost".equals(contentEntityObject.getType())) {
            context.put("page", (Object)contentEntityObject);
        }
        String domainName = GeneralUtil.getGlobalSettings().getBaseUrl();
        if (StringUtils.isNotBlank((CharSequence)domainName) && domainName.endsWith("/")) {
            domainName = domainName.substring(0, domainName.length() - 1);
        }
        context.put("baseurl", (Object)domainName);
        if (tinyUrlTarget != null) {
            context.put("tinyUrl", (Object)(domainName + "/x/" + new TinyUrl(tinyUrlTarget.getId()).getIdentifier()));
        }
        return notificationData;
    }

    private DataHandler createAvatarDataHandler(ConfluenceUser user) {
        DataSource avatarDataSource = this.imageDataSourceFactory.getAvatar((User)user);
        return new DataHandler(avatarDataSource);
    }
}

