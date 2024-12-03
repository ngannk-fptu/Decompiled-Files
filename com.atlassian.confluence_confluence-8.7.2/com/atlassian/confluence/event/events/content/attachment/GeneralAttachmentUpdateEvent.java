/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.event.events.types.ConfluenceEntityUpdated;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.pages.Attachment;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GeneralAttachmentUpdateEvent
extends AttachmentEvent
implements Updated,
ConfluenceEntityUpdated {
    private static final long serialVersionUID = -3479787187740854855L;
    private final @NonNull Attachment attachment;
    private final @NonNull Attachment previousVersion;

    public GeneralAttachmentUpdateEvent(Object src, Attachment attachment, Attachment previousVersion) {
        this(src, attachment, previousVersion, false);
    }

    public GeneralAttachmentUpdateEvent(Object src, Attachment attachment, Attachment previousVersion, boolean suppressNotifications) {
        super(src, attachment, suppressNotifications);
        Objects.requireNonNull(attachment, "attachment cannot be null");
        Objects.requireNonNull(previousVersion, "previousVersion cannot be null");
        this.attachment = attachment;
        this.previousVersion = previousVersion;
    }

    @Override
    public @NonNull Attachment getOld() {
        return this.previousVersion;
    }

    @Override
    public @NonNull Attachment getNew() {
        return this.attachment;
    }

    public boolean isFileNameChanged() {
        return !this.attachment.getFileName().equals(this.previousVersion.getFileName());
    }

    public boolean isAttachmentContainerUpdated() {
        ContentEntityObject currentOwner = this.attachment.getContainer();
        ContentEntityObject previousOwner = this.previousVersion.getContainer();
        if (currentOwner == null || previousOwner == null) {
            return currentOwner != previousOwner;
        }
        if (!StringUtils.equals((CharSequence)currentOwner.getTitle(), (CharSequence)previousOwner.getTitle())) {
            return true;
        }
        if (!(currentOwner instanceof SpaceContentEntityObject) || !(previousOwner instanceof SpaceContentEntityObject)) {
            return !currentOwner.getClass().equals(previousOwner.getClass());
        }
        SpaceContentEntityObject currentOwnerSpaced = (SpaceContentEntityObject)currentOwner;
        SpaceContentEntityObject previousOwnerSpaced = (SpaceContentEntityObject)previousOwner;
        return !currentOwnerSpaced.getSpaceKey().equals(previousOwnerSpaced.getSpaceKey());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        GeneralAttachmentUpdateEvent that = (GeneralAttachmentUpdateEvent)o;
        return Objects.equals(this.attachment, that.attachment) && Objects.equals(this.previousVersion, that.previousVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attachment, this.previousVersion);
    }
}

