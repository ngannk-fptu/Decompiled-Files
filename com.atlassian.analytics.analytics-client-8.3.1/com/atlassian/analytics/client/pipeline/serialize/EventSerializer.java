/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.pipeline.serialize;

import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import com.atlassian.analytics.event.RawEvent;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface EventSerializer {
    public Supplier<RawEvent> toAnalyticsEvent(Object var1, @Nullable String var2, RequestInfo var3);
}

