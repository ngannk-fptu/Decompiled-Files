/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.asm;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;

public class ConvertDate {
    static TreeMap<String, Integer> monthsTable = new TreeMap(new StringCmpNS());
    static TreeMap<String, Integer> daysTable = new TreeMap(new StringCmpNS());
    private static HashSet<String> voidData = new HashSet();
    public static TimeZone defaultTimeZone;
    static TreeMap<String, TimeZone> timeZoneMapping;

    public static Integer getMonth(String month) {
        return monthsTable.get(month);
    }

    private static Integer parseMonth(String s1) {
        if (Character.isDigit(s1.charAt(0))) {
            return Integer.parseInt(s1) - 1;
        }
        Integer month = monthsTable.get(s1);
        if (month == null) {
            throw new NullPointerException("can not parse " + s1 + " as month");
        }
        return (int)month;
    }

    private static GregorianCalendar newCalandar() {
        TimeZone TZ;
        GregorianCalendar cal = new GregorianCalendar(2000, 0, 0, 0, 0, 0);
        if (defaultTimeZone != null) {
            cal.setTimeZone(defaultTimeZone);
        }
        if ((TZ = cal.getTimeZone()) == null) {
            TZ = TimeZone.getDefault();
        }
        cal.setTimeInMillis(-TZ.getRawOffset());
        return cal;
    }

    private static void fillMap(TreeMap<String, Integer> map, String key, Integer value) {
        map.put(key, value);
        key = key.replace("\u00e9", "e");
        key = key.replace("\u00fb", "u");
        map.put(key, value);
    }

    public static Date convertToDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date)obj;
        }
        if (obj instanceof Number) {
            return new Date(((Number)obj).longValue());
        }
        if (obj instanceof String) {
            obj = ((String)obj).replace("p.m.", "pm").replace("a.m.", "am");
            StringTokenizer st = new StringTokenizer((String)obj, " -/:,.+\u5e74\u6708\u65e5\u66dc\u6642\u5206\u79d2");
            String s1 = "";
            if (!st.hasMoreTokens()) {
                return null;
            }
            s1 = st.nextToken();
            if (s1.length() == 4 && Character.isDigit(s1.charAt(0))) {
                return ConvertDate.getYYYYMMDD(st, s1);
            }
            if (daysTable.containsKey(s1)) {
                if (!st.hasMoreTokens()) {
                    return null;
                }
                s1 = st.nextToken();
            }
            if (monthsTable.containsKey(s1)) {
                return ConvertDate.getMMDDYYYY(st, s1);
            }
            if (Character.isDigit(s1.charAt(0))) {
                return ConvertDate.getDDMMYYYY(st, s1);
            }
            return null;
        }
        throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to int");
    }

    private static Date getYYYYMMDD(StringTokenizer st, String s1) {
        GregorianCalendar cal = ConvertDate.newCalandar();
        int year = Integer.parseInt(s1);
        cal.set(1, year);
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        cal.set(2, ConvertDate.parseMonth(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        if (Character.isDigit(s1.charAt(0))) {
            if (s1.length() == 5 && s1.charAt(2) == 'T') {
                int day = Integer.parseInt(s1.substring(0, 2));
                cal.set(5, day);
                return ConvertDate.addHour(st, cal, s1.substring(3));
            }
            int day = Integer.parseInt(s1);
            cal.set(5, day);
            return ConvertDate.addHour(st, cal, null);
        }
        return cal.getTime();
    }

    private static int getYear(String s1) {
        int year = Integer.parseInt(s1);
        if (year < 100) {
            year = year > 30 ? (year += 2000) : (year += 1900);
        }
        return year;
    }

    private static Date getMMDDYYYY(StringTokenizer st, String s1) {
        GregorianCalendar cal = ConvertDate.newCalandar();
        Integer month = monthsTable.get(s1);
        if (month == null) {
            throw new NullPointerException("can not parse " + s1 + " as month");
        }
        cal.set(2, month);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        int day = Integer.parseInt(s1);
        cal.set(5, day);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        if (Character.isLetter(s1.charAt(0))) {
            if (!st.hasMoreTokens()) {
                return null;
            }
            s1 = st.nextToken();
        }
        if (s1.length() == 4) {
            cal.set(1, ConvertDate.getYear(s1));
        } else if (s1.length() == 2) {
            return ConvertDate.addHour2(st, cal, s1);
        }
        return ConvertDate.addHour(st, cal, null);
    }

    private static Date getDDMMYYYY(StringTokenizer st, String s1) {
        GregorianCalendar cal = ConvertDate.newCalandar();
        int day = Integer.parseInt(s1);
        cal.set(5, day);
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        cal.set(2, ConvertDate.parseMonth(s1));
        if (!st.hasMoreTokens()) {
            return null;
        }
        s1 = st.nextToken();
        cal.set(1, ConvertDate.getYear(s1));
        return ConvertDate.addHour(st, cal, null);
    }

    private static Date addHour(StringTokenizer st, Calendar cal, String s1) {
        if (s1 == null) {
            if (!st.hasMoreTokens()) {
                return cal.getTime();
            }
            s1 = st.nextToken();
        }
        return ConvertDate.addHour2(st, cal, s1);
    }

    private static Date addHour2(StringTokenizer st, Calendar cal, String s1) {
        s1 = ConvertDate.trySkip(st, s1, cal);
        cal.set(11, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        if ((s1 = ConvertDate.trySkip(st, s1, cal)) == null) {
            return cal.getTime();
        }
        cal.set(12, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        if ((s1 = ConvertDate.trySkip(st, s1, cal)) == null) {
            return cal.getTime();
        }
        cal.set(13, Integer.parseInt(s1));
        if (!st.hasMoreTokens()) {
            return cal.getTime();
        }
        s1 = st.nextToken();
        if ((s1 = ConvertDate.trySkip(st, s1, cal)) == null) {
            return cal.getTime();
        }
        if ((s1 = ConvertDate.trySkip(st, s1, cal)).length() == 4 && Character.isDigit(s1.charAt(0))) {
            cal.set(1, ConvertDate.getYear(s1));
        }
        return cal.getTime();
    }

    private static String trySkip(StringTokenizer st, String s1, Calendar cal) {
        while (true) {
            TimeZone tz;
            if ((tz = timeZoneMapping.get(s1)) != null) {
                cal.setTimeZone(tz);
                if (!st.hasMoreTokens()) {
                    return null;
                }
                s1 = st.nextToken();
                continue;
            }
            if (!voidData.contains(s1)) break;
            if (s1.equalsIgnoreCase("pm")) {
                cal.add(9, 1);
            }
            if (s1.equalsIgnoreCase("am")) {
                cal.add(9, 0);
            }
            if (!st.hasMoreTokens()) {
                return null;
            }
            s1 = st.nextToken();
        }
        return s1;
    }

    static {
        timeZoneMapping = new TreeMap();
        voidData.add("\u00e0");
        voidData.add("at");
        voidData.add("MEZ");
        voidData.add("Uhr");
        voidData.add("h");
        voidData.add("pm");
        voidData.add("PM");
        voidData.add("am");
        voidData.add("AM");
        voidData.add("min");
        voidData.add("um");
        voidData.add("o'clock");
        for (String tz : TimeZone.getAvailableIDs()) {
            timeZoneMapping.put(tz, TimeZone.getTimeZone(tz));
        }
        for (Locale locale : DateFormatSymbols.getAvailableLocales()) {
            String s;
            int i;
            if ("ja".equals(locale.getLanguage()) || "ko".equals(locale.getLanguage()) || "zh".equals(locale.getLanguage())) continue;
            DateFormatSymbols dfs = DateFormatSymbols.getInstance(locale);
            String[] keys = dfs.getMonths();
            for (i = 0; i < keys.length; ++i) {
                if (keys[i].length() == 0) continue;
                ConvertDate.fillMap(monthsTable, keys[i], i);
            }
            keys = dfs.getShortMonths();
            for (i = 0; i < keys.length; ++i) {
                s = keys[i];
                if (s.length() == 0 || Character.isDigit(s.charAt(s.length() - 1))) continue;
                ConvertDate.fillMap(monthsTable, keys[i], i);
                ConvertDate.fillMap(monthsTable, keys[i].replace(".", ""), i);
            }
            keys = dfs.getWeekdays();
            for (i = 0; i < keys.length; ++i) {
                s = keys[i];
                if (s.length() == 0) continue;
                ConvertDate.fillMap(daysTable, s, i);
                ConvertDate.fillMap(daysTable, s.replace(".", ""), i);
            }
            keys = dfs.getShortWeekdays();
            for (i = 0; i < keys.length; ++i) {
                s = keys[i];
                if (s.length() == 0) continue;
                ConvertDate.fillMap(daysTable, s, i);
                ConvertDate.fillMap(daysTable, s.replace(".", ""), i);
            }
        }
    }

    public static class StringCmpNS
    implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareToIgnoreCase(o2);
        }
    }
}

