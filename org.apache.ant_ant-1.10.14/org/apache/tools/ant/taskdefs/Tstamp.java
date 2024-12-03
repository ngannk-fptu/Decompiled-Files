/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class Tstamp
extends Task {
    private static final String ENV_SOURCE_DATE_EPOCH = "SOURCE_DATE_EPOCH";
    private List<CustomFormat> customFormats = new Vector<CustomFormat>();
    private String prefix = "";

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        if (!this.prefix.endsWith(".")) {
            this.prefix = this.prefix + ".";
        }
    }

    @Override
    public void execute() throws BuildException {
        try {
            Date d = this.getNow();
            String epoch = System.getenv(ENV_SOURCE_DATE_EPOCH);
            try {
                if (epoch != null) {
                    d = new Date(Long.parseLong(epoch) * 1000L);
                    this.log("Honouring environment variable SOURCE_DATE_EPOCH which has been set to " + epoch);
                }
            }
            catch (NumberFormatException e) {
                this.log("Ignoring invalid value '" + epoch + "' for " + ENV_SOURCE_DATE_EPOCH + " environment variable", 4);
            }
            Date date = d;
            this.customFormats.forEach(cts -> cts.execute(this.getProject(), date, this.getLocation()));
            SimpleDateFormat dstamp = new SimpleDateFormat("yyyyMMdd");
            this.setProperty("DSTAMP", dstamp.format(d));
            SimpleDateFormat tstamp = new SimpleDateFormat("HHmm");
            this.setProperty("TSTAMP", tstamp.format(d));
            SimpleDateFormat today = new SimpleDateFormat("MMMM d yyyy", Locale.US);
            this.setProperty("TODAY", today.format(d));
        }
        catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public CustomFormat createFormat() {
        CustomFormat cts = new CustomFormat();
        this.customFormats.add(cts);
        return cts;
    }

    private void setProperty(String name, String value) {
        this.getProject().setNewProperty(this.prefix + name, value);
    }

    protected Date getNow() {
        Optional<Date> now = this.getNow("ant.tstamp.now.iso", s -> Date.from(Instant.parse(s)), (k, v) -> "magic property " + k + " ignored as '" + v + "' is not in valid ISO pattern");
        if (now.isPresent()) {
            return now.get();
        }
        now = this.getNow("ant.tstamp.now", s -> new Date(1000L * Long.parseLong(s)), (k, v) -> "magic property " + k + " ignored as " + v + " is not a valid number");
        return now.orElseGet(Date::new);
    }

    protected Optional<Date> getNow(String propertyName, Function<String, Date> map, BiFunction<String, String, String> log) {
        String property = this.getProject().getProperty(propertyName);
        if (property != null && !property.isEmpty()) {
            try {
                return Optional.ofNullable(map.apply(property));
            }
            catch (Exception e) {
                this.log(log.apply(propertyName, property));
            }
        }
        return Optional.empty();
    }

    public class CustomFormat {
        private TimeZone timeZone;
        private String propertyName;
        private String pattern;
        private String language;
        private String country;
        private String variant;
        private int offset = 0;
        private int field = 5;

        public void setProperty(String propertyName) {
            this.propertyName = propertyName;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public void setLocale(String locale) {
            StringTokenizer st = new StringTokenizer(locale, " \t\n\r\f,");
            try {
                this.language = st.nextToken();
                if (st.hasMoreElements()) {
                    this.country = st.nextToken();
                    if (st.hasMoreElements()) {
                        this.variant = st.nextToken();
                        if (st.hasMoreElements()) {
                            throw new BuildException("bad locale format", Tstamp.this.getLocation());
                        }
                    }
                } else {
                    this.country = "";
                }
            }
            catch (NoSuchElementException e) {
                throw new BuildException("bad locale format", e, Tstamp.this.getLocation());
            }
        }

        public void setTimezone(String id) {
            this.timeZone = TimeZone.getTimeZone(id);
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        @Deprecated
        public void setUnit(String unit) {
            Tstamp.this.log("DEPRECATED - The setUnit(String) method has been deprecated. Use setUnit(Tstamp.Unit) instead.");
            Unit u = new Unit();
            u.setValue(unit);
            this.field = u.getCalendarField();
        }

        public void setUnit(Unit unit) {
            this.field = unit.getCalendarField();
        }

        public void execute(Project project, Date date, Location location) {
            if (this.propertyName == null) {
                throw new BuildException("property attribute must be provided", location);
            }
            if (this.pattern == null) {
                throw new BuildException("pattern attribute must be provided", location);
            }
            SimpleDateFormat sdf = this.language == null ? new SimpleDateFormat(this.pattern) : (this.variant == null ? new SimpleDateFormat(this.pattern, new Locale(this.language, this.country)) : new SimpleDateFormat(this.pattern, new Locale(this.language, this.country, this.variant)));
            if (this.offset != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(this.field, this.offset);
                date = calendar.getTime();
            }
            if (this.timeZone != null) {
                sdf.setTimeZone(this.timeZone);
            }
            Tstamp.this.setProperty(this.propertyName, sdf.format(date));
        }
    }

    public static class Unit
    extends EnumeratedAttribute {
        private static final String MILLISECOND = "millisecond";
        private static final String SECOND = "second";
        private static final String MINUTE = "minute";
        private static final String HOUR = "hour";
        private static final String DAY = "day";
        private static final String WEEK = "week";
        private static final String MONTH = "month";
        private static final String YEAR = "year";
        private static final String[] UNITS = new String[]{"millisecond", "second", "minute", "hour", "day", "week", "month", "year"};
        private Map<String, Integer> calendarFields = new HashMap<String, Integer>();

        public Unit() {
            this.calendarFields.put(MILLISECOND, 14);
            this.calendarFields.put(SECOND, 13);
            this.calendarFields.put(MINUTE, 12);
            this.calendarFields.put(HOUR, 11);
            this.calendarFields.put(DAY, 5);
            this.calendarFields.put(WEEK, 3);
            this.calendarFields.put(MONTH, 2);
            this.calendarFields.put(YEAR, 1);
        }

        public int getCalendarField() {
            String key = this.getValue().toLowerCase(Locale.ENGLISH);
            return this.calendarFields.get(key);
        }

        @Override
        public String[] getValues() {
            return UNITS;
        }
    }
}

