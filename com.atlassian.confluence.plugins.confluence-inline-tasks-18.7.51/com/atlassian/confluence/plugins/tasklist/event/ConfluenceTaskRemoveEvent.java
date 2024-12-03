/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.confluence.plugins.tasklist.event.AbstractConfluenceTaskEvent;
import com.atlassian.user.User;

public class ConfluenceTaskRemoveEvent
extends AbstractConfluenceTaskEvent {
    public ConfluenceTaskRemoveEvent(Object src, User user, Task task) {
        super(src, user, task);
    }
}

