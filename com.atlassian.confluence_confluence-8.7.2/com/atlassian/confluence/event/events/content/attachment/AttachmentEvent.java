/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.pages.Attachment;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AttachmentEvent
extends ContentEvent {
    private static final long serialVersionUID = -3656274508551755894L;
    private final @NonNull List<Attachment> attachments;
    private final @NonNull ContentEntityObject attachedTo;

    protected AttachmentEvent(Object src, List<Attachment> attachments, boolean suppressNotifications) {
        super(src, suppressNotifications);
        Objects.requireNonNull(attachments, "An AttachmentEvent must be associated with one or more attachments");
        Preconditions.checkArgument((!attachments.isEmpty() ? 1 : 0) != 0, (Object)"An AttachmentEvent must be associated with one or more attachments");
        this.attachments = new ArrayList<Attachment>(attachments);
        ContentEntityObject container = attachments.get(0).getContainer();
        Objects.requireNonNull(container, "Attachments must have a container ContentEntityObject");
        this.attachedTo = container;
    }

    protected AttachmentEvent(Object src, List<Attachment> attachments) {
        this(src, attachments, false);
    }

    protected AttachmentEvent(Object src, Attachment attachment) {
        this(src, attachment, false);
    }

    protected AttachmentEvent(Object src, Attachment attachment, boolean suppressNotifications) {
        this(src, Collections.singletonList(attachment), suppressNotifications);
    }

    @Override
    public @NonNull ContentEntityObject getContent() {
        return this.attachedTo;
    }

    public @NonNull ContentEntityObject getAttachedTo() {
        return this.attachedTo;
    }

    public Attachment getAttachment() {
        if (this.attachments.isEmpty()) {
            return null;
        }
        return this.attachments.get(0);
    }

    public @NonNull List<Attachment> getAttachments() {
        return Collections.unmodifiableList(this.attachments);
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
        AttachmentEvent that = (AttachmentEvent)o;
        return Objects.equals(this.attachedTo, that.attachedTo) && Objects.equals(this.attachments, that.attachments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.attachments, this.attachedTo);
    }
}

