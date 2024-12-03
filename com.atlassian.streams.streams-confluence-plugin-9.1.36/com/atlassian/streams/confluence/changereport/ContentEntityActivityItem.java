/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.ContentEntityObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Date;

class ContentEntityActivityItem
implements ActivityItem {
    private final ContentEntityObject entity;
    private final Iterable<StreamsEntry.ActivityObject> activityObjects;
    private final Option<StreamsEntry.ActivityObject> target;
    private final StreamsEntry.Renderer renderer;
    private final Predicate<String> canCommentPredicate;

    public ContentEntityActivityItem(ContentEntityObject entity, Iterable<StreamsEntry.ActivityObject> activityObjects, Option<StreamsEntry.ActivityObject> target, StreamsEntry.Renderer renderer, Predicate<String> canCommentPredicate) {
        this.entity = entity;
        this.activityObjects = activityObjects;
        this.target = target;
        this.renderer = renderer;
        this.canCommentPredicate = canCommentPredicate;
    }

    public ContentEntityActivityItem(ContentEntityObject entity, Iterable<StreamsEntry.ActivityObject> activityObjects, Option<StreamsEntry.ActivityObject> target, StreamsEntry.Renderer renderer) {
        this.entity = entity;
        this.activityObjects = activityObjects;
        this.target = target;
        this.renderer = renderer;
        this.canCommentPredicate = Predicates.alwaysFalse();
    }

    @Override
    public Iterable<StreamsEntry.ActivityObject> getActivityObjects() {
        return this.activityObjects;
    }

    @Override
    public Option<StreamsEntry.ActivityObject> getTarget() {
        return this.target;
    }

    @Override
    public String getChangedBy() {
        return this.entity.isNew() ? this.entity.getCreatorName() : this.entity.getLastModifierName();
    }

    @Override
    public String getContentType() {
        return this.entity.getType();
    }

    @Override
    public String getIconPath() {
        if (ContentEntityObjects.isBlogPost(this.entity)) {
            return "/images/icons/blogentry_16.gif";
        }
        if (ContentEntityObjects.isMail(this.entity)) {
            return "/images/icons/mail_content_16.gif";
        }
        return "/images/icons/docs_16.gif";
    }

    @Override
    public Long getId() {
        return this.entity.getLatestVersionId();
    }

    @Override
    public Date getModified() {
        return this.entity.getLastModificationDate() != null ? this.entity.getLastModificationDate() : this.entity.getCreationDate();
    }

    @Override
    public StreamsEntry.Renderer getRenderer() {
        return this.renderer;
    }

    @Override
    public Option<String> getSpaceKey() {
        if (this.entity instanceof SpaceContentEntityObject) {
            return Option.option((Object)((SpaceContentEntityObject)this.entity.getLatestVersion()).getSpaceKey());
        }
        return Option.none();
    }

    @Override
    public String getType() {
        return this.entity.getType() + "." + (this.isNew() ? "added" : "modified");
    }

    @Override
    public String getUrlPath() {
        return this.entity.getUrlPath();
    }

    @Override
    public ActivityVerb getVerb() {
        return this.isNew() ? ActivityVerbs.post() : ActivityVerbs.update();
    }

    public boolean isNew() {
        return this.entity.isNew();
    }

    @Override
    public boolean isAcceptingCommentsFromUser(String username) {
        return this.canCommentPredicate.apply((Object)username);
    }

    @Override
    public int getVersion() {
        return this.entity.getVersion();
    }

    public ContentEntityObject getEntity() {
        return this.entity;
    }
}

