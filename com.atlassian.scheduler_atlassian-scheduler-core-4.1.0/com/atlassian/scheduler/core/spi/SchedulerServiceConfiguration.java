/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.spi;

import java.util.TimeZone;
import javax.annotation.Nullable;

public interface SchedulerServiceConfiguration {
    @Nullable
    public TimeZone getDefaultTimeZone();
}

