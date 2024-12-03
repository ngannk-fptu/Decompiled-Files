/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.space;

import com.atlassian.confluence.event.events.space.SpaceEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.spaces.Space;

public class SpaceLogoUpdateEvent
extends SpaceEvent
implements Updated {
    private static final long serialVersionUID = -2818441038779419488L;
    private final SpaceLogoActions eventType;
    private final Attachment logo;

    @Deprecated
    public SpaceLogoUpdateEvent(Object src, Space space, String eventType, Attachment logo) {
        super(src, space);
        this.eventType = SpaceLogoActions.fromString(eventType);
        this.logo = logo;
    }

    public SpaceLogoUpdateEvent(Object src, Space space, SpaceLogoActions eventType, Attachment logo) {
        super(src, space);
        this.eventType = eventType;
        this.logo = logo;
    }

    public SpaceLogoActions getEventTypeEnum() {
        return this.eventType;
    }

    @Deprecated
    public String getEventType() {
        return this.eventType.getText();
    }

    public Attachment getLogo() {
        return this.logo;
    }

    public static enum SpaceLogoActions {
        DISABLE,
        ENABLE,
        UPLOAD,
        DELETE;


        public String getText() {
            return this.name().toLowerCase();
        }

        public static SpaceLogoActions fromString(String text) {
            return SpaceLogoActions.valueOf(text.toUpperCase());
        }
    }
}

