/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.event;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.content.ContentEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentEvent;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.event.Event;

public class EventUtils {
    public static Event extractWrappedEventOrOriginal(Event event) {
        while (event instanceof ClusterEventWrapper) {
            event = ((ClusterEventWrapper)event).getEvent();
        }
        return event;
    }

    public static ContentTypeEnum getEventContentType(Event event) {
        ContentEvent contentEvent;
        ContentEntityObject content;
        if (event instanceof AttachmentEvent) {
            return ContentTypeEnum.ATTACHMENT;
        }
        if (event instanceof ContentEvent && (content = (contentEvent = (ContentEvent)event).getContent()) != null) {
            return content.getTypeEnum();
        }
        return null;
    }
}

