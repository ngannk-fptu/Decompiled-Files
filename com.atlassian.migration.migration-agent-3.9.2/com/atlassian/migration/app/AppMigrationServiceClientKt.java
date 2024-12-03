/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=2, xi=48, d1={"\u0000\u0014\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00060\u0001j\u0002`\u00022\u0006\u0010\u0003\u001a\u00020\u00042\n\u0010\u0005\u001a\u00060\u0001j\u0002`\u0002\u00a8\u0006\u0006"}, d2={"handleHttpServiceException", "Ljava/lang/RuntimeException;", "Lkotlin/RuntimeException;", "status", "", "httpServiceException", "app-migration-assistant"})
public final class AppMigrationServiceClientKt {
    @NotNull
    public static final RuntimeException handleHttpServiceException(int status, @NotNull RuntimeException httpServiceException) {
        Intrinsics.checkNotNullParameter((Object)httpServiceException, (String)"httpServiceException");
        return status == 429 ? new RuntimeException("App migration API rate limit exceeded.") : httpServiceException;
    }
}

