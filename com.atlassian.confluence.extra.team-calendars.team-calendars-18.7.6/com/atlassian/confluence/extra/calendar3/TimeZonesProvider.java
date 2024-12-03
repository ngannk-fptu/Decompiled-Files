/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.confluence.extra.calendar3;

import java.util.Map;
import org.joda.time.DateTimeZone;

public interface TimeZonesProvider {
    public Map<String, DateTimeZone> getAvailableTimeZones();
}

