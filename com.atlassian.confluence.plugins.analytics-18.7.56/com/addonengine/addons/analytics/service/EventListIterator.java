/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.AbstractIterator
 *  kotlin.Metadata
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventCursor;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.Page;
import com.addonengine.addons.analytics.service.PageRequest;
import com.google.common.collect.AbstractIterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\u0001BG\u00120\u0010\u0004\u001a,\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0016\u0012\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0004\u0012\u00020\b0\t0\u0005\u0012\u0006\u0010\n\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0002H\u0014R8\u0010\u0004\u001a,\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u0016\u0012\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\u0004\u0012\u00020\b0\t0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/service/EventListIterator;", "Lcom/google/common/collect/AbstractIterator;", "", "Lcom/addonengine/addons/analytics/service/Event;", "fetch", "Lkotlin/Function2;", "Lcom/addonengine/addons/analytics/service/EventQuery;", "Lcom/addonengine/addons/analytics/service/PageRequest;", "Lcom/addonengine/addons/analytics/service/EventCursor;", "Lcom/addonengine/addons/analytics/service/Page;", "query", "pageSize", "", "(Lkotlin/jvm/functions/Function2;Lcom/addonengine/addons/analytics/service/EventQuery;I)V", "lastPage", "", "nextCursor", "computeNext", "analytics"})
public final class EventListIterator
extends AbstractIterator<List<? extends Event>> {
    @NotNull
    private final Function2<EventQuery, PageRequest<EventCursor>, Page<List<Event>, EventCursor>> fetch;
    @NotNull
    private final EventQuery query;
    private final int pageSize;
    @Nullable
    private EventCursor nextCursor;
    private boolean lastPage;

    public EventListIterator(@NotNull Function2<? super EventQuery, ? super PageRequest<EventCursor>, Page<List<Event>, EventCursor>> fetch, @NotNull EventQuery query, int pageSize) {
        Intrinsics.checkNotNullParameter(fetch, (String)"fetch");
        Intrinsics.checkNotNullParameter((Object)query, (String)"query");
        this.fetch = fetch;
        this.query = query;
        this.pageSize = pageSize;
    }

    @Nullable
    protected List<Event> computeNext() {
        List list;
        if (this.lastPage) {
            return (List)this.endOfData();
        }
        Page eventsPage = (Page)this.fetch.invoke((Object)this.query, new PageRequest<EventCursor>(this.pageSize, this.nextCursor));
        this.nextCursor = (EventCursor)eventsPage.getCursor();
        if (((List)eventsPage.getData()).isEmpty()) {
            list = (List)this.endOfData();
        } else {
            this.lastPage = ((List)eventsPage.getData()).size() < this.pageSize;
            list = (List)eventsPage.getData();
        }
        return list;
    }
}

