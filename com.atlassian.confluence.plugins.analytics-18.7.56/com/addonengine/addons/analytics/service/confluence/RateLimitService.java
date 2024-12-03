/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/RateLimitService;", "", "clearActiveOperationCounts", "", "decrementOperationCount", "sessionId", "", "rateLimit", "", "analytics"})
public interface RateLimitService {
    public boolean rateLimit(@NotNull String var1);

    public void decrementOperationCount(@NotNull String var1);

    public void clearActiveOperationCounts();
}

