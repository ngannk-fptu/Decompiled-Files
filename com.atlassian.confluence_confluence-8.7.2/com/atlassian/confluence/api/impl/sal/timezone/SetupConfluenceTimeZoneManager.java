/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserKey
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.sal.timezone;

import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserKey;
import java.util.TimeZone;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SetupConfluenceTimeZoneManager
implements TimeZoneManager {
    public @NonNull TimeZone getUserTimeZone() {
        return this.getDefaultTimeZone();
    }

    public @NonNull TimeZone getUserTimeZone(UserKey userKey) {
        return this.getDefaultTimeZone();
    }

    public @NonNull TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }
}

