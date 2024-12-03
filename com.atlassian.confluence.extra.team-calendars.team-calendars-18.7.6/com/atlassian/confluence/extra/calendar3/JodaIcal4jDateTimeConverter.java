/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 */
package com.atlassian.confluence.extra.calendar3;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;

public interface JodaIcal4jDateTimeConverter {
    public org.joda.time.DateTime toJodaTime(Date var1, TimeZone var2);

    public Date toIcal4jDate(org.joda.time.DateTime var1);

    public DateTime toIcal4jDateTime(org.joda.time.DateTime var1);
}

