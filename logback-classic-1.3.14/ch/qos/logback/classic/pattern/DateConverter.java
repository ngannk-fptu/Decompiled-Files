/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.util.CachingDateFormatter
 */
package ch.qos.logback.classic.pattern;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.util.CachingDateFormatter;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

public class DateConverter
extends ClassicConverter {
    long lastTimestamp = -1L;
    String timestampStrCache = null;
    CachingDateFormatter cachingDateFormatter = null;

    public void start() {
        String datePattern = this.getFirstOption();
        if (datePattern == null) {
            datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
        }
        if (datePattern.equals("ISO8601")) {
            datePattern = "yyyy-MM-dd HH:mm:ss,SSS";
        }
        List optionList = this.getOptionList();
        ZoneId zoneId = null;
        if (optionList != null && optionList.size() > 1) {
            String zoneIdString = (String)optionList.get(1);
            zoneId = ZoneId.of(zoneIdString);
            this.addInfo("Setting zoneId to \"" + zoneId + "\"");
        }
        Locale locale = null;
        if (optionList != null && optionList.size() > 2) {
            String localeIdStr = (String)optionList.get(2);
            locale = Locale.forLanguageTag(localeIdStr);
            this.addInfo("Setting locale to \"" + locale + "\"");
        }
        try {
            this.cachingDateFormatter = new CachingDateFormatter(datePattern, zoneId, locale);
        }
        catch (IllegalArgumentException e) {
            this.addWarn("Could not instantiate SimpleDateFormat with pattern " + datePattern, e);
            this.cachingDateFormatter = new CachingDateFormatter("yyyy-MM-dd HH:mm:ss,SSS", zoneId);
        }
        super.start();
    }

    public String convert(ILoggingEvent le) {
        long timestamp = le.getTimeStamp();
        return this.cachingDateFormatter.format(timestamp);
    }
}

