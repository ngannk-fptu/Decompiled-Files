/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00060\u0001j\u0002`\u0002B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/NoSpaceOrNoPermissionException;", "Ljava/lang/Exception;", "Lkotlin/Exception;", "spaceKey", "", "(Ljava/lang/String;)V", "getSpaceKey", "()Ljava/lang/String;", "analytics"})
public final class NoSpaceOrNoPermissionException
extends Exception {
    @NotNull
    private final String spaceKey;

    public NoSpaceOrNoPermissionException(@NotNull String spaceKey) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        super("No space exists with key '" + spaceKey + "', or the calling user does not have permission to view the content.");
        this.spaceKey = spaceKey;
    }

    @NotNull
    public final String getSpaceKey() {
        return this.spaceKey;
    }
}

