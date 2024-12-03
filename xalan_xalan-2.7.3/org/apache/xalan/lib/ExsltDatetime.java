/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.lib;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;

public class ExsltDatetime {
    static final String dt = "yyyy-MM-dd'T'HH:mm:ss";
    static final String d = "yyyy-MM-dd";
    static final String gym = "yyyy-MM";
    static final String gy = "yyyy";
    static final String gmd = "--MM-dd";
    static final String gm = "--MM--";
    static final String gd = "---dd";
    static final String t = "HH:mm:ss";
    static final String EMPTY_STR = "";

    public static String dateTime() {
        String resultStr = EMPTY_STR;
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            XMLGregorianCalendar xmlGcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            xmlGcal.setMillisecond(Integer.MIN_VALUE);
            resultStr = xmlGcal.toXMLFormat();
        }
        catch (DatatypeConfigurationException datatypeConfigurationException) {
            // empty catch block
        }
        return resultStr;
    }

    private static String formatDigits(int q) {
        String dd = String.valueOf(Math.abs(q));
        return dd.length() == 1 ? '0' + dd : dd;
    }

    public static String date(String datetimeIn) throws ParseException {
        if (EMPTY_STR.equals(datetimeIn)) {
            return EMPTY_STR;
        }
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String leader = edz[0];
        String datetime = edz[1];
        String zone = edz[2];
        if (datetime == null || zone == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d};
        String formatOut = d;
        Date date = ExsltDatetime.testFormats(datetime, formatsIn);
        if (date == null) {
            return EMPTY_STR;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatOut);
        dateFormat.setLenient(false);
        String dateOut = dateFormat.format(date);
        if (dateOut.length() == 0) {
            return EMPTY_STR;
        }
        return leader + dateOut + zone;
    }

    public static String date() {
        String datetime = ExsltDatetime.dateTime();
        String date = datetime.substring(0, datetime.indexOf("T"));
        String zone = datetime.substring(ExsltDatetime.getZoneStart(datetime));
        return date + zone;
    }

    public static String time(String timeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(timeIn);
        String time = edz[1];
        String zone = edz[2];
        if (time == null || zone == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d, t};
        String formatOut = t;
        Date date = ExsltDatetime.testFormats(time, formatsIn);
        if (date == null) {
            return EMPTY_STR;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatOut);
        String out = dateFormat.format(date);
        return out + zone;
    }

    public static String time() {
        String datetime = ExsltDatetime.dateTime();
        String time = datetime.substring(datetime.indexOf("T") + 1);
        return time;
    }

    public static double year(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        boolean ad = edz[0].length() == 0;
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d, gym, gy};
        double yr = ExsltDatetime.getNumber(datetime, formats, 1);
        if (ad || Double.isNaN(yr)) {
            return yr;
        }
        return -yr;
    }

    public static double year() {
        Calendar cal = Calendar.getInstance();
        return cal.get(1);
    }

    public static double monthInYear(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d, gym, gm, gmd};
        return ExsltDatetime.getNumber(datetime, formats, 2) + 1.0;
    }

    public static double monthInYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(2) + 1;
    }

    public static double weekInYear(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d};
        return ExsltDatetime.getNumber(datetime, formats, 3);
    }

    public static double weekInYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(3);
    }

    public static double dayInYear(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d};
        return ExsltDatetime.getNumber(datetime, formats, 6);
    }

    public static double dayInYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(6);
    }

    public static double dayInMonth(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        String[] formats = new String[]{dt, d, gmd, gd};
        double day = ExsltDatetime.getNumber(datetime, formats, 5);
        return day;
    }

    public static double dayInMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(5);
    }

    public static double dayOfWeekInMonth(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d};
        return ExsltDatetime.getNumber(datetime, formats, 8);
    }

    public static double dayOfWeekInMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(8);
    }

    public static double dayInWeek(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, d};
        return ExsltDatetime.getNumber(datetime, formats, 7);
    }

    public static double dayInWeek() {
        Calendar cal = Calendar.getInstance();
        return cal.get(7);
    }

    public static double hourInDay(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, t};
        return ExsltDatetime.getNumber(datetime, formats, 11);
    }

    public static double hourInDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(11);
    }

    public static double minuteInHour(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, t};
        return ExsltDatetime.getNumber(datetime, formats, 12);
    }

    public static double minuteInHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(12);
    }

    public static double secondInMinute(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        String[] formats = new String[]{dt, t};
        return ExsltDatetime.getNumber(datetime, formats, 13);
    }

    public static double secondInMinute() {
        Calendar cal = Calendar.getInstance();
        return cal.get(13);
    }

    public static XObject leapYear(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return new XNumber(Double.NaN);
        }
        String[] formats = new String[]{dt, d, gym, gy};
        double dbl = ExsltDatetime.getNumber(datetime, formats, 1);
        if (Double.isNaN(dbl)) {
            return new XNumber(Double.NaN);
        }
        int yr = (int)dbl;
        return new XBoolean(yr % 400 == 0 || yr % 100 != 0 && yr % 4 == 0);
    }

    public static boolean leapYear() {
        Calendar cal = Calendar.getInstance();
        int yr = cal.get(1);
        return yr % 400 == 0 || yr % 100 != 0 && yr % 4 == 0;
    }

    public static String monthName(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d, gym, gm};
        String formatOut = "MMMM";
        return ExsltDatetime.getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }

    public static String monthName() {
        String format = "MMMM";
        return ExsltDatetime.getNameOrAbbrev(format);
    }

    public static String monthAbbreviation(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d, gym, gm};
        String formatOut = "MMM";
        return ExsltDatetime.getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }

    public static String monthAbbreviation() {
        String format = "MMM";
        return ExsltDatetime.getNameOrAbbrev(format);
    }

    public static String dayName(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d};
        String formatOut = "EEEE";
        return ExsltDatetime.getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }

    public static String dayName() {
        String format = "EEEE";
        return ExsltDatetime.getNameOrAbbrev(format);
    }

    public static String dayAbbreviation(String datetimeIn) throws ParseException {
        String[] edz = ExsltDatetime.getEraDatetimeZone(datetimeIn);
        String datetime = edz[1];
        if (datetime == null) {
            return EMPTY_STR;
        }
        String[] formatsIn = new String[]{dt, d};
        String formatOut = "EEE";
        return ExsltDatetime.getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }

    public static String dayAbbreviation() {
        String format = "EEE";
        return ExsltDatetime.getNameOrAbbrev(format);
    }

    private static String[] getEraDatetimeZone(String in) {
        int z;
        String leader = EMPTY_STR;
        String datetime = in;
        String zone = EMPTY_STR;
        if (in.charAt(0) == '-' && !in.startsWith("--")) {
            leader = "-";
            datetime = in.substring(1);
        }
        if ((z = ExsltDatetime.getZoneStart(datetime)) > 0) {
            zone = datetime.substring(z);
            datetime = datetime.substring(0, z);
        } else if (z == -2) {
            zone = null;
        }
        return new String[]{leader, datetime, zone};
    }

    private static int getZoneStart(String datetime) {
        if (datetime.indexOf(90) == datetime.length() - 1) {
            return datetime.length() - 1;
        }
        if (datetime.length() >= 6 && datetime.charAt(datetime.length() - 3) == ':' && (datetime.charAt(datetime.length() - 6) == '+' || datetime.charAt(datetime.length() - 6) == '-')) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                dateFormat.setLenient(false);
                Date d = dateFormat.parse(datetime.substring(datetime.length() - 5));
                return datetime.length() - 6;
            }
            catch (ParseException pe) {
                System.out.println("ParseException " + pe.getErrorOffset());
                return -2;
            }
        }
        return -1;
    }

    private static Date testFormats(String in, String[] formats) throws ParseException {
        for (int i = 0; i < formats.length; ++i) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formats[i]);
                dateFormat.setLenient(false);
                return dateFormat.parse(in);
            }
            catch (ParseException parseException) {
                continue;
            }
        }
        return null;
    }

    private static double getNumber(String in, String[] formats, int calField) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        Date date = ExsltDatetime.testFormats(in, formats);
        if (date == null) {
            return Double.NaN;
        }
        cal.setTime(date);
        return cal.get(calField);
    }

    private static String getNameOrAbbrev(String in, String[] formatsIn, String formatOut) throws ParseException {
        for (int i = 0; i < formatsIn.length; ++i) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatsIn[i], Locale.ENGLISH);
                dateFormat.setLenient(false);
                Date dt = dateFormat.parse(in);
                dateFormat.applyPattern(formatOut);
                return dateFormat.format(dt);
            }
            catch (ParseException parseException) {
                continue;
            }
        }
        return EMPTY_STR;
    }

    private static String getNameOrAbbrev(String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(cal.getTime());
    }

    public static String formatDate(String dateTime, String pattern) {
        String zone;
        TimeZone timeZone;
        String yearSymbols = "Gy";
        String monthSymbols = "M";
        String daySymbols = "dDEFwW";
        if (dateTime.endsWith("Z") || dateTime.endsWith("z")) {
            timeZone = TimeZone.getTimeZone("GMT");
            dateTime = dateTime.substring(0, dateTime.length() - 1) + "GMT";
            zone = "z";
        } else if (dateTime.length() >= 6 && dateTime.charAt(dateTime.length() - 3) == ':' && (dateTime.charAt(dateTime.length() - 6) == '+' || dateTime.charAt(dateTime.length() - 6) == '-')) {
            String offset = dateTime.substring(dateTime.length() - 6);
            timeZone = "+00:00".equals(offset) || "-00:00".equals(offset) ? TimeZone.getTimeZone("GMT") : TimeZone.getTimeZone("GMT" + offset);
            zone = "z";
            dateTime = dateTime.substring(0, dateTime.length() - 6) + "GMT" + offset;
        } else {
            timeZone = TimeZone.getDefault();
            zone = EMPTY_STR;
        }
        String[] formats = new String[]{dt + zone, d, gym, gy};
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat(t + zone);
            inFormat.setLenient(false);
            Date d = inFormat.parse(dateTime);
            SimpleDateFormat outFormat = new SimpleDateFormat(ExsltDatetime.strip("GyMdDEFwW", pattern));
            outFormat.setTimeZone(timeZone);
            return outFormat.format(d);
        }
        catch (ParseException inFormat) {
            for (int i = 0; i < formats.length; ++i) {
                try {
                    SimpleDateFormat inFormat2 = new SimpleDateFormat(formats[i]);
                    inFormat2.setLenient(false);
                    Date d = inFormat2.parse(dateTime);
                    SimpleDateFormat outFormat = new SimpleDateFormat(pattern);
                    outFormat.setTimeZone(timeZone);
                    return outFormat.format(d);
                }
                catch (ParseException inFormat2) {
                    continue;
                }
            }
            try {
                SimpleDateFormat inFormat3 = new SimpleDateFormat(gmd);
                inFormat3.setLenient(false);
                Date d = inFormat3.parse(dateTime);
                SimpleDateFormat outFormat = new SimpleDateFormat(ExsltDatetime.strip("Gy", pattern));
                outFormat.setTimeZone(timeZone);
                return outFormat.format(d);
            }
            catch (ParseException inFormat3) {
                try {
                    SimpleDateFormat inFormat4 = new SimpleDateFormat(gm);
                    inFormat4.setLenient(false);
                    Date d = inFormat4.parse(dateTime);
                    SimpleDateFormat outFormat = new SimpleDateFormat(ExsltDatetime.strip("Gy", pattern));
                    outFormat.setTimeZone(timeZone);
                    return outFormat.format(d);
                }
                catch (ParseException inFormat4) {
                    try {
                        SimpleDateFormat inFormat5 = new SimpleDateFormat(gd);
                        inFormat5.setLenient(false);
                        Date d = inFormat5.parse(dateTime);
                        SimpleDateFormat outFormat = new SimpleDateFormat(ExsltDatetime.strip("GyM", pattern));
                        outFormat.setTimeZone(timeZone);
                        return outFormat.format(d);
                    }
                    catch (ParseException parseException) {
                        return EMPTY_STR;
                    }
                }
            }
        }
    }

    private static String strip(String symbols, String pattern) {
        int i = 0;
        StringBuffer result = new StringBuffer(pattern.length());
        while (i < pattern.length()) {
            char ch = pattern.charAt(i);
            if (ch == '\'') {
                int endQuote = pattern.indexOf(39, i + 1);
                if (endQuote == -1) {
                    endQuote = pattern.length();
                }
                result.append(pattern.substring(i, endQuote));
                i = endQuote++;
                continue;
            }
            if (symbols.indexOf(ch) > -1) {
                ++i;
                continue;
            }
            result.append(ch);
            ++i;
        }
        return result.toString();
    }
}

