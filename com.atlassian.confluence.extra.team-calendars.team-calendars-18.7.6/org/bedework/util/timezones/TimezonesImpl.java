/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.io.Reader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.TreeSet;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VTimeZone;
import org.bedework.util.caching.FlushMap;
import org.bedework.util.timezones.DateTimeUtil;
import org.bedework.util.timezones.TimeZoneName;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;
import org.bedework.util.timezones.TzServer;
import org.bedework.util.timezones.model.TimezoneListType;
import org.bedework.util.timezones.model.TimezoneType;

public class TimezonesImpl
extends Timezones {
    private String serverUrl;
    protected String defaultTimeZoneId;
    protected transient TimeZone defaultTimeZone;
    private static final FlushMap<String, TzServer> tzServers = new FlushMap();
    protected FlushMap<String, TimeZone> timezones = new FlushMap(3600000L, 100);
    protected static volatile Collection<TimeZoneName> timezoneNames;
    private final UTCDateCaches dateCaches = new UTCDateCaches();
    private static Properties aliases;
    private long datesCached;
    private long dateCacheHits;
    private long dateCacheMisses;
    private static final java.util.Calendar cal;
    private static final java.util.TimeZone utctz;

    @Override
    public void init(String serverUrl) throws TimezonesException {
        this.serverUrl = serverUrl;
    }

    @Override
    public TimeZone getTimeZone(String id) throws TimezonesException {
        TimeZone tz = this.timezones.get(id);
        if (tz != null) {
            return tz;
        }
        tz = this.fetchTimeZone(id);
        this.register(id, tz);
        return tz;
    }

    @Override
    public Timezones.TaggedTimeZone getTimeZone(String id, String etag) throws TimezonesException {
        return this.fetchTimeZone(id, etag);
    }

    @Override
    public Collection<TimeZoneName> getTimeZoneNames() throws TimezonesException {
        if (timezoneNames != null) {
            return timezoneNames;
        }
        try (TzServer server = TimezonesImpl.getTzServer(this.serverUrl);){
            TimezoneListType tzlist = server.getList(null);
            TreeSet<TimeZoneName> ids = new TreeSet<TimeZoneName>();
            for (TimezoneType s : tzlist.getTimezones()) {
                ids.add(new TimeZoneName(s.getTzid()));
            }
            timezoneNames = Collections.unmodifiableCollection(ids);
            Collection<TimeZoneName> collection = timezoneNames;
            return collection;
        }
    }

    @Override
    public TimezoneListType getList(String changedSince) throws TimezonesException {
        try (TzServer server = TimezonesImpl.getTzServer(this.serverUrl);){
            TimezoneListType timezoneListType = server.getList(changedSince);
            return timezoneListType;
        }
    }

    @Override
    public synchronized void refreshTimezones() throws TimezonesException {
        timezoneNames = null;
        this.timezones.clear();
    }

    @Override
    public String unalias(String tzid) throws TimezonesException {
        String target = tzid = TimezonesImpl.transformTzid(tzid);
        if (aliases == null) {
            aliases = TimezonesImpl.getTzServer(this.serverUrl).getAliases();
        }
        for (int i = 0; i < 100; ++i) {
            String unaliased = aliases.getProperty(target);
            if (unaliased == null) {
                return target;
            }
            if (unaliased.equals(tzid)) break;
            target = unaliased;
        }
        this.error("Possible circular alias chain looking for " + tzid);
        return null;
    }

    @Override
    public void setDefaultTimeZoneId(String id) throws TimezonesException {
        this.defaultTimeZone = null;
        this.defaultTimeZoneId = id;
    }

    @Override
    public String getDefaultTimeZoneId() throws TimezonesException {
        return this.defaultTimeZoneId;
    }

    @Override
    public TimeZone getDefaultTimeZone() throws TimezonesException {
        if (this.defaultTimeZone == null && this.defaultTimeZoneId != null) {
            this.defaultTimeZone = this.getTimeZone(this.defaultTimeZoneId);
        }
        return this.defaultTimeZone;
    }

    @Override
    public synchronized String calculateUtc(String timePar, String tzidPar) throws TimezonesException {
        try {
            TimeZone tz;
            if (DateTimeUtil.isISODateTimeUTC(timePar)) {
                return timePar;
            }
            String time = timePar;
            String dateKey = null;
            String tzid = tzidPar;
            if (tzid == null) {
                tzid = TimezonesImpl.getThreadDefaultTzid();
            }
            UTCDateCache cache = this.dateCaches.get(tzid);
            if (time.length() == 8 && DateTimeUtil.isISODate(time)) {
                String utc;
                if (cache != null && (utc = (String)cache.get(time)) != null) {
                    ++this.dateCacheHits;
                    return utc;
                }
                ++this.dateCacheMisses;
                dateKey = time;
                time = time + "T000000";
            } else if (!DateTimeUtil.isISODateTime(time)) {
                throw new DateTimeUtil.BadDateException(time);
            }
            if (cache != null) {
                if (!tzid.equals(cache.tzid)) {
                    this.dateCaches.clear();
                    throw new TimezonesException(TimezonesException.cacheError, tzid);
                }
                tz = cache.tz;
            } else {
                tz = this.getTimeZone(tzid);
                if (tz == null) {
                    throw new TimezonesException(TimezonesException.unknownTimezone, tzid);
                }
                cache = new UTCDateCache(tzid, tz);
                this.dateCaches.put(tzid, cache);
            }
            SimpleDateFormat formatTd = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
            formatTd.setTimeZone(tz);
            Date date = formatTd.parse(time);
            cal.clear();
            cal.setTime(date);
            StringBuilder sb = new StringBuilder();
            this.digit4(sb, cal.get(1));
            this.digit2(sb, cal.get(2) + 1);
            this.digit2(sb, cal.get(5));
            sb.append('T');
            this.digit2(sb, cal.get(11));
            this.digit2(sb, cal.get(12));
            this.digit2(sb, cal.get(13));
            sb.append('Z');
            String utc = sb.toString();
            if (dateKey != null) {
                cache.put(dateKey, utc);
                ++this.datesCached;
            }
            return utc;
        }
        catch (TimezonesException cfe) {
            throw cfe;
        }
        catch (DateTimeUtil.BadDateException bde) {
            throw new TimezonesException(TimezonesException.badDate, timePar);
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    @Override
    public long getDatesCached() {
        return this.datesCached;
    }

    @Override
    public long getDateCacheHits() {
        return this.dateCacheHits;
    }

    @Override
    public long getDateCacheMisses() {
        return this.dateCacheMisses;
    }

    protected TimeZone fetchTimeZone(String id) throws TimezonesException {
        Timezones.TaggedTimeZone ttz = this.fetchTimeZone(id, null);
        if (ttz == null) {
            return null;
        }
        this.register(id, ttz.tz);
        return ttz.tz;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected Timezones.TaggedTimeZone fetchTimeZone(String id, String etag) throws TimezonesException {
        try (TzServer server = TimezonesImpl.getTzServer(this.serverUrl);){
            Timezones.TaggedTimeZone ttz = server.getTz(id, etag);
            if (ttz == null) {
                Timezones.TaggedTimeZone taggedTimeZone = null;
                return taggedTimeZone;
            }
            CalendarBuilder cb = new CalendarBuilder();
            UnfoldingReader ufrdr = new UnfoldingReader((Reader)new StringReader(ttz.vtz), true);
            Calendar cal = cb.build(ufrdr);
            VTimeZone vtz = (VTimeZone)cal.getComponents().getComponent("VTIMEZONE");
            if (vtz == null) {
                throw new TimezonesException("Incorrectly stored timezone");
            }
            ttz.tz = new TimeZone(vtz);
            Timezones.TaggedTimeZone taggedTimeZone = ttz;
            return taggedTimeZone;
        }
        catch (Throwable t) {
            throw new TimezonesException(t);
        }
    }

    @Override
    public synchronized void register(String id, TimeZone timezone) throws TimezonesException {
        this.timezones.put(id, timezone);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static TzServer getTzServer(String url) throws TimezonesException {
        FlushMap<String, TzServer> flushMap = tzServers;
        synchronized (flushMap) {
            TzServer svr = tzServers.get(url);
            if (svr != null) {
                return svr;
            }
            svr = new TzServer(url);
            tzServers.put(url, svr);
            return svr;
        }
    }

    private static String transformTzid(String tzid) {
        int len = tzid.length();
        if (len > 13 && tzid.startsWith("/mozilla.org/")) {
            int pos = tzid.indexOf(47, 13);
            if (pos < 0 || pos == len - 1) {
                return tzid;
            }
            return tzid.substring(pos + 1);
        }
        return tzid;
    }

    private void digit2(StringBuilder sb, int val) throws DateTimeUtil.BadDateException {
        if (val > 99) {
            throw new DateTimeUtil.BadDateException();
        }
        if (val < 10) {
            sb.append("0");
        }
        sb.append(val);
    }

    private void digit4(StringBuilder sb, int val) throws DateTimeUtil.BadDateException {
        if (val > 9999) {
            throw new DateTimeUtil.BadDateException();
        }
        if (val < 10) {
            sb.append("000");
        } else if (val < 100) {
            sb.append("00");
        } else if (val < 1000) {
            sb.append("0");
        }
        sb.append(val);
    }

    static {
        cal = java.util.Calendar.getInstance();
        try {
            utctz = TimeZone.getTimeZone("Etc/UTC");
        }
        catch (Throwable t) {
            throw new RuntimeException("Unable to initialise UTC timezone");
        }
        cal.setTimeZone(utctz);
    }

    private class UTCDateCaches
    extends FlushMap<String, UTCDateCache> {
        private UTCDateCache defaultDateCache;

        private UTCDateCaches() {
        }

        private boolean isDefault(String tzid) {
            return TimezonesImpl.this.defaultTimeZoneId != null && TimezonesImpl.this.defaultTimeZoneId.equals(tzid);
        }

        @Override
        public boolean containsKey(Object key) {
            if (this.isDefault((String)key)) {
                return this.defaultDateCache != null;
            }
            return super.containsKey(key);
        }

        @Override
        public synchronized UTCDateCache put(String key, UTCDateCache val) {
            if (!this.isDefault(key)) {
                return super.put(key, val);
            }
            UTCDateCache cache = this.defaultDateCache;
            this.defaultDateCache = val;
            return cache;
        }

        @Override
        public UTCDateCache get(Object key) {
            if (!this.isDefault((String)key)) {
                return (UTCDateCache)super.get(key);
            }
            return this.defaultDateCache;
        }
    }

    private static class UTCDateCache
    extends FlushMap<String, String> {
        String tzid;
        TimeZone tz;

        private UTCDateCache(String tzid, TimeZone tz) {
            super(100, 0L, 1000);
            this.tzid = tzid;
            this.tz = tz;
        }
    }
}

