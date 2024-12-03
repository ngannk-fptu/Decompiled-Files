/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.sal.api.component.ComponentLocator
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.util.ClasspathUtils;
import com.atlassian.confluence.util.classpath.ClasspathClasses;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeZone {
    private static final Logger log = LoggerFactory.getLogger(TimeZone.class);
    private static final Map<String, TimeZone> timeZonesByID = TimeZone.loadTimeZones();
    private final java.util.TimeZone wrappedTimeZone;

    public static TimeZone getInstance(String timeZoneID) {
        if (!timeZonesByID.containsKey(timeZoneID)) {
            return TimeZone.determineDefaultTimeZone();
        }
        return timeZonesByID.get(timeZoneID);
    }

    public static TimeZone getInstance(java.util.TimeZone timeZone) {
        String timeZoneID = timeZone.getID();
        if (!timeZonesByID.containsKey(timeZoneID)) {
            log.debug("Adding explicitly requested timezone: " + timeZoneID);
            timeZonesByID.put(timeZoneID, new TimeZone(timeZoneID));
        }
        return timeZonesByID.get(timeZoneID);
    }

    private TimeZone(String timeZoneID) {
        this.wrappedTimeZone = timeZoneID == null ? java.util.TimeZone.getDefault() : java.util.TimeZone.getTimeZone(timeZoneID);
    }

    public String getID() {
        return this.wrappedTimeZone.getID();
    }

    public String getMessageKey() {
        return "time.zone." + this.getID().replaceAll("[^A-Za-z]", ".");
    }

    public String toString() {
        return this.getMessageKey();
    }

    public int hashCode() {
        return 31 + this.getID().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null || !o.getClass().equals(this.getClass())) {
            return false;
        }
        return ((TimeZone)o).getID().equals(this.getID());
    }

    public String getDisplayOffset() {
        int offset = this.getRawOffset();
        String sign = offset < 0 ? "-" : "+";
        offset = Math.abs(offset);
        int millis = offset % 1000;
        offset = (offset - millis) / 1000;
        int seconds = offset % 60;
        offset = (offset - seconds) / 60;
        int minutes = offset % 60;
        int hours = offset = (offset - minutes) / 60;
        return MessageFormat.format("{0}{1,number,00}{2,number,00}", sign, hours, minutes);
    }

    private int getRawOffset() {
        return this.wrappedTimeZone.getOffset(new Date().getTime());
    }

    @Deprecated
    public static TimeZone getDefault() {
        return TimeZone.determineDefaultTimeZone();
    }

    private static TimeZone determineDefaultTimeZone() {
        String defaultTimezoneId;
        TimeZoneManager timeZoneManager;
        java.util.TimeZone defaultTimeZone = null;
        if (ComponentLocator.isInitialized() && (timeZoneManager = (TimeZoneManager)ComponentLocator.getComponent(TimeZoneManager.class)) != null) {
            defaultTimeZone = timeZoneManager.getDefaultTimeZone();
        }
        if (defaultTimeZone == null) {
            defaultTimeZone = java.util.TimeZone.getDefault();
        }
        if (!timeZonesByID.containsKey(defaultTimezoneId = defaultTimeZone.getID())) {
            log.debug("Adding Server's default timezone: " + defaultTimezoneId);
            timeZonesByID.put(defaultTimezoneId, new TimeZone(defaultTimezoneId));
        }
        return TimeZone.getInstance(defaultTimezoneId);
    }

    public static List<TimeZone> getSortedTimeZones() {
        TreeSet<TimeZone> timeZones = new TreeSet<TimeZone>(new TimeZoneComparator());
        timeZones.addAll(timeZonesByID.values());
        ArrayList<TimeZone> result = new ArrayList<TimeZone>();
        for (TimeZone timeZone : timeZones) {
            result.add(timeZone);
        }
        return result;
    }

    private static Map<String, TimeZone> loadTimeZones() {
        Properties timeZoneProperties = new Properties();
        try {
            InputStream asStream = ClassLoaderUtils.getResourceAsStream((String)"com/atlassian/confluence/core/timezones.properties", TimeZone.class);
            if (asStream == null) {
                ClasspathClasses clazzes = ClasspathUtils.getFilesInClasspathJars();
                throw new RuntimeException("Cannot find timeezone.properties. The classpath is " + clazzes.getJarUrlsByClass().toString());
            }
            timeZoneProperties.load(asStream);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot load time zones from properties file.", e);
        }
        String timeZoneList = timeZoneProperties.getProperty("time.zone.list");
        if (timeZoneList == null || timeZoneList.trim().equals("")) {
            throw new RuntimeException("time.zone.list property missing from time zones properties file");
        }
        String[] timeZoneIDs = timeZoneList.split("[, ]+");
        HashMap<String, TimeZone> result = new HashMap<String, TimeZone>();
        for (int i = 0; i < timeZoneIDs.length; ++i) {
            String timeZoneID = timeZoneIDs[i];
            result.put(timeZoneID, new TimeZone(timeZoneID));
        }
        return result;
    }

    public java.util.TimeZone getWrappedTimeZone() {
        return this.wrappedTimeZone;
    }

    private static class TimeZoneComparator
    implements Comparator<TimeZone> {
        private TimeZoneComparator() {
        }

        @Override
        public int compare(TimeZone tz1, TimeZone tz2) {
            if (tz1.getRawOffset() < tz2.getRawOffset()) {
                return -1;
            }
            if (tz1.getRawOffset() > tz2.getRawOffset()) {
                return 1;
            }
            return tz1.getID().compareToIgnoreCase(tz2.getID());
        }
    }
}

