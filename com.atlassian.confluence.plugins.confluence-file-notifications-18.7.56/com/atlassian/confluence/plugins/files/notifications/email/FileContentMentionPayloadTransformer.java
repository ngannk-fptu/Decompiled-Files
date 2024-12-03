/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.notifications.PayloadTransformerTemplate
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.files.notifications.email;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.PayloadTransformerTemplate;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.files.notifications.api.FileContentEventType;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentMentionUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.email.FileContentUpdatePayload;
import com.atlassian.confluence.plugins.files.notifications.email.NotificationContent;
import com.atlassian.confluence.plugins.files.notifications.helper.FileContentPayloadTransformerHelper;
import com.atlassian.confluence.plugins.mentions.api.ConfluenceMentionEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.user.User;
import java.util.Collections;

public class FileContentMentionPayloadTransformer
extends PayloadTransformerTemplate<ConfluenceMentionEvent, FileContentUpdatePayload> {
    protected Maybe<FileContentUpdatePayload> checkedCreate(ConfluenceMentionEvent event) {
        if (event.getMentionedUserProfile() == null) {
            return Option.none();
        }
        ContentEntityObject content = event.getContent();
        if (!(content instanceof Comment)) {
            return Option.none();
        }
        Comment comment = (Comment)content;
        ContentEntityObject commentContainer = comment.getContainer();
        if (!(commentContainer instanceof Attachment)) {
            return Option.none();
        }
        Attachment attachment = (Attachment)commentContainer;
        User mentioningUser = event.getMentioningUser();
        FileContentMentionUpdatePayload payload = new FileContentMentionUpdatePayload(FileContentEventType.MENTION_IN_COMMENT, FileContentPayloadTransformerHelper.getNotificationContentForCeo(attachment.getContainer()), Collections.singletonList(FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)attachment)), NotificationContent.EMPTY, FileContentPayloadTransformerHelper.getNotificationContentForCeo((ContentEntityObject)comment), mentioningUser == null ? null : ((ConfluenceUser)mentioningUser).getKey().getStringValue(), event.getMentionedUserProfile().getUserKey().getStringValue());
        return Option.some((Object)payload);
    }
}

