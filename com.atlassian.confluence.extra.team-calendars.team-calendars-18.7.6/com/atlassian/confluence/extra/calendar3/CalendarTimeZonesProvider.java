/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.TimeZonesProvider;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarTimeZonesProvider
implements TimeZonesProvider {
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;

    @Autowired
    public CalendarTimeZonesProvider(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
    }

    @Override
    public Map<String, DateTimeZone> getAvailableTimeZones() {
        LinkedHashMap<String, DateTimeZone> availableTimeZones = new LinkedHashMap<String, DateTimeZone>();
        for (String supportedTimeZoneId : this.jodaIcal4jTimeZoneMapper.getSupportedTimeZoneIds()) {
            availableTimeZones.put(supportedTimeZoneId, DateTimeZone.forID((String)supportedTimeZoneId));
        }
        return availableTimeZones;
    }
}

