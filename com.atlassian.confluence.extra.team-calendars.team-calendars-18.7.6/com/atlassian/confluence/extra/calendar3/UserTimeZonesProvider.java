/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.TimeZone
 *  org.joda.time.DateTimeZone
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.core.TimeZone;
import com.atlassian.confluence.extra.calendar3.JodaIcal4jTimeZoneMapper;
import com.atlassian.confluence.extra.calendar3.TimeZonesProvider;
import java.util.LinkedHashMap;
import java.util.Map;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserTimeZonesProvider
implements TimeZonesProvider {
    private final JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper;

    @Autowired
    public UserTimeZonesProvider(JodaIcal4jTimeZoneMapper jodaIcal4jTimeZoneMapper) {
        this.jodaIcal4jTimeZoneMapper = jodaIcal4jTimeZoneMapper;
    }

    @Override
    public Map<String, DateTimeZone> getAvailableTimeZones() {
        LinkedHashMap<String, DateTimeZone> availableTimeZones = new LinkedHashMap<String, DateTimeZone>();
        for (TimeZone supportedTimeZone : this.jodaIcal4jTimeZoneMapper.getSupportConfluenceTimeZones()) {
            availableTimeZones.put(supportedTimeZone.getID(), DateTimeZone.forID((String)supportedTimeZone.getID()));
        }
        return availableTimeZones;
    }
}

