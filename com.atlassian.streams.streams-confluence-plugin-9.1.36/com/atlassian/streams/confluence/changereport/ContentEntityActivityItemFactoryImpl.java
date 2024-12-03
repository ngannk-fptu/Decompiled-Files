/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.ActivityObjectFactory;
import com.atlassian.streams.confluence.changereport.CanCommentPredicateFactory;
import com.atlassian.streams.confluence.changereport.CommentActivityItem;
import com.atlassian.streams.confluence.changereport.ContentEntityActivityItem;
import com.atlassian.streams.confluence.changereport.ContentEntityActivityItemFactory;
import com.atlassian.streams.confluence.changereport.ContentEntityObjects;
import com.atlassian.streams.confluence.changereport.SpaceActivityItem;
import com.atlassian.streams.confluence.renderer.ContentEntityRendererFactory;
import com.atlassian.streams.confluence.renderer.SpaceRendererFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ContentEntityActivityItemFactoryImpl
implements ContentEntityActivityItemFactory {
    private static final Logger log = LoggerFactory.getLogger(ContentEntityActivityItemFactoryImpl.class);
    private final ActivityObjectFactory activityObjectFactory;
    private final ContentEntityRendererFactory contentEntityRendererFactory;
    private final SpaceRendererFactory spaceRendererFactory;
    private final CanCommentPredicateFactory canCommentPredicateFactory;

    public ContentEntityActivityItemFactoryImpl(ActivityObjectFactory activityObjectFactory, ContentEntityRendererFactory contentEntityRendererFactory, SpaceRendererFactory spaceRendererFactory, CanCommentPredicateFactory canCommentPredicateFactory) {
        this.activityObjectFactory = (ActivityObjectFactory)Preconditions.checkNotNull((Object)activityObjectFactory, (Object)"activityObjectFactory");
        this.contentEntityRendererFactory = (ContentEntityRendererFactory)Preconditions.checkNotNull((Object)contentEntityRendererFactory, (Object)"contentEntityRendererFactory");
        this.spaceRendererFactory = (SpaceRendererFactory)Preconditions.checkNotNull((Object)spaceRendererFactory, (Object)"spaceRendererFactory");
        this.canCommentPredicateFactory = (CanCommentPredicateFactory)Preconditions.checkNotNull((Object)canCommentPredicateFactory, (Object)"canCommentPredicateFactory");
    }

    @Override
    public ActivityItem newActivityItem(URI baseUri, AbstractPage page) {
        return new ContentEntityActivityItem((ContentEntityObject)page, this.getActivityObjects(baseUri, page), (Option<StreamsEntry.ActivityObject>)Option.some((Object)this.getTarget((SpaceContentEntityObject)page)), this.contentEntityRendererFactory.newInstance(baseUri, (ContentEntityObject)page), this.canCommentPredicateFactory.canCommentOn(page));
    }

    private Iterable<StreamsEntry.ActivityObject> getActivityObjects(URI baseUri, AbstractPage page) {
        return ImmutableList.of((Object)(ContentEntityObjects.isBlogPost((ContentEntityObject)page) ? this.activityObjectFactory.newActivityObject(baseUri, (BlogPost)page) : this.activityObjectFactory.newActivityObject(baseUri, (Page)page)));
    }

    @Override
    public ActivityItem newActivityItem(URI baseUri, Comment comment) {
        return new CommentActivityItem(comment, (Iterable<StreamsEntry.ActivityObject>)ImmutableList.of((Object)this.activityObjectFactory.newActivityObject(baseUri, comment)), this.getTarget(baseUri, comment), this.contentEntityRendererFactory.newInstance(baseUri, (ContentEntityObject)comment), this.canCommentPredicateFactory.canCommentOn(comment));
    }

    private Option<StreamsEntry.ActivityObject> getTarget(URI baseUri, Comment comment) {
        ContentEntityObject owner = comment.getContainer();
        if (owner == null) {
            log.debug("Encountered a comment with no owner: " + comment);
            return Option.none();
        }
        if (owner.getType() == null) {
            log.debug("Encountered a comment with an owner with no type. Comment: " + comment + ", Owner: " + owner);
            return Option.none();
        }
        if (ContentEntityObjects.isBlogPost(owner)) {
            return Option.some((Object)this.activityObjectFactory.newActivityObject(baseUri, (BlogPost)owner));
        }
        if (ContentEntityObjects.isPage(owner)) {
            return Option.some((Object)this.activityObjectFactory.newActivityObject(baseUri, (Page)owner));
        }
        return Option.none();
    }

    @Override
    public ActivityItem newActivityItem(SpaceDescription space, boolean isCreationEvent) {
        return new SpaceActivityItem(space, isCreationEvent, (Iterable<StreamsEntry.ActivityObject>)ImmutableList.of((Object)this.activityObjectFactory.newActivityObject(space)), (Option<StreamsEntry.ActivityObject>)Option.none(StreamsEntry.ActivityObject.class), this.spaceRendererFactory.newInstance(space));
    }

    private StreamsEntry.ActivityObject getTarget(SpaceContentEntityObject entity) {
        Space space = entity.getSpace();
        if (space == null && entity.getLatestVersion() instanceof SpaceContentEntityObject) {
            space = ((SpaceContentEntityObject)entity.getLatestVersion()).getSpace();
        }
        return this.activityObjectFactory.newActivityObject(new SpaceDescription(space));
    }
}

