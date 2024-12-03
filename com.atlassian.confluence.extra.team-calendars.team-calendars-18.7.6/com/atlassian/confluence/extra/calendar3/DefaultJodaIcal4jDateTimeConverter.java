/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.joda.time.DateTimeZone
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jDateTimeConverter;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultJodaIcal4jDateTimeConverter
implements JodaIcal4jDateTimeConverter {
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;

    @Autowired
    public DefaultJodaIcal4jDateTimeConverter(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
    }

    @Override
    public org.joda.time.DateTime toJodaTime(Date ical4jDate, TimeZone timeZone) {
        return new org.joda.time.DateTime(ical4jDate.getTime(), this.getTimeZone(ical4jDate, timeZone));
    }

    @Override
    public Date toIcal4jDate(org.joda.time.DateTime jodaDate) {
        return CalendarUtil.toIcal4jDate(jodaDate);
    }

    @Override
    public DateTime toIcal4jDateTime(org.joda.time.DateTime jodaDate) {
        return CalendarUtil.toIcal4jDateTime(this.jodaIcal4jTimeZoneMapper, jodaDate);
    }

    private DateTimeZone getTimeZone(Date ical4jDate, java.util.TimeZone timeZone) {
        TimeZone ical4jTimeZone;
        if (ical4jDate instanceof DateTime) {
            DateTime ical4jDateTime = (DateTime)ical4jDate;
            if (ical4jDateTime.isUtc()) {
                ical4jTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(DateTimeZone.UTC.getID());
            } else {
                ical4jTimeZone = ical4jDateTime.getTimeZone();
                if (null != ical4jTimeZone && this.jodaIcal4jTimeZoneMapper.isTimeZoneIdAnAlias(ical4jTimeZone.getID())) {
                    ical4jTimeZone = this.jodaIcal4jTimeZoneMapper.getIcal4jTimeZone(ical4jTimeZone.getID());
                }
            }
        } else {
            ical4jTimeZone = this.jodaIcal4jTimeZoneMapper.toIcal4jTimeZone(DateTimeZone.UTC.getID());
        }
        return this.jodaIcal4jTimeZoneMapper.toJodaTimeZone(null == ical4jTimeZone ? timeZone.getID() : ical4jTimeZone.getID());
    }
}

