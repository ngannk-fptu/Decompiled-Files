/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.util.ArrayList;
import java.util.List;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.bedework.util.timezones.TimeZoneName;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;
import org.bedework.util.timezones.TimezonesImpl;
import org.bedework.util.timezones.TzFetcher;

public class ServerTzFetcher
implements TzFetcher {
    private final Timezones tzs = new TimezonesImpl();

    public ServerTzFetcher(String tzsvrUri) throws TimezonesException {
        this.tzs.init(tzsvrUri);
    }

    @Override
    public VTimeZone getTz(String tzid) throws TimezonesException {
        TimeZone tz = this.tzs.getTimeZone(tzid);
        if (tz == null) {
            return null;
        }
        return tz.getVTimeZone();
    }

    @Override
    public List<String> getTzids() throws TimezonesException {
        ArrayList<String> ids = new ArrayList<String>();
        for (TimeZoneName tzn : this.tzs.getTimeZoneNames()) {
            ids.add(tzn.getId());
        }
        return ids;
    }

    @Override
    public void close() throws Exception {
    }
}

