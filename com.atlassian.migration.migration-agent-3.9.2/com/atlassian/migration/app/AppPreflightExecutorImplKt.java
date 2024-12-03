/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppPreflightExecutorImpl;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={1, 7, 1}, k=2, xi=48, d1={"\u0000\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0004"}, d2={"ALLOWED_CHECK_LIMIT_PER_APP", "", "log", "Lorg/slf4j/Logger;", "app-migration-assistant"})
public final class AppPreflightExecutorImplKt {
    @NotNull
    private static final Logger log;
    private static final int ALLOWED_CHECK_LIMIT_PER_APP = 3;

    public static final /* synthetic */ Logger access$getLog$p() {
        return log;
    }

    static {
        Logger logger = LoggerFactory.getLogger(AppPreflightExecutorImpl.class);
        Intrinsics.checkNotNull((Object)logger);
        log = logger;
    }
}

