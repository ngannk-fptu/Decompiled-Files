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

import com.atlassian.migration.app.VendorCheckRetriever;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Metadata(mv={1, 7, 1}, k=2, xi=48, d1={"\u0000\b\n\u0000\n\u0002\u0018\u0002\n\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0002"}, d2={"log", "Lorg/slf4j/Logger;", "app-migration-assistant"})
public final class VendorCheckRetrieverKt {
    @NotNull
    private static final Logger log;

    public static final /* synthetic */ Logger access$getLog$p() {
        return log;
    }

    static {
        Logger logger = LoggerFactory.getLogger(VendorCheckRetriever.class);
        Intrinsics.checkNotNull((Object)logger);
        log = logger;
    }
}

