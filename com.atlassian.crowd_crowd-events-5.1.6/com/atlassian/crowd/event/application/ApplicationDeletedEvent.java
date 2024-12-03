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

public class ApplicationDeletedEvent {
    private final ImmutableApplication application;

    public ApplicationDeletedEvent(Application application) {
        this.application = ImmutableApplication.from((Application)application);
    }

    public Application getApplication() {
        return this.application;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationDeletedEvent that = (ApplicationDeletedEvent)o;
        return Objects.equal((Object)this.application, (Object)that.application);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.application});
    }
}

