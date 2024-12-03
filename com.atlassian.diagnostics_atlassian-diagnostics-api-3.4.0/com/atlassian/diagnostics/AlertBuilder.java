/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertTrigger;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AlertBuilder {
    @Nonnull
    public Alert build();

    @Nonnull
    public AlertBuilder details(@Nullable Object var1);

    @Nonnull
    public AlertBuilder detailsAsJson(@Nullable String var1);

    @Nonnull
    public AlertBuilder timestamp(@Nonnull Instant var1);

    @Nonnull
    public AlertBuilder trigger(@Nullable AlertTrigger var1);
}

