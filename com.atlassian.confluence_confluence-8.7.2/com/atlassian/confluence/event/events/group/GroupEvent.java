/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 */
package com.atlassian.confluence.event.events.group;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.user.Group;

public class GroupEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -3235128115069693407L;
    private final Group group;

    public GroupEvent(Object src, Group group) {
        super(src);
        this.group = group;
    }

    public Group getGroup() {
        return this.group;
    }
}

