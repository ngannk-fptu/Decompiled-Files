/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.google.common.base.Objects
 */
package com.atlassian.crowd.event.application;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.google.common.base.Objects;

public class ApplicationUpdatedEvent {
    protected final ImmutableApplication newApplication;
    protected final ImmutableApplication oldApplication;

    public ApplicationUpdatedEvent(Application oldApplication, Application newApplication) {
        this.oldApplication = ImmutableApplication.from((Application)oldApplication);
        this.newApplication = ImmutableApplication.from((Application)newApplication);
    }

    public Application getApplication() {
        return this.newApplication;
    }

    public Application getOldApplication() {
        return this.oldApplication;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationUpdatedEvent that = (ApplicationUpdatedEvent)o;
        return Objects.equal((Object)this.newApplication, (Object)that.newApplication) && Objects.equal((Object)this.oldApplication, (Object)that.oldApplication);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.newApplication, this.oldApplication});
    }

    public Long getApplicationId() {
        return this.newApplication.getId();
    }
}

