/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.timezone;

import com.atlassian.sal.api.user.UserKey;
import java.util.TimeZone;
import javax.annotation.Nonnull;

public interface TimeZoneManager {
    @Nonnull
    public TimeZone getUserTimeZone();

    @Nonnull
    public TimeZone getUserTimeZone(@Nonnull UserKey var1);

    @Nonnull
    public TimeZone getDefaultTimeZone();
}

