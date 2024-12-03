/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.io.Serializable;
import java.util.Collection;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import org.bedework.util.misc.Logged;
import org.bedework.util.timezones.TimeZoneName;
import org.bedework.util.timezones.TimezonesException;
import org.bedework.util.timezones.model.TimezoneListType;

public abstract class Timezones
extends Logged
implements Serializable {
    private static final ThreadLocal<String> threadTzid = new ThreadLocal();
    private static Timezones tzs;
    private static final TimeZoneRegistry tzRegistry;

    public static TimeZoneRegistry getTzRegistry() {
        return tzRegistry;
    }

    public static void initTimezones(String serverUrl) throws TimezonesException {
        try {
            if (tzs == null) {
                tzs = (Timezones)Class.forName("org.bedework.util.timezones.TimezonesImpl").newInstance();
            }
            tzs.init(serverUrl);
        }
        catch (TimezonesException te) {
            throw te;
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    public static Timezones getTimezones() {
        if (tzs == null) {
            throw new RuntimeException("Timezones not initialized");
        }
        return tzs;
    }

    public static void setSystemDefaultTzid(String id) throws TimezonesException {
        Timezones.getTimezones().setDefaultTimeZoneId(id);
    }

    public static String getSystemDefaultTzid() throws TimezonesException {
        return Timezones.getTimezones().getDefaultTimeZoneId();
    }

    public static void setThreadDefaultTzid(String id) throws TimezonesException {
        threadTzid.set(id);
    }

    public static String getThreadDefaultTzid() throws TimezonesException {
        String id = threadTzid.get();
        if (id != null) {
            return id;
        }
        return Timezones.getSystemDefaultTzid();
    }

    public static TimeZone getDefaultTz() throws TimezonesException {
        return Timezones.getTz(Timezones.getThreadDefaultTzid());
    }

    public static TimeZone getTz(String id) throws TimezonesException {
        return Timezones.getTimezones().getTimeZone(id);
    }

    public static String getUtc(String time, String tzid) throws TimezonesException {
        return Timezones.getTimezones().calculateUtc(time, tzid);
    }

    public static void registerTz(String id, TimeZone timezone) throws TimezonesException {
    }

    public static Collection<TimeZoneName> getTzNames() throws TimezonesException {
        return Timezones.getTimezones().getTimeZoneNames();
    }

    public static void refreshTzs() throws TimezonesException {
        Timezones.getTimezones().refreshTimezones();
    }

    public abstract void init(String var1) throws TimezonesException;

    public abstract TimeZone getTimeZone(String var1) throws TimezonesException;

    public abstract TaggedTimeZone getTimeZone(String var1, String var2) throws TimezonesException;

    public abstract Collection<TimeZoneName> getTimeZoneNames() throws TimezonesException;

    public abstract TimezoneListType getList(String var1) throws TimezonesException;

    public abstract void refreshTimezones() throws TimezonesException;

    public abstract String unalias(String var1) throws TimezonesException;

    public abstract void setDefaultTimeZoneId(String var1) throws TimezonesException;

    public abstract String getDefaultTimeZoneId() throws TimezonesException;

    public abstract TimeZone getDefaultTimeZone() throws TimezonesException;

    public abstract void register(String var1, TimeZone var2) throws TimezonesException;

    public abstract String calculateUtc(String var1, String var2) throws TimezonesException;

    public abstract long getDatesCached();

    public abstract long getDateCacheHits();

    public abstract long getDateCacheMisses();

    static {
        tzRegistry = new TzRegistry();
    }

    public static class TaggedTimeZone {
        public String etag;
        public String vtz;
        public TimeZone tz;

        public TaggedTimeZone(String etag) {
            this.etag = etag;
        }

        public TaggedTimeZone(String etag, String vtz) {
            this.etag = etag;
            this.vtz = vtz;
        }
    }

    private static class TzRegistry
    implements TimeZoneRegistry {
        private TzRegistry() {
        }

        @Override
        public void register(TimeZone timezone) {
        }

        @Override
        public void register(TimeZone timezone, boolean update) {
        }

        @Override
        public void clear() {
        }

        @Override
        public TimeZone getTimeZone(String id) {
            try {
                return Timezones.getTimezones().getTimeZone(id);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }
}

