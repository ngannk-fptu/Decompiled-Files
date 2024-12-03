/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent
 *  com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent
 *  com.atlassian.confluence.pages.PageUpdateTrigger
 *  com.atlassian.confluence.plugins.synchrony.events.exported.SynchronyRequestEvent
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service.event;

import com.atlassian.confluence.event.events.content.attachment.AttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.ContentUpdatedEvent;
import com.atlassian.confluence.event.events.content.page.synchrony.SynchronyRecoveryEvent;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.plugins.synchrony.events.exported.SynchronyRequestEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

@AsynchronousPreferred
public class CollaborativeEditingAsyncEvent {
    private final long contentId;
    private final EventType eventType;
    private final EnumMap<Key, Object> properties;

    protected CollaborativeEditingAsyncEvent(long contentId, EventType eventType) {
        this.contentId = contentId;
        this.eventType = Objects.requireNonNull(eventType);
        this.properties = new EnumMap(Key.class);
    }

    public static CollaborativeEditingAsyncEvent from(long contentId, SynchronyRequestEvent synchronyRequestEvent) {
        return new CollaborativeEditingAsyncEvent(contentId, EventType.SYNCHRONY_REQUEST).addProperty(Key.TYPE, synchronyRequestEvent.getType()).addProperty(Key.URL, synchronyRequestEvent.getUrl()).addProperty(Key.PROPERTIES, synchronyRequestEvent.getParams()).addProperty(Key.SUCCESS_FLAG, synchronyRequestEvent.isSuccessful());
    }

    public static CollaborativeEditingAsyncEvent from(long contentId, SynchronyRecoveryEvent synchronyRecoveryEvent, String syncRev, String confRev) {
        return new CollaborativeEditingAsyncEvent(contentId, EventType.SYNCHRONY_RECOVERY).addProperty(Key.TYPE, "SynchronyRecoveryEvent").addProperty(Key.SYNC_REV, syncRev).addProperty(Key.CONF_REV, confRev).addProperty(Key.TRIGGER, "unknown");
    }

    public static CollaborativeEditingAsyncEvent from(long contentId, ContentUpdatedEvent contentUpdatedEvent, String confRev) {
        return new CollaborativeEditingAsyncEvent(contentId, EventType.CONTENT_UPDATED).addProperty(Key.TYPE, "ContentUpdatedEvent").addProperty(Key.SYNC_REV, contentUpdatedEvent.getSyncRev()).addProperty(Key.CONF_REV, confRev).addProperty(Key.TRIGGER, contentUpdatedEvent.getUpdateTrigger().name());
    }

    public static CollaborativeEditingAsyncEvent from(long contentId, AttachmentCreateEvent attachmentCreateEvent, String syncRev, String confRev) {
        return new CollaborativeEditingAsyncEvent(contentId, EventType.ATTACHMENT_CREATE).addProperty(Key.TYPE, "AttachmentCreateEvent").addProperty(Key.SYNC_REV, syncRev).addProperty(Key.CONF_REV, confRev).addProperty(Key.TRIGGER, PageUpdateTrigger.VIEW_PAGE.name());
    }

    public static CollaborativeEditingAsyncEvent from(long contentId, AttachmentUpdateEvent attachmentUpdateEvent, String syncRev, String confRev) {
        return new CollaborativeEditingAsyncEvent(contentId, EventType.ATTACHMENT_UPDATE).addProperty(Key.TYPE, "AttachmentUpdateEvent").addProperty(Key.SYNC_REV, syncRev).addProperty(Key.CONF_REV, confRev).addProperty(Key.TRIGGER, PageUpdateTrigger.VIEW_PAGE.name());
    }

    public CollaborativeEditingAsyncEvent addProperty(Key key, @Nullable Object value) {
        if (value != null) {
            this.properties.put(Objects.requireNonNull(key), value);
        }
        return this;
    }

    public Object getProperty(Key key) {
        return this.properties.get((Object)Objects.requireNonNull(key));
    }

    public long getContentId() {
        return this.contentId;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public Map<Key, Object> getProperties() {
        return this.properties;
    }

    public static enum Key {
        TYPE,
        URL,
        SUCCESS_FLAG,
        TRIGGER,
        SYNC_REV,
        CONF_REV,
        PROPERTIES;

    }

    public static enum EventType {
        ATTACHMENT_CREATE,
        ATTACHMENT_UPDATE,
        CONTENT_UPDATED,
        SYNCHRONY_RECOVERY,
        SYNCHRONY_REQUEST;

    }
}

