/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 */
package com.atlassian.confluence.event.events.group;

import com.atlassian.confluence.event.events.group.GroupEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.user.Group;

public class GroupCreateEvent
extends GroupEvent
implements Created {
    private static final long serialVersionUID = 5169676855826892723L;

    public GroupCreateEvent(Object src, Group group) {
        super(src, group);
    }
}

