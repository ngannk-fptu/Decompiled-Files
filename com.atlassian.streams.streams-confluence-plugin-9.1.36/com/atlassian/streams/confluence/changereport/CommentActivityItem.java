/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Predicate
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ContentEntityActivityItem;
import com.google.common.base.Predicate;

final class CommentActivityItem
extends ContentEntityActivityItem {
    private final Comment comment;

    public CommentActivityItem(Comment comment, Iterable<StreamsEntry.ActivityObject> activityObjects, Option<StreamsEntry.ActivityObject> target, StreamsEntry.Renderer renderer, Predicate<String> canCommentPredicate) {
        super((ContentEntityObject)comment, activityObjects, target, renderer, canCommentPredicate);
        this.comment = comment;
    }

    @Override
    public String getIconPath() {
        return "/images/icons/comment_16.gif";
    }

    @Override
    public Option<String> getSpaceKey() {
        ContentEntityObject container = this.comment.getContainer();
        if (container instanceof SpaceContentEntityObject) {
            return Option.option((Object)((SpaceContentEntityObject)container).getSpaceKey());
        }
        return Option.none();
    }

    @Override
    public String getType() {
        return "comment.added";
    }

    @Override
    public ActivityVerb getVerb() {
        return ActivityVerbs.post();
    }
}

