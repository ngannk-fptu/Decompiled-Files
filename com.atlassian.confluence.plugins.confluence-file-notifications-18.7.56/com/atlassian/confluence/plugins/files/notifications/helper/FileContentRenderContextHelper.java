/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.files.notifications.helper;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FileContentRenderContextHelper {
    public static final LinkType LIKE_LINK = LinkType.valueOf((String)"like");

    public static NotificationContext generateNotificationContextMap(FileContentEventType type, List<Content> fileContentList, ContentId containerContentId, Content descendantContent, Content previousFileContent, ModuleCompleteKey notificationKey, User modifier, RoleRecipient roleRecipient, I18NBean i18NBean) {
        NotificationContext context = new NotificationContext();
        HashMap<String, LinkType> linkTypes = new HashMap<String, LinkType>();
        linkTypes.put("webui", LinkType.WEB_UI);
        linkTypes.put("like", LIKE_LINK);
        context.put("titleIconKey", (Object)FileContentRenderContextHelper.getTitleIconKey(type));
        context.put("fileContentList", fileContentList);
        context.put("modifier", (Object)modifier);
        context.put("notificationKey", (Object)notificationKey);
        context.put("linkTypes", linkTypes);
        context.put("containerId", (Object)containerContentId);
        context.put("actionString", (Object)FileContentRenderContextHelper.getActionString(fileContentList, type, modifier, descendantContent, i18NBean));
        context.put("eventType", (Object)type);
        if (descendantContent != null) {
            context.put("descendantContent", (Object)descendantContent);
            context.put("descendantContentView", (Object)((ContentBody)descendantContent.getBody().get(ContentRepresentation.VIEW)).getValue());
            if (descendantContent.getAncestors().size() == 1) {
                context.put("ancestorView", (Object)((ContentBody)((Content)descendantContent.getAncestors().get(0)).getBody().get(ContentRepresentation.VIEW)).getValue());
            }
        }
        if (previousFileContent != null) {
            context.put("previousFileContent", (Object)previousFileContent);
        }
        return context;
    }

    private static String getActionString(List<Content> fileContentList, FileContentEventType type, User modifier, Content descendantContent, I18NBean i18NBean) {
        String actionKey;
        String userFullName = modifier == null ? i18NBean.getText("confluence.file.notifications.anonymous.name") : modifier.getFullName();
        switch (type) {
            case CREATE: {
                actionKey = fileContentList.size() == 1 ? "confluence.file.notifications.new.file" : "confluence.file.notifications.new.files";
                break;
            }
            case UPDATE: {
                actionKey = "confluence.file.notifications.new.version";
                break;
            }
            case CREATE_COMMENT: {
                actionKey = descendantContent.getAncestors().isEmpty() ? "confluence.file.notifications.new.comment" : "confluence.file.notifications.new.reply";
                break;
            }
            case DELETE: {
                actionKey = "confluence.file.notifications.delete.file";
                break;
            }
            case DELETE_VERSION: {
                actionKey = "confluence.file.notifications.delete.version";
                break;
            }
            case DELETE_COMMENT: {
                actionKey = "confluence.file.notifications.delete.comment";
                break;
            }
            case RESOLVE_COMMENT: {
                actionKey = "confluence.file.notifications.resolve.comment";
                break;
            }
            case MENTION_IN_COMMENT: {
                actionKey = "confluence.file.notifications.mention.in.comment";
                break;
            }
            default: {
                throw new IllegalArgumentException("Cannot find matching action key for event type: " + type.name());
            }
        }
        return i18NBean.getText(actionKey, Collections.singletonList(userFullName));
    }

    private static String getTitleIconKey(FileContentEventType type) {
        switch (type) {
            case CREATE: 
            case UPDATE: 
            case DELETE: 
            case DELETE_VERSION: {
                return "file-content-image-icon";
            }
            case CREATE_COMMENT: 
            case DELETE_COMMENT: 
            case RESOLVE_COMMENT: 
            case MENTION_IN_COMMENT: {
                return "file-content-comment-icon";
            }
        }
        return "";
    }
}

