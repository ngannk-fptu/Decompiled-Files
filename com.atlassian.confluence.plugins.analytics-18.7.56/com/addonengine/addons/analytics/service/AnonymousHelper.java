/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00042\b\u0010\u0007\u001a\u0004\u0018\u00010\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2={"Lcom/addonengine/addons/analytics/service/AnonymousHelper;", "", "()V", "frontendAnonymousUserKey", "", "storedAnonymousUserKey", "userKeyToFrontendFormat", "userKey", "analytics"})
public final class AnonymousHelper {
    @NotNull
    public static final AnonymousHelper INSTANCE = new AnonymousHelper();
    @NotNull
    public static final String storedAnonymousUserKey = "[anonymous]";
    @NotNull
    public static final String frontendAnonymousUserKey = "";

    private AnonymousHelper() {
    }

    @Nullable
    public final String userKeyToFrontendFormat(@Nullable String userKey) {
        if (Intrinsics.areEqual((Object)userKey, (Object)storedAnonymousUserKey)) {
            return frontendAnonymousUserKey;
        }
        return userKey;
    }
}

