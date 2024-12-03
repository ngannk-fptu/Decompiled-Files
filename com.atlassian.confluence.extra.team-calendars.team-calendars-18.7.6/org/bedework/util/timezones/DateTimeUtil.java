/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.timezones;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.bedework.util.timezones.Timezones;
import org.bedework.util.timezones.TimezonesException;

public class DateTimeUtil {
    private static final DateFormat isoDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static final DateFormat rfcDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd");
    private static final DateFormat isoDateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final DateFormat rfcDateTimeFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
    private static final DateFormat isoDateTimeTZFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    private static final DateFormat rfcDateTimeTZFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
    private static final DateFormat isoDateTimeUTCTZFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final DateFormat isoDateTimeUTCFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
    private static final DateFormat rfcDateTimeUTCFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
    private static final DateFormat rfc822GMTFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    private DateTimeUtil() {
    }

    public static Date yesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        return cal.getTime();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String isoDate(Date val) {
        DateFormat dateFormat = isoDateFormat;
        synchronized (dateFormat) {
            try {
                isoDateFormat.setTimeZone(Timezones.getDefaultTz());
            }
            catch (TimezonesException tze) {
                throw new RuntimeException(tze);
            }
            return isoDateFormat.format(val);
        }
    }

    public static String isoDate() {
        return DateTimeUtil.isoDate(new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String rfcDate(Date val) {
        DateFormat dateFormat = rfcDateFormat;
        synchronized (dateFormat) {
            try {
                rfcDateFormat.setTimeZone(Timezones.getDefaultTz());
            }
            catch (TimezonesException tze) {
                throw new RuntimeException(tze);
            }
            return rfcDateFormat.format(val);
        }
    }

    public static String rfcDate() {
        return DateTimeUtil.rfcDate(new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String isoDateTime(Date val) {
        DateFormat dateFormat = isoDateTimeFormat;
        synchronized (dateFormat) {
            try {
                isoDateTimeFormat.setTimeZone(Timezones.getDefaultTz());
            }
            catch (TimezonesException tze) {
                throw new RuntimeException(tze);
            }
            return isoDateTimeFormat.format(val);
        }
    }

    public static String isoDateTime() {
        return DateTimeUtil.isoDateTime(new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String isoDateTime(Date val, TimeZone tz) {
        DateFormat dateFormat = isoDateTimeTZFormat;
        synchronized (dateFormat) {
            isoDateTimeTZFormat.setTimeZone(tz);
            return isoDateTimeTZFormat.format(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String rfcDateTime(Date val) {
        DateFormat dateFormat = rfcDateTimeFormat;
        synchronized (dateFormat) {
            try {
                rfcDateTimeFormat.setTimeZone(Timezones.getDefaultTz());
            }
            catch (TimezonesException tze) {
                throw new RuntimeException(tze);
            }
            return rfcDateTimeFormat.format(val);
        }
    }

    public static String rfcDateTime() {
        return DateTimeUtil.rfcDateTime(new Date());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String rfcDateTime(Date val, TimeZone tz) {
        DateFormat dateFormat = rfcDateTimeTZFormat;
        synchronized (dateFormat) {
            rfcDateTimeTZFormat.setTimeZone(tz);
            return rfcDateTimeTZFormat.format(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String isoDateTimeUTC(Date val) {
        DateFormat dateFormat = isoDateTimeUTCFormat;
        synchronized (dateFormat) {
            return isoDateTimeUTCFormat.format(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String rfcDateTimeUTC(Date val) {
        DateFormat dateFormat = rfcDateTimeUTCFormat;
        synchronized (dateFormat) {
            return rfcDateTimeUTCFormat.format(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String rfc822Date(Date val) {
        DateFormat dateFormat = rfc822GMTFormat;
        synchronized (dateFormat) {
            return rfc822GMTFormat.format(val);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromISODate(String val) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateFormat;
            synchronized (dateFormat) {
                try {
                    isoDateFormat.setTimeZone(Timezones.getDefaultTz());
                }
                catch (TimezonesException tze) {
                    throw new RuntimeException(tze);
                }
                return isoDateFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromRfcDate(String val) throws BadDateException {
        try {
            DateFormat dateFormat = rfcDateFormat;
            synchronized (dateFormat) {
                try {
                    rfcDateFormat.setTimeZone(Timezones.getDefaultTz());
                }
                catch (TimezonesException tze) {
                    throw new RuntimeException(tze);
                }
                return rfcDateFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromISODateTime(String val) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateTimeFormat;
            synchronized (dateFormat) {
                try {
                    isoDateTimeFormat.setTimeZone(Timezones.getDefaultTz());
                }
                catch (TimezonesException tze) {
                    throw new RuntimeException(tze);
                }
                return isoDateTimeFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    public static Date fromRfcDateTime(String val) throws BadDateException {
        try {
            return DateTimeUtil.fromRfcDateTime(val, Timezones.getDefaultTz());
        }
        catch (BadDateException bde) {
            throw bde;
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromRfcDateTime(String val, TimeZone tz) throws BadDateException {
        try {
            DateFormat dateFormat = rfcDateTimeTZFormat;
            synchronized (dateFormat) {
                rfcDateTimeTZFormat.setTimeZone(tz);
                return rfcDateTimeTZFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromISODateTime(String val, TimeZone tz) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateTimeTZFormat;
            synchronized (dateFormat) {
                isoDateTimeTZFormat.setTimeZone(tz);
                return isoDateTimeTZFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromISODateTimeUTC(String val, TimeZone tz) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateTimeUTCTZFormat;
            synchronized (dateFormat) {
                isoDateTimeUTCTZFormat.setTimeZone(tz);
                return isoDateTimeUTCTZFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromISODateTimeUTC(String val) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateTimeUTCFormat;
            synchronized (dateFormat) {
                return isoDateTimeUTCFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Date fromRfcDateTimeUTC(String val) throws BadDateException {
        try {
            DateFormat dateFormat = rfcDateTimeUTCFormat;
            synchronized (dateFormat) {
                return rfcDateTimeUTCFormat.parse(val);
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String fromISODateTimeUTCtoRfc822(String val) throws BadDateException {
        try {
            DateFormat dateFormat = isoDateTimeUTCFormat;
            synchronized (dateFormat) {
                return DateTimeUtil.rfc822Date(isoDateTimeUTCFormat.parse(val));
            }
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    public static boolean isISODate(String val) throws BadDateException {
        try {
            if (val.length() != 8) {
                return false;
            }
            DateTimeUtil.fromISODate(val);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean isISODateTimeUTC(String val) throws BadDateException {
        try {
            if (val.length() != 16) {
                return false;
            }
            DateTimeUtil.fromISODateTimeUTC(val);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean isISODateTime(String val) throws BadDateException {
        try {
            if (val.length() != 15) {
                return false;
            }
            DateTimeUtil.fromISODateTime(val);
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static Date fromDate(String dt) throws BadDateException {
        try {
            if (dt == null) {
                return null;
            }
            if (dt.indexOf("T") > 0) {
                return DateTimeUtil.fromDateTime(dt);
            }
            if (!dt.contains("-")) {
                return DateTimeUtil.fromISODate(dt);
            }
            return DateTimeUtil.fromRfcDate(dt);
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    public static Date fromDateTime(String dt) throws BadDateException {
        try {
            if (dt == null) {
                return null;
            }
            if (!dt.contains("-")) {
                return DateTimeUtil.fromISODateTimeUTC(dt);
            }
            return DateTimeUtil.fromRfcDateTimeUTC(dt);
        }
        catch (Throwable t) {
            throw new BadDateException();
        }
    }

    static {
        isoDateTimeUTCFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        isoDateTimeUTCFormat.setLenient(false);
        rfcDateTimeUTCFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        rfcDateTimeUTCFormat.setLenient(false);
        rfc822GMTFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
    }

    public static class BadDateException
    extends Throwable {
        public BadDateException() {
            super("Bad date");
        }

        public BadDateException(String msg) {
            super(msg);
        }
    }
}

