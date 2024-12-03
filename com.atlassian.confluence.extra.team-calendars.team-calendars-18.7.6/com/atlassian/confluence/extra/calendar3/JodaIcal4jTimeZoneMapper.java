/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.TimeZone
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.joda.time.DateTimeZone
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Collection;
import java.util.Set;
import net.fortuna.ical4j.model.TimeZone;
import org.joda.time.DateTimeZone;

public interface JodaIcal4jTimeZoneMapper {
    public DateTimeZone toJodaTimeZone(String var1);

    public TimeZone toIcal4jTimeZone(String var1);

    public TimeZone getIcal4jTimeZone(String var1);

    public Set<String> getSupportedTimeZoneIds();

    public Collection<com.atlassian.confluence.core.TimeZone> getSupportConfluenceTimeZones();

    public String getSystemTimeZoneIdJoda(boolean var1);

    public String getSystemTimeZoneIdJoda();

    public String getUserTimeZoneIdJoda(ConfluenceUser var1);

    public boolean isTimeZoneIdAnAlias(String var1);

    public boolean isTimeZoneSupported(TimeZone var1);

    public String getTimeZoneIdForAlias(String var1);
}

