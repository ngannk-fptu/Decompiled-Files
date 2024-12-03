/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.ActivityVerbs
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Functions
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Objects
 *  javax.annotation.Nullable
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.ActivityVerbs;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.google.common.base.Objects;
import java.util.Date;
import javax.annotation.Nullable;

public final class AttachmentActivityItem
implements ActivityItem {
    private final Attachment attachment;
    private final Iterable<Entry> entries;
    private final Iterable<StreamsEntry.ActivityObject> activityObjects;
    private final Option<StreamsEntry.ActivityObject> target;
    private final StreamsEntry.Renderer renderer;

    public AttachmentActivityItem(Attachment attachment, Iterable<Entry> entries, Iterable<StreamsEntry.ActivityObject> activityObjects, Option<StreamsEntry.ActivityObject> target, StreamsEntry.Renderer renderer) {
        this.attachment = attachment;
        this.entries = entries;
        this.activityObjects = activityObjects;
        this.target = target;
        this.renderer = renderer;
    }

    @Override
    public Iterable<StreamsEntry.ActivityObject> getActivityObjects() {
        return this.activityObjects;
    }

    @Override
    public String getChangedBy() {
        ConfluenceUser lastModifier = this.attachment.getLastModifier();
        return lastModifier != null ? lastModifier.getName() : null;
    }

    @Override
    public String getContentType() {
        return "attachment";
    }

    @Override
    public String getIconPath() {
        return "/images/icons/contenttypes/attachment_16.png";
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Date getModified() {
        return this.attachment.getLastModificationDate();
    }

    @Override
    public StreamsEntry.Renderer getRenderer() {
        return this.renderer;
    }

    @Override
    public int getVersion() {
        return this.attachment.getVersion();
    }

    @Override
    public Option<String> getSpaceKey() {
        if (this.attachment.getSpace() != null) {
            return Option.option((Object)this.attachment.getSpace().getKey());
        }
        return Option.none();
    }

    @Override
    public Option<StreamsEntry.ActivityObject> getTarget() {
        return this.target;
    }

    @Override
    public String getType() {
        return "attachment." + (this.attachment.isNew() ? "added" : "modified");
    }

    @Override
    public String getUrlPath() {
        return this.extractUrlPath(this.attachment);
    }

    @Override
    public ActivityVerb getVerb() {
        return this.attachment.isNew() ? ActivityVerbs.post() : ActivityVerbs.update();
    }

    @Override
    public boolean isAcceptingCommentsFromUser(String username) {
        return false;
    }

    public Iterable<Entry> getAttachments() {
        return this.entries;
    }

    public Attachment getEntity() {
        return this.attachment;
    }

    public boolean matches(Attachment attachment) {
        ConfluenceUser lastModifier = attachment.getLastModifier();
        return Objects.equal((Object)this.getChangedBy(), (Object)(lastModifier != null ? lastModifier.getName() : null)) && this.getUrlPath().equals(this.extractUrlPath(attachment)) && Math.abs(this.getModified().getTime() - attachment.getLastModificationDate().getTime()) < 60000L;
    }

    @Nullable
    private String extractUrlPath(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        return container != null ? container.getUrlPath() : null;
    }

    public static final class Preview {
        private final String downloadPath;
        private final int height;
        private final int width;

        public Preview(Attachment attachment, Thumbnail thumbnail) {
            this.downloadPath = attachment.getDownloadPath();
            this.height = thumbnail.getHeight();
            this.width = thumbnail.getWidth();
        }

        public String getDownloadPath() {
            return this.downloadPath;
        }

        public int getHeight() {
            return this.height;
        }

        public int getWidth() {
            return this.width;
        }
    }

    public static final class Entry {
        private final Attachment attachment;
        private final Option<Preview> preview;

        public Entry(Attachment attachment, Option<Preview> preview) {
            this.attachment = attachment;
            this.preview = preview;
        }

        public String getDownloadPath() {
            return this.attachment.getDownloadPath();
        }

        public String getName() {
            return this.attachment.getFileName();
        }

        public Option<String> getComment() {
            return Option.option((Object)this.attachment.getVersionComment()).flatMap(Functions.trimToNone());
        }

        public Option<Preview> getPreview() {
            return this.preview;
        }
    }
}

