/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.zone.ZoneOffsetTransition;
import java.time.zone.ZoneOffsetTransitionRule;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.Daylight;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.Standard;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.TzId;
import net.fortuna.ical4j.model.property.TzOffsetFrom;
import net.fortuna.ical4j.model.property.TzOffsetTo;
import net.fortuna.ical4j.model.property.TzUrl;
import net.fortuna.ical4j.util.Configurator;
import net.fortuna.ical4j.util.ResourceLoader;
import net.fortuna.ical4j.util.TimeZoneCache;
import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;

public class TimeZoneLoader {
    private static final String UPDATE_ENABLED = "net.fortuna.ical4j.timezone.update.enabled";
    private static final String UPDATE_CONNECT_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.connect";
    private static final String UPDATE_READ_TIMEOUT = "net.fortuna.ical4j.timezone.update.timeout.read";
    private static final String UPDATE_PROXY_ENABLED = "net.fortuna.ical4j.timezone.update.proxy.enabled";
    private static final String UPDATE_PROXY_TYPE = "net.fortuna.ical4j.timezone.update.proxy.type";
    private static final String UPDATE_PROXY_HOST = "net.fortuna.ical4j.timezone.update.proxy.host";
    private static final String UPDATE_PROXY_PORT = "net.fortuna.ical4j.timezone.update.proxy.port";
    private static final String TZ_CACHE_IMPL = "net.fortuna.ical4j.timezone.cache.impl";
    private static final String DEFAULT_TZ_CACHE_IMPL = "net.fortuna.ical4j.util.JCacheTimeZoneCache";
    private static final String MESSAGE_MISSING_DEFAULT_TZ_CACHE_IMPL = "Error loading default cache implementation. Please ensure the JCache API dependency is included in the classpath, or override the cache implementation (e.g. via configuration: net.fortuna.ical4j.timezone.cache.impl=net.fortuna.ical4j.util.MapTimeZoneCache)";
    private static Proxy proxy = null;
    private static final Set<String> TIMEZONE_DEFINITIONS = new HashSet<String>();
    private static final String DATE_TIME_TPL = "yyyyMMdd'T'HHmmss";
    private static final String RRULE_TPL = "FREQ=YEARLY;BYMONTH=%d;BYDAY=%d%s";
    private static final Standard NO_TRANSITIONS;
    private final String resourcePrefix;
    private final TimeZoneCache cache;

    public TimeZoneLoader(String resourcePrefix) {
        this(resourcePrefix, TimeZoneLoader.cacheInit());
    }

    public TimeZoneLoader(String resourcePrefix, TimeZoneCache cache) {
        this.resourcePrefix = resourcePrefix;
        this.cache = cache;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public VTimeZone loadVTimeZone(String id) throws IOException, ParserException, ParseException {
        Validate.notBlank((CharSequence)id, (String)"Invalid TimeZone ID: [%s]", (Object[])new Object[]{id});
        if (this.cache.containsId(id)) return this.cache.getTimezone(id);
        URL resource = ResourceLoader.getResource(this.resourcePrefix + id + ".ics");
        if (resource == null) return TimeZoneLoader.generateTimezoneForId(id);
        try (InputStream in = resource.openStream();){
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(in);
            VTimeZone vTimeZone = (VTimeZone)calendar.getComponent("VTIMEZONE");
            if (!"false".equals(Configurator.getProperty(UPDATE_ENABLED).orElse("true"))) {
                VTimeZone vTimeZone2 = this.updateDefinition(vTimeZone);
                return vTimeZone2;
            }
            if (vTimeZone == null) return this.cache.getTimezone(id);
            this.cache.putIfAbsent(id, vTimeZone);
            return this.cache.getTimezone(id);
        }
    }

    private VTimeZone updateDefinition(VTimeZone vTimeZone) throws IOException, ParserException {
        TzUrl tzUrl = vTimeZone.getTimeZoneUrl();
        if (tzUrl != null) {
            int connectTimeout = Configurator.getIntProperty(UPDATE_CONNECT_TIMEOUT).orElse(0);
            int readTimeout = Configurator.getIntProperty(UPDATE_READ_TIMEOUT).orElse(0);
            URL url = tzUrl.getUri().toURL();
            URLConnection connection = "true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false")) && proxy != null ? url.openConnection(proxy) : url.openConnection();
            connection.setConnectTimeout(connectTimeout);
            connection.setReadTimeout(readTimeout);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(connection.getInputStream());
            VTimeZone updatedVTimeZone = (VTimeZone)calendar.getComponent("VTIMEZONE");
            if (updatedVTimeZone != null) {
                return updatedVTimeZone;
            }
        }
        return vTimeZone;
    }

    private static VTimeZone generateTimezoneForId(String timezoneId) throws ParseException {
        if (!TIMEZONE_DEFINITIONS.contains(timezoneId)) {
            return null;
        }
        java.util.TimeZone javaTz = java.util.TimeZone.getTimeZone(timezoneId);
        ZoneId zoneId = ZoneId.of(javaTz.getID(), ZoneId.SHORT_IDS);
        int rawTimeZoneOffsetInSeconds = javaTz.getRawOffset() / 1000;
        VTimeZone timezone = new VTimeZone();
        timezone.getProperties().add(new TzId(timezoneId));
        TimeZoneLoader.addTransitions(zoneId, timezone, rawTimeZoneOffsetInSeconds);
        TimeZoneLoader.addTransitionRules(zoneId, rawTimeZoneOffsetInSeconds, timezone);
        if (timezone.getObservances() == null || timezone.getObservances().isEmpty()) {
            timezone.getObservances().add(NO_TRANSITIONS);
        }
        return timezone;
    }

    private static void addTransitionRules(ZoneId zoneId, int rawTimeZoneOffsetInSeconds, VTimeZone result) {
        ZoneOffsetTransition zoneOffsetTransition = null;
        if (!zoneId.getRules().getTransitions().isEmpty()) {
            Collections.min(zoneId.getRules().getTransitions(), Comparator.comparing(ZoneOffsetTransition::getDateTimeBefore));
        }
        LocalDateTime startDate = null;
        startDate = zoneOffsetTransition != null ? zoneOffsetTransition.getDateTimeBefore() : LocalDateTime.now(zoneId);
        for (ZoneOffsetTransitionRule transitionRule : zoneId.getRules().getTransitionRules()) {
            int transitionRuleMonthValue = transitionRule.getMonth().getValue();
            DayOfWeek transitionRuleDayOfWeek = transitionRule.getDayOfWeek();
            LocalDateTime ldt = LocalDateTime.now(zoneId).with(TemporalAdjusters.firstInMonth(transitionRuleDayOfWeek)).withMonth(transitionRuleMonthValue).with(transitionRule.getLocalTime());
            Month month = ldt.getMonth();
            TreeSet<Integer> allDaysOfWeek = new TreeSet<Integer>();
            do {
                allDaysOfWeek.add(ldt.getDayOfMonth());
            } while ((ldt = ldt.plus(Period.ofWeeks(1))).getMonth() == month);
            Integer dayOfMonth = allDaysOfWeek.ceiling(transitionRule.getDayOfMonthIndicator());
            if (dayOfMonth == null) {
                dayOfMonth = (Integer)allDaysOfWeek.last();
            }
            int weekdayIndexInMonth = 0;
            Iterator it = allDaysOfWeek.iterator();
            while (it.hasNext() && it.next() != dayOfMonth) {
                ++weekdayIndexInMonth;
            }
            weekdayIndexInMonth = weekdayIndexInMonth >= 3 ? weekdayIndexInMonth - allDaysOfWeek.size() : weekdayIndexInMonth;
            String rruleText = String.format(RRULE_TPL, transitionRuleMonthValue, weekdayIndexInMonth, transitionRuleDayOfWeek.name().substring(0, 2));
            try {
                TzOffsetFrom offsetFrom = new TzOffsetFrom(transitionRule.getOffsetBefore());
                TzOffsetTo offsetTo = new TzOffsetTo(transitionRule.getOffsetAfter());
                RRule rrule = new RRule(rruleText);
                Observance observance = transitionRule.getOffsetAfter().getTotalSeconds() > rawTimeZoneOffsetInSeconds ? new Daylight() : new Standard();
                observance.getProperties().add(offsetFrom);
                observance.getProperties().add(offsetTo);
                observance.getProperties().add(rrule);
                observance.getProperties().add(new DtStart(startDate.withMonth(transitionRule.getMonth().getValue()).withDayOfMonth(transitionRule.getDayOfMonthIndicator()).with(transitionRule.getDayOfWeek()).format(DateTimeFormatter.ofPattern(DATE_TIME_TPL))));
                result.getObservances().add(observance);
            }
            catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void addTransitions(ZoneId zoneId, VTimeZone result, int rawTimeZoneOffsetInSeconds) throws ParseException {
        HashMap<ZoneOffsetKey, Set> zoneTransitionsByOffsets = new HashMap<ZoneOffsetKey, Set>();
        for (ZoneOffsetTransition zoneOffsetTransition : zoneId.getRules().getTransitions()) {
            ZoneOffsetKey offfsetKey = ZoneOffsetKey.of(zoneOffsetTransition.getOffsetBefore(), zoneOffsetTransition.getOffsetAfter());
            Set transitionRulesForOffset = zoneTransitionsByOffsets.computeIfAbsent(offfsetKey, k -> new HashSet(1));
            transitionRulesForOffset.add(zoneOffsetTransition);
        }
        for (Map.Entry entry : zoneTransitionsByOffsets.entrySet()) {
            Observance observance = ((ZoneOffsetKey)entry.getKey()).offsetAfter.getTotalSeconds() > rawTimeZoneOffsetInSeconds ? new Daylight() : new Standard();
            LocalDateTime start = ((ZoneOffsetTransition)Collections.min((Collection)entry.getValue())).getDateTimeBefore();
            DtStart dtStart = new DtStart(start.format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
            TzOffsetFrom offsetFrom = new TzOffsetFrom(((ZoneOffsetKey)entry.getKey()).offsetBefore);
            TzOffsetTo offsetTo = new TzOffsetTo(((ZoneOffsetKey)entry.getKey()).offsetAfter);
            observance.getProperties().add(dtStart);
            observance.getProperties().add(offsetFrom);
            observance.getProperties().add(offsetTo);
            for (ZoneOffsetTransition transition : (Set)entry.getValue()) {
                RDate rDate = new RDate(new ParameterList(), transition.getDateTimeBefore().format(DateTimeFormatter.ofPattern(DATE_TIME_TPL)));
                observance.getProperties().add(rDate);
            }
            result.getObservances().add(observance);
        }
    }

    private static TimeZoneCache cacheInit() {
        Optional<TimeZoneCache> property = Configurator.getObjectProperty(TZ_CACHE_IMPL);
        return property.orElseGet(() -> {
            try {
                return (TimeZoneCache)Class.forName(DEFAULT_TZ_CACHE_IMPL).newInstance();
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoClassDefFoundError e) {
                throw new RuntimeException(MESSAGE_MISSING_DEFAULT_TZ_CACHE_IMPL, e);
            }
        });
    }

    static {
        TIMEZONE_DEFINITIONS.addAll(Arrays.asList(TimeZone.getAvailableIDs()));
        NO_TRANSITIONS = new Standard();
        TzOffsetFrom offsetFrom = new TzOffsetFrom(ZoneOffset.UTC);
        TzOffsetTo offsetTo = new TzOffsetTo(ZoneOffset.UTC);
        NO_TRANSITIONS.getProperties().add(offsetFrom);
        NO_TRANSITIONS.getProperties().add(offsetTo);
        DtStart start = new DtStart();
        start.setDate(new DateTime(0L));
        NO_TRANSITIONS.getProperties().add(start);
        try {
            if ("true".equals(Configurator.getProperty(UPDATE_PROXY_ENABLED).orElse("false"))) {
                Proxy.Type type = Configurator.getEnumProperty(Proxy.Type.class, UPDATE_PROXY_TYPE).orElse(Proxy.Type.DIRECT);
                String proxyHost = Configurator.getProperty(UPDATE_PROXY_HOST).orElse("");
                int proxyPort = Configurator.getIntProperty(UPDATE_PROXY_PORT).orElse(-1);
                proxy = new Proxy(type, new InetSocketAddress(proxyHost, proxyPort));
            }
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(TimeZoneLoader.class).warn("Error loading proxy server configuration: " + e.getMessage());
        }
    }

    private static class ZoneOffsetKey {
        private final ZoneOffset offsetBefore;
        private final ZoneOffset offsetAfter;

        private ZoneOffsetKey(ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            this.offsetBefore = offsetBefore;
            this.offsetAfter = offsetAfter;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return true;
            }
            if (!(obj instanceof ZoneOffsetKey)) {
                return false;
            }
            ZoneOffsetKey otherZoneOffsetKey = (ZoneOffsetKey)obj;
            return Objects.equals(this.offsetBefore, otherZoneOffsetKey.offsetBefore) && Objects.equals(this.offsetAfter, otherZoneOffsetKey.offsetAfter);
        }

        public int hashCode() {
            int result = 31;
            result *= this.offsetBefore == null ? 1 : this.offsetBefore.hashCode();
            return result *= this.offsetAfter == null ? 1 : this.offsetAfter.hashCode();
        }

        static ZoneOffsetKey of(ZoneOffset offsetBefore, ZoneOffset offsetAfter) {
            return new ZoneOffsetKey(offsetBefore, offsetAfter);
        }
    }
}

