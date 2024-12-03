/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.util.CustomParam;
import java.time.ZoneId;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005R\u0014\u0010\u0006\u001a\u00020\u0002X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/rest/util/ZoneIdParam;", "Lcom/addonengine/addons/analytics/rest/util/CustomParam;", "Ljava/time/ZoneId;", "param", "", "(Ljava/lang/String;)V", "value", "getValue", "()Ljava/time/ZoneId;", "analytics"})
public final class ZoneIdParam
implements CustomParam<ZoneId> {
    @NotNull
    private final ZoneId value;

    public ZoneIdParam(@NotNull String param) {
        Intrinsics.checkNotNullParameter((Object)param, (String)"param");
        ZoneId zoneId = ZoneId.of(param);
        Intrinsics.checkNotNull((Object)zoneId);
        this.value = zoneId;
    }

    @Override
    @NotNull
    public ZoneId getValue() {
        return this.value;
    }
}

