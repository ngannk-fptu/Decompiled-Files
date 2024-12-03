/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.sal.api.user.UserProfile
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.event;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.sal.api.user.UserProfile;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AsynchronousPreferred
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0017\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\bR\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u000f"}, d2={"Lcom/addonengine/addons/analytics/event/AsyncTrackedConfluenceEvent;", "", "event", "Lcom/atlassian/confluence/event/events/ConfluenceEvent;", "user", "Lcom/atlassian/sal/api/user/UserProfile;", "userAgent", "", "(Lcom/atlassian/confluence/event/events/ConfluenceEvent;Lcom/atlassian/sal/api/user/UserProfile;Ljava/lang/String;)V", "getEvent", "()Lcom/atlassian/confluence/event/events/ConfluenceEvent;", "getUser", "()Lcom/atlassian/sal/api/user/UserProfile;", "getUserAgent", "()Ljava/lang/String;", "analytics"})
public class AsyncTrackedConfluenceEvent {
    @NotNull
    private final ConfluenceEvent event;
    @Nullable
    private final UserProfile user;
    @Nullable
    private final String userAgent;

    public AsyncTrackedConfluenceEvent(@NotNull ConfluenceEvent event, @Nullable UserProfile user, @Nullable String userAgent) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        this.event = event;
        this.user = user;
        this.userAgent = userAgent;
    }

    @NotNull
    public ConfluenceEvent getEvent() {
        return this.event;
    }

    @Nullable
    public UserProfile getUser() {
        return this.user;
    }

    @Nullable
    public String getUserAgent() {
        return this.userAgent;
    }
}

