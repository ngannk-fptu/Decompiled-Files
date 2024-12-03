/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.DatatypeConverter
 */
package org.apache.xmpbox;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;

public final class DateConverter {
    private static final SimpleDateFormat[] POTENTIAL_FORMATS = new SimpleDateFormat[]{new SimpleDateFormat("EEEE, dd MMM yyyy hh:mm:ss a"), new SimpleDateFormat("EEEE, MMM dd, yyyy hh:mm:ss a"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S")};

    private DateConverter() {
    }

    public static Calendar toCalendar(String date) throws IOException {
        GregorianCalendar retval;
        block25: {
            retval = null;
            if (date != null && date.trim().length() > 0) {
                date = date.trim();
                int month = 1;
                int day = 1;
                int hour = 0;
                int minute = 0;
                int second = 0;
                try {
                    SimpleTimeZone zone = null;
                    if (Pattern.matches("^\\d{4}-\\d{2}-\\d{2}T.*", date)) {
                        return DateConverter.fromISO8601(date);
                    }
                    if (date.startsWith("D:")) {
                        date = date.substring(2);
                    }
                    if ((date = date.replaceAll("[-:T]", "")).length() < 4) {
                        throw new IOException("Error: Invalid date format '" + date + "'");
                    }
                    int year = Integer.parseInt(date.substring(0, 4));
                    if (date.length() >= 6) {
                        month = Integer.parseInt(date.substring(4, 6));
                    }
                    if (date.length() >= 8) {
                        day = Integer.parseInt(date.substring(6, 8));
                    }
                    if (date.length() >= 10) {
                        hour = Integer.parseInt(date.substring(8, 10));
                    }
                    if (date.length() >= 12) {
                        minute = Integer.parseInt(date.substring(10, 12));
                    }
                    int timeZonePos = 12;
                    if (date.length() - 12 > 5 || date.length() - 12 == 3 && date.endsWith("Z")) {
                        second = Integer.parseInt(date.substring(12, 14));
                        timeZonePos = 14;
                    }
                    if (date.length() >= timeZonePos + 1) {
                        char sign = date.charAt(timeZonePos);
                        if (sign == 'Z') {
                            zone = new SimpleTimeZone(0, "Unknown");
                        } else {
                            int hours = 0;
                            int minutes = 0;
                            if (date.length() >= timeZonePos + 3) {
                                hours = sign == '+' ? Integer.parseInt(date.substring(timeZonePos + 1, timeZonePos + 3)) : -Integer.parseInt(date.substring(timeZonePos, timeZonePos + 2));
                            }
                            if (sign == '+') {
                                if (date.length() >= timeZonePos + 5) {
                                    minutes = Integer.parseInt(date.substring(timeZonePos + 3, timeZonePos + 5));
                                }
                            } else if (date.length() >= timeZonePos + 4) {
                                minutes = Integer.parseInt(date.substring(timeZonePos + 2, timeZonePos + 4));
                            }
                            zone = new SimpleTimeZone(hours * 60 * 60 * 1000 + minutes * 60 * 1000, "Unknown");
                        }
                    }
                    if (zone == null) {
                        retval = new GregorianCalendar();
                    } else {
                        DateConverter.updateZoneId(zone);
                        retval = new GregorianCalendar(zone);
                    }
                    retval.clear();
                    retval.set(year, month - 1, day, hour, minute, second);
                }
                catch (NumberFormatException e) {
                    if (date.charAt(date.length() - 3) == ':' && (date.charAt(date.length() - 6) == '+' || date.charAt(date.length() - 6) == '-')) {
                        date = date.substring(0, date.length() - 3) + date.substring(date.length() - 2);
                    }
                    for (int i = 0; retval == null && i < POTENTIAL_FORMATS.length; ++i) {
                        try {
                            Date utilDate = POTENTIAL_FORMATS[i].parse(date);
                            retval = new GregorianCalendar();
                            retval.setTime(utilDate);
                            continue;
                        }
                        catch (ParseException parseException) {
                            // empty catch block
                        }
                    }
                    if (retval != null) break block25;
                    throw new IOException("Error converting date:" + date, e);
                }
            }
        }
        return retval;
    }

    private static void updateZoneId(TimeZone tz) {
        int offset = tz.getRawOffset();
        int pm = 43;
        if (offset < 0) {
            pm = 45;
            offset = -offset;
        }
        int hh = offset / 3600000;
        int mm = offset % 3600000 / 60000;
        if (offset == 0) {
            tz.setID("GMT");
        } else if (pm == 43 && hh <= 12) {
            tz.setID(String.format(Locale.US, "GMT+%02d:%02d", hh, mm));
        } else if (pm == 45 && hh <= 14) {
            tz.setID(String.format(Locale.US, "GMT-%02d:%02d", hh, mm));
        } else {
            tz.setID("unknown");
        }
    }

    public static String toISO8601(Calendar cal) {
        return DateConverter.toISO8601(cal, false);
    }

    public static String toISO8601(Calendar cal, boolean printMillis) {
        int timeZone;
        StringBuilder retval = new StringBuilder();
        retval.append(cal.get(1));
        retval.append('-');
        retval.append(String.format(Locale.US, "%02d", cal.get(2) + 1));
        retval.append('-');
        retval.append(String.format(Locale.US, "%02d", cal.get(5)));
        retval.append('T');
        retval.append(String.format(Locale.US, "%02d", cal.get(11)));
        retval.append(':');
        retval.append(String.format(Locale.US, "%02d", cal.get(12)));
        retval.append(':');
        retval.append(String.format(Locale.US, "%02d", cal.get(13)));
        if (printMillis) {
            retval.append('.');
            retval.append(String.format(Locale.US, "%03d", cal.get(14)));
        }
        if ((timeZone = cal.get(15) + cal.get(16)) < 0) {
            retval.append('-');
        } else {
            retval.append('+');
        }
        timeZone = Math.abs(timeZone);
        int hours = timeZone / 1000 / 60 / 60;
        int minutes = (timeZone - hours * 1000 * 60 * 60) / 1000 / 60;
        if (hours < 10) {
            retval.append('0');
        }
        retval.append(hours);
        retval.append(':');
        if (minutes < 10) {
            retval.append('0');
        }
        retval.append(minutes);
        return retval.toString();
    }

    private static Calendar fromISO8601(String dateString) {
        int teeIndex;
        Pattern timeZonePattern = Pattern.compile("[\\d-]*T?[\\d-\\.]([A-Z]{1,4})$|(.*\\d*)([A-Z][a-z]+\\/[A-Z][a-z]+)$");
        Matcher timeZoneMatcher = timeZonePattern.matcher(dateString);
        String timeZoneString = null;
        while (timeZoneMatcher.find()) {
            for (int i = 1; i <= timeZoneMatcher.groupCount(); ++i) {
                String group = timeZoneMatcher.group(i);
                if (group == null) continue;
                timeZoneString = group;
            }
        }
        if (timeZoneString != null) {
            teeIndex = dateString.indexOf(84);
            int tzIndex = dateString.indexOf(timeZoneString);
            String toParse = dateString.substring(0, tzIndex);
            if (tzIndex - teeIndex == 6) {
                toParse = dateString.substring(0, tzIndex) + ":00";
            }
            Calendar cal = DatatypeConverter.parseDateTime((String)toParse);
            TimeZone z = TimeZone.getTimeZone(timeZoneString);
            cal.setTimeZone(z);
            return cal;
        }
        teeIndex = dateString.indexOf(84);
        if (teeIndex == -1) {
            return DatatypeConverter.parseDateTime((String)dateString);
        }
        int plusIndex = dateString.indexOf(43, teeIndex + 1);
        int minusIndex = dateString.indexOf(45, teeIndex + 1);
        if (plusIndex == -1 && minusIndex == -1) {
            return DatatypeConverter.parseDateTime((String)dateString);
        }
        if ((plusIndex = Math.max(plusIndex, minusIndex)) - teeIndex == 6) {
            String toParse = dateString.substring(0, plusIndex) + ":00" + dateString.substring(plusIndex);
            return DatatypeConverter.parseDateTime((String)toParse);
        }
        return DatatypeConverter.parseDateTime((String)dateString);
    }
}

