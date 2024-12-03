/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.scheduler.status;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;
import javax.annotation.Nonnull;

@PublicApi
public interface RunDetails {
    public static final int MAXIMUM_MESSAGE_LENGTH = 255;

    @Nonnull
    public Date getStartTime();

    public long getDurationInMillis();

    @Nonnull
    public RunOutcome getRunOutcome();

    @Nonnull
    public String getMessage();
}

