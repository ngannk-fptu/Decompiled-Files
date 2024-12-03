/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.ValueParser;

public class ComparisonDateTool
extends DateTool {
    public static final long MILLIS_PER_SECOND = 1000L;
    public static final long MILLIS_PER_MINUTE = 60000L;
    public static final long MILLIS_PER_HOUR = 3600000L;
    public static final long MILLIS_PER_DAY = 86400000L;
    public static final long MILLIS_PER_WEEK = 604800000L;
    public static final long MILLIS_PER_MONTH = 2592000000L;
    public static final long MILLIS_PER_YEAR = 31536000000L;
    public static final String BUNDLE_NAME_KEY = "bundle";
    public static final String DEPTH_KEY = "depth";
    public static final String SKIPPED_UNITS_KEY = "skip";
    public static final String DEFAULT_BUNDLE_NAME = "org.apache.velocity.tools.generic.times";
    protected static final String MILLISECOND_KEY = "millisecond";
    protected static final String SECOND_KEY = "second";
    protected static final String MINUTE_KEY = "minute";
    protected static final String HOUR_KEY = "hour";
    protected static final String DAY_KEY = "day";
    protected static final String WEEK_KEY = "week";
    protected static final String MONTH_KEY = "month";
    protected static final String YEAR_KEY = "year";
    protected static final Map TIME_UNITS;
    protected static final String CURRENT_PREFIX = "current.";
    protected static final String AFTER_KEY = "after";
    protected static final String BEFORE_KEY = "before";
    protected static final String EQUAL_KEY = "equal";
    protected static final String ZERO_KEY = "zero";
    protected static final String ABBR_SUFFIX = ".abbr";
    protected static final String ONE_DAY_SUFFIX = ".day";
    protected static final String PLURAL_SUFFIX = "s";
    protected static final int CURRENT_TYPE = 0;
    protected static final int RELATIVE_TYPE = 1;
    protected static final int DIFF_TYPE = 2;
    private String bundleName = "org.apache.velocity.tools.generic.times";
    private ResourceBundle defaultBundle;
    private Map timeUnits = TIME_UNITS;
    private int depth = 1;

    @Override
    protected void configure(ValueParser values) {
        super.configure(values);
        String bundle = values.getString(BUNDLE_NAME_KEY);
        if (bundle != null) {
            this.bundleName = bundle;
        }
        this.depth = values.getInt(DEPTH_KEY, 1);
        String[] skip = values.getStrings(SKIPPED_UNITS_KEY);
        if (skip != null) {
            this.timeUnits = new LinkedHashMap(TIME_UNITS);
            for (int i = 0; i < skip.length; ++i) {
                this.timeUnits.remove(skip[i]);
            }
        }
    }

    protected String getText(String key, Locale locale) {
        Locale defaultLocale = this.getLocale();
        ResourceBundle bundle = null;
        if (locale == null || locale.equals(defaultLocale)) {
            if (this.defaultBundle == null) {
                try {
                    this.defaultBundle = ResourceBundle.getBundle(this.bundleName, defaultLocale);
                }
                catch (MissingResourceException missingResourceException) {
                    // empty catch block
                }
            }
            bundle = this.defaultBundle;
        } else {
            try {
                bundle = ResourceBundle.getBundle(this.bundleName, locale);
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
        }
        if (bundle != null) {
            try {
                return bundle.getString(key);
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
        }
        return "???" + key + "???";
    }

    public static long toYears(long ms) {
        return ms / 31536000000L;
    }

    public static long toMonths(long ms) {
        return ms / 2592000000L;
    }

    public static long toWeeks(long ms) {
        return ms / 604800000L;
    }

    public static long toDays(long ms) {
        return ms / 86400000L;
    }

    public static long toHours(long ms) {
        return ms / 3600000L;
    }

    public static long toMinutes(long ms) {
        return ms / 60000L;
    }

    public static long toSeconds(long ms) {
        return ms / 1000L;
    }

    public Comparison whenIs(Object then) {
        return this.compare(this.getCalendar(), then, 0);
    }

    public Comparison whenIs(Object now, Object then) {
        return this.compare(now, then, 1);
    }

    public Comparison difference(Object now, Object then) {
        return this.compare(now, then, 2);
    }

    protected Comparison compare(Object now, Object then, int type) {
        Calendar calThen = this.toCalendar(then);
        Calendar calNow = this.toCalendar(now);
        if (calThen == null || calNow == null) {
            return null;
        }
        long ms = calThen.getTimeInMillis() - calNow.getTimeInMillis();
        return new Comparison(ms, type, this.depth, false, null);
    }

    protected String toString(long ms, int type, int depth, boolean abbr, Locale loc) {
        String directionKey;
        if (ms == 0L) {
            String sameKey;
            String string = sameKey = abbr ? ABBR_SUFFIX : "";
            sameKey = type == 0 ? "current.equal" + sameKey : (type == 1 ? EQUAL_KEY + sameKey : ZERO_KEY + sameKey);
            return this.getText(sameKey, loc);
        }
        boolean isBefore = false;
        if (ms < 0L) {
            isBefore = true;
            ms *= -1L;
        }
        String friendly = this.toString(ms, depth, abbr, loc);
        if (type == 2) {
            if (isBefore) {
                friendly = "-" + friendly;
            }
            return friendly;
        }
        String string = directionKey = isBefore ? BEFORE_KEY : AFTER_KEY;
        if (type == 0) {
            directionKey = CURRENT_PREFIX + directionKey;
            if (friendly != null && friendly.startsWith("1")) {
                String dayKey;
                String string2 = dayKey = abbr ? "day.abbr" : DAY_KEY;
                if (friendly.equals("1 " + this.getText(dayKey, loc))) {
                    directionKey = directionKey + ONE_DAY_SUFFIX;
                    return this.getText(directionKey, loc);
                }
            }
        }
        if (abbr) {
            directionKey = directionKey + ABBR_SUFFIX;
        }
        return friendly + " " + this.getText(directionKey, loc);
    }

    protected String toString(long diff, int maxUnitDepth, boolean abbreviate, Locale locale) {
        if (diff <= 0L) {
            return null;
        }
        if (maxUnitDepth > this.timeUnits.size()) {
            maxUnitDepth = this.timeUnits.size();
        }
        long value = 0L;
        long remainder = 0L;
        Iterator i = this.timeUnits.keySet().iterator();
        String unitKey = (String)i.next();
        Long unit = (Long)this.timeUnits.get(unitKey);
        while (i.hasNext()) {
            String nextUnitKey = (String)i.next();
            Long nextUnit = (Long)this.timeUnits.get(nextUnitKey);
            if (diff < nextUnit) {
                value = diff / unit;
                remainder = diff - value * unit;
                break;
            }
            unitKey = nextUnitKey;
            unit = nextUnit;
        }
        if (unitKey.equals(YEAR_KEY)) {
            value = diff / unit;
            remainder = diff - value * unit;
        }
        if (value != 1L) {
            unitKey = unitKey + PLURAL_SUFFIX;
        }
        if (abbreviate) {
            unitKey = unitKey + ABBR_SUFFIX;
        }
        String output = value + " " + this.getText(unitKey, locale);
        if (maxUnitDepth > 1 && remainder > 0L) {
            output = output + " " + this.toString(remainder, maxUnitDepth - 1, abbreviate, locale);
        }
        return output;
    }

    static {
        LinkedHashMap<String, Long> units = new LinkedHashMap<String, Long>(8);
        units.put(MILLISECOND_KEY, 1L);
        units.put(SECOND_KEY, 1000L);
        units.put(MINUTE_KEY, 60000L);
        units.put(HOUR_KEY, 3600000L);
        units.put(DAY_KEY, 86400000L);
        units.put(WEEK_KEY, 604800000L);
        units.put(MONTH_KEY, 2592000000L);
        units.put(YEAR_KEY, 31536000000L);
        TIME_UNITS = Collections.unmodifiableMap(units);
    }

    public class Comparison {
        private final long milliseconds;
        private final int type;
        private final int maxUnitDepth;
        private final boolean abbreviate;
        private final Locale locale;

        public Comparison(long ms, int type, int depth, boolean abbr, Locale loc) {
            this.milliseconds = ms;
            this.type = type;
            this.maxUnitDepth = depth;
            this.abbreviate = abbr;
            this.locale = loc;
        }

        public Comparison abbr(boolean abbr) {
            return new Comparison(this.milliseconds, this.type, this.maxUnitDepth, abbr, this.locale);
        }

        public Comparison depth(int depth) {
            return new Comparison(this.milliseconds, this.type, depth, this.abbreviate, this.locale);
        }

        public Comparison locale(Locale loc) {
            return new Comparison(this.milliseconds, this.type, this.maxUnitDepth, this.abbreviate, loc);
        }

        public long getYears() {
            return ComparisonDateTool.toYears(this.milliseconds);
        }

        public long getMonths() {
            return ComparisonDateTool.toMonths(this.milliseconds);
        }

        public long getWeeks() {
            return ComparisonDateTool.toWeeks(this.milliseconds);
        }

        public long getDays() {
            return ComparisonDateTool.toDays(this.milliseconds);
        }

        public long getHours() {
            return ComparisonDateTool.toHours(this.milliseconds);
        }

        public long getMinutes() {
            return ComparisonDateTool.toMinutes(this.milliseconds);
        }

        public long getSeconds() {
            return ComparisonDateTool.toSeconds(this.milliseconds);
        }

        public long getMilliseconds() {
            return this.milliseconds;
        }

        public Comparison getFull() {
            return this.depth(ComparisonDateTool.this.timeUnits.size());
        }

        public Comparison getDifference() {
            return new Comparison(this.milliseconds, 2, this.maxUnitDepth, this.abbreviate, this.locale);
        }

        public Comparison getRelative() {
            return new Comparison(this.milliseconds, 1, this.maxUnitDepth, this.abbreviate, this.locale);
        }

        public Comparison getAbbr() {
            return this.abbr(true);
        }

        public String toString() {
            return ComparisonDateTool.this.toString(this.milliseconds, this.type, this.maxUnitDepth, this.abbreviate, this.locale);
        }
    }
}

