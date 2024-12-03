/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Group
 */
package com.atlassian.confluence.event.events.group;

import com.atlassian.confluence.event.events.group.GroupEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.user.Group;

public class GroupRemoveEvent
extends GroupEvent
implements Removed {
    private static final long serialVersionUID = -7527031808299725283L;

    public GroupRemoveEvent(Object src, Group group) {
        super(src, group);
    }
}

