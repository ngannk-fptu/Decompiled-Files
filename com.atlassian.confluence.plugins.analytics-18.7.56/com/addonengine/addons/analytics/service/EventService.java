/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.sal.api.user.UserProfile
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventQuery;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.sal.api.user.UserProfile;
import java.util.stream.Stream;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J$\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH&J,\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000fH&J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0014H&J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0014H&R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/EventService;", "", "ao", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "getAo", "()Lcom/atlassian/activeobjects/external/ActiveObjects;", "save", "", "event", "Lcom/atlassian/confluence/event/events/ConfluenceEvent;", "user", "Lcom/atlassian/sal/api/user/UserProfile;", "userAgent", "", "eventAt", "", "stream", "Ljava/util/stream/Stream;", "Lcom/addonengine/addons/analytics/service/Event;", "query", "Lcom/addonengine/addons/analytics/service/EventQuery;", "streamUnsecured", "analytics"})
public interface EventService {
    @NotNull
    public ActiveObjects getAo();

    public void save(@NotNull ConfluenceEvent var1, @Nullable UserProfile var2, @Nullable String var3);

    public void save(@NotNull ConfluenceEvent var1, @Nullable UserProfile var2, @Nullable String var3, long var4);

    @NotNull
    public Stream<Event> stream(@NotNull EventQuery var1);

    @NotNull
    public Stream<Event> streamUnsecured(@NotNull EventQuery var1);
}

