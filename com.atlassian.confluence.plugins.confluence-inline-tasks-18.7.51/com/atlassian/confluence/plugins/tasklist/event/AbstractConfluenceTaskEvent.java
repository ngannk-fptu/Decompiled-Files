/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.confluence.event.events.types.UserDriven
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.tasklist.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.types.UserDriven;
import com.atlassian.confluence.plugins.tasklist.Task;
import com.atlassian.user.User;

public abstract class AbstractConfluenceTaskEvent
extends ConfluenceEvent
implements UserDriven {
    protected final User user;
    protected final Task task;

    public AbstractConfluenceTaskEvent(Object src, User user, Task task) {
        super(src);
        this.user = user;
        this.task = task;
    }

    public Task getTask() {
        return this.task;
    }

    public User getOriginatingUser() {
        return this.user;
    }

    public String toString() {
        return ((Object)((Object)this)).getClass().getSimpleName() + "{user=" + this.user + ", task=" + this.task + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AbstractConfluenceTaskEvent that = (AbstractConfluenceTaskEvent)((Object)o);
        if (this.task != null ? !this.task.equals(that.task) : that.task != null) {
            return false;
        }
        return !(this.user != null ? !this.user.equals(that.user) : that.user != null);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.user != null ? this.user.hashCode() : 0);
        result = 31 * result + (this.task != null ? this.task.hashCode() : 0);
        return result;
    }
}

