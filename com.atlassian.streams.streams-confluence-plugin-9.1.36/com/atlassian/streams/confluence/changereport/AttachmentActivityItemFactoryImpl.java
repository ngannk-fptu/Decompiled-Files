/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailManager
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.ActivityObjectFactory;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItem;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItemFactory;
import com.atlassian.streams.confluence.renderer.AttachmentRendererFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class AttachmentActivityItemFactoryImpl
implements AttachmentActivityItemFactory {
    private static final Logger log = LoggerFactory.getLogger(AttachmentActivityItemFactoryImpl.class);
    private final ActivityObjectFactory activityObjectFactory;
    private final ThumbnailManager thumbnailManager;
    private final AttachmentRendererFactory attachmentRendererFactory;
    private final Function<AttachmentActivityItem.Entry, StreamsEntry.ActivityObject> toActivityObjects = new Function<AttachmentActivityItem.Entry, StreamsEntry.ActivityObject>(){

        public StreamsEntry.ActivityObject apply(AttachmentActivityItem.Entry attachment) {
            return AttachmentActivityItemFactoryImpl.this.activityObjectFactory.newActivityObject(attachment);
        }
    };

    AttachmentActivityItemFactoryImpl(ActivityObjectFactory activityObjectFactory, ThumbnailManager thumbnailManager, AttachmentRendererFactory attachmentRendererFactory) {
        this.activityObjectFactory = (ActivityObjectFactory)Preconditions.checkNotNull((Object)activityObjectFactory, (Object)"activityObjectFactory");
        this.thumbnailManager = (ThumbnailManager)Preconditions.checkNotNull((Object)thumbnailManager, (Object)"thumbnailManager");
        this.attachmentRendererFactory = (AttachmentRendererFactory)Preconditions.checkNotNull((Object)attachmentRendererFactory, (Object)"attachmentRendererFactory");
    }

    @Override
    public ActivityItem newActivityItem(URI baseUri, Attachment attachment) {
        return this.newActivityItem(baseUri, attachment, (Iterable<AttachmentActivityItem.Entry>)ImmutableList.of((Object)this.buildAttachmentEntry(attachment)));
    }

    @Override
    public ActivityItem newActivityItem(URI baseUri, Attachment attachment, AttachmentActivityItem attachmentItem) {
        return this.newActivityItem(baseUri, attachment, Iterables.concat(attachmentItem.getAttachments(), (Iterable)ImmutableList.of((Object)this.buildAttachmentEntry(attachment))));
    }

    private ActivityItem newActivityItem(URI baseUri, Attachment attachment, Iterable<AttachmentActivityItem.Entry> entries) {
        return new AttachmentActivityItem(attachment, entries, this.getActivityObjects(entries), this.getTarget(baseUri, attachment.getContainer()), this.attachmentRendererFactory.newInstance(entries));
    }

    private Iterable<StreamsEntry.ActivityObject> getActivityObjects(Iterable<AttachmentActivityItem.Entry> attachments) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(attachments, this.toAttachmentActivityObjects()));
    }

    private Function<AttachmentActivityItem.Entry, StreamsEntry.ActivityObject> toAttachmentActivityObjects() {
        return this.toActivityObjects;
    }

    private Option<StreamsEntry.ActivityObject> getTarget(URI baseUri, ContentEntityObject owner) {
        if (owner instanceof BlogPost) {
            return Option.some((Object)this.activityObjectFactory.newActivityObject(baseUri, (BlogPost)owner));
        }
        if (owner instanceof Page) {
            return Option.some((Object)this.activityObjectFactory.newActivityObject(baseUri, (Page)owner));
        }
        return Option.none();
    }

    private AttachmentActivityItem.Entry buildAttachmentEntry(Attachment attachment) {
        Option preview;
        if (this.thumbnailManager.isThumbnailable(attachment)) {
            try {
                Thumbnail thumbnail = this.thumbnailManager.getThumbnail(attachment);
                preview = Option.some((Object)new AttachmentActivityItem.Preview(attachment, thumbnail));
            }
            catch (Exception e) {
                log.warn("Exception caught trying to get a thumbnail.", (Throwable)e);
                preview = Option.none();
            }
        } else {
            preview = Option.none();
        }
        return new AttachmentActivityItem.Entry(attachment, (Option<AttachmentActivityItem.Preview>)preview);
    }
}

