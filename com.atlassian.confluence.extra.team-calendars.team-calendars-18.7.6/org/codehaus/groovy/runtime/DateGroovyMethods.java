/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;

public class DateGroovyMethods
extends DefaultGroovyMethodsSupport {
    private static final Map<String, Integer> CAL_MAP = new HashMap<String, Integer>();

    public static int getAt(Date self, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        return cal.get(field);
    }

    public static Calendar toCalendar(Date self) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        return cal;
    }

    public static int getAt(Calendar self, int field) {
        return self.get(field);
    }

    public static void putAt(Calendar self, int field, int value) {
        self.set(field, value);
    }

    public static void putAt(Date self, int field, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        DateGroovyMethods.putAt(cal, field, value);
        self.setTime(cal.getTimeInMillis());
    }

    public static void set(Calendar self, Map<Object, Integer> updates) {
        for (Map.Entry<Object, Integer> entry : updates.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                key = CAL_MAP.get(key);
            }
            if (!(key instanceof Integer)) continue;
            self.set((Integer)key, entry.getValue());
        }
    }

    public static Calendar updated(Calendar self, Map<Object, Integer> updates) {
        Calendar result = (Calendar)self.clone();
        DateGroovyMethods.set(result, updates);
        return result;
    }

    public static Calendar copyWith(Calendar self, Map<Object, Integer> updates) {
        Calendar result = (Calendar)self.clone();
        DateGroovyMethods.set(result, updates);
        return result;
    }

    public static void set(Date self, Map<Object, Integer> updates) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        DateGroovyMethods.set(cal, updates);
        self.setTime(cal.getTimeInMillis());
    }

    public static Date updated(Date self, Map<Object, Integer> updates) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        DateGroovyMethods.set(cal, updates);
        return cal.getTime();
    }

    public static Date copyWith(Date self, Map<Object, Integer> updates) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(self);
        DateGroovyMethods.set(cal, updates);
        return cal.getTime();
    }

    public static Date next(Date self) {
        return DateGroovyMethods.plus(self, 1);
    }

    public static Calendar next(Calendar self) {
        Calendar result = (Calendar)self.clone();
        result.add(5, 1);
        return result;
    }

    public static Calendar previous(Calendar self) {
        Calendar result = (Calendar)self.clone();
        result.add(5, -1);
        return result;
    }

    public static java.sql.Date next(java.sql.Date self) {
        return new java.sql.Date(DateGroovyMethods.next((Date)self).getTime());
    }

    public static Date previous(Date self) {
        return DateGroovyMethods.minus(self, 1);
    }

    public static java.sql.Date previous(java.sql.Date self) {
        return new java.sql.Date(DateGroovyMethods.previous((Date)self).getTime());
    }

    public static Date plus(Date self, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(self);
        calendar.add(5, days);
        return calendar.getTime();
    }

    public static java.sql.Date plus(java.sql.Date self, int days) {
        return new java.sql.Date(DateGroovyMethods.plus((Date)self, days).getTime());
    }

    public static Timestamp plus(Timestamp self, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(self);
        calendar.add(5, days);
        Timestamp ts = new Timestamp(calendar.getTime().getTime());
        ts.setNanos(self.getNanos());
        return ts;
    }

    public static Date minus(Date self, int days) {
        return DateGroovyMethods.plus(self, -days);
    }

    public static java.sql.Date minus(java.sql.Date self, int days) {
        return new java.sql.Date(DateGroovyMethods.minus((Date)self, days).getTime());
    }

    public static Timestamp minus(Timestamp self, int days) {
        return DateGroovyMethods.plus(self, -days);
    }

    public static int minus(Calendar self, Calendar then) {
        Calendar a = self;
        Calendar b = then;
        boolean swap = a.before(b);
        if (swap) {
            Calendar t = a;
            a = b;
            b = t;
        }
        int days = 0;
        b = (Calendar)b.clone();
        while (a.get(1) > b.get(1)) {
            days += 1 + (b.getActualMaximum(6) - b.get(6));
            b.set(6, 1);
            b.add(1, 1);
        }
        days += a.get(6) - b.get(6);
        if (swap) {
            days = -days;
        }
        return days;
    }

    public static int minus(Date self, Date then) {
        Calendar a = (Calendar)Calendar.getInstance().clone();
        a.setTime(self);
        Calendar b = (Calendar)Calendar.getInstance().clone();
        b.setTime(then);
        return DateGroovyMethods.minus(a, b);
    }

    public static String format(Date self, String format) {
        return new SimpleDateFormat(format).format(self);
    }

    public static String format(Date self, String format, TimeZone tz) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(tz);
        return sdf.format(self);
    }

    public static String getDateString(Date self) {
        return DateFormat.getDateInstance(3).format(self);
    }

    public static String getTimeString(Date self) {
        return DateFormat.getTimeInstance(2).format(self);
    }

    public static String getDateTimeString(Date self) {
        return DateFormat.getDateTimeInstance(3, 2).format(self);
    }

    private static void clearTimeCommon(Calendar self) {
        self.set(11, 0);
        self.clear(12);
        self.clear(13);
        self.clear(14);
    }

    public static Date clearTime(Date self) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(self);
        DateGroovyMethods.clearTimeCommon(calendar);
        self.setTime(calendar.getTime().getTime());
        return self;
    }

    public static java.sql.Date clearTime(java.sql.Date self) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(self);
        DateGroovyMethods.clearTimeCommon(calendar);
        self.setTime(calendar.getTime().getTime());
        return self;
    }

    public static Calendar clearTime(Calendar self) {
        DateGroovyMethods.clearTimeCommon(self);
        return self;
    }

    public static String format(Calendar self, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(self.getTimeZone());
        return sdf.format(self.getTime());
    }

    public static void upto(Date self, Date to, Closure closure) {
        if (self.compareTo(to) <= 0) {
            Date i = (Date)self.clone();
            while (i.compareTo(to) <= 0) {
                closure.call((Object)i);
                i = DateGroovyMethods.next(i);
            }
        } else {
            throw new GroovyRuntimeException("The argument (" + to + ") to upto() cannot be earlier than the value (" + self + ") it's called on.");
        }
    }

    public static void upto(Calendar self, Calendar to, Closure closure) {
        if (self.compareTo(to) <= 0) {
            Calendar i = (Calendar)self.clone();
            while (i.compareTo(to) <= 0) {
                closure.call((Object)i);
                i = DateGroovyMethods.next(i);
            }
        } else {
            throw new GroovyRuntimeException("The argument (" + to + ") to upto() cannot be earlier than the value (" + self + ") it's called on.");
        }
    }

    public static void downto(Date self, Date to, Closure closure) {
        if (self.compareTo(to) >= 0) {
            Date i = (Date)self.clone();
            while (i.compareTo(to) >= 0) {
                closure.call((Object)i);
                i = DateGroovyMethods.previous(i);
            }
        } else {
            throw new GroovyRuntimeException("The argument (" + to + ") to downto() cannot be later than the value (" + self + ") it's called on.");
        }
    }

    public static void downto(Calendar self, Calendar to, Closure closure) {
        if (self.compareTo(to) >= 0) {
            Calendar i = (Calendar)self.clone();
            while (i.compareTo(to) >= 0) {
                closure.call((Object)i);
                i = DateGroovyMethods.previous(i);
            }
        } else {
            throw new GroovyRuntimeException("The argument (" + to + ") to downto() cannot be later than the value (" + self + ") it's called on.");
        }
    }

    static {
        CAL_MAP.put("year", 1);
        CAL_MAP.put("month", 2);
        CAL_MAP.put("date", 5);
        CAL_MAP.put("dayOfMonth", 5);
        CAL_MAP.put("hourOfDay", 11);
        CAL_MAP.put("minute", 12);
        CAL_MAP.put("second", 13);
    }
}

