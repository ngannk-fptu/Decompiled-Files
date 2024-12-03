/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.types.Viewed
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.mail.archive.events;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.spaces.Space;

public class MailListViewEvent
extends ConfluenceEvent
implements Viewed {
    private final Space space;

    public MailListViewEvent(Object src, Space space) {
        super(src);
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }
}

