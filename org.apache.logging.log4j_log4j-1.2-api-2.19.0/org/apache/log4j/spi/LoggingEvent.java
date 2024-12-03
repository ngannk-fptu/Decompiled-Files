/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.spi;

import java.util.Map;
import java.util.Set;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.bridge.LogEventAdapter;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.ThrowableInformation;

public class LoggingEvent {
    public final long timeStamp;

    public static long getStartTime() {
        return LogEventAdapter.getStartTime();
    }

    public LoggingEvent() {
        this.timeStamp = System.currentTimeMillis();
    }

    public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Level level, Object message, String threadName, ThrowableInformation throwable, String ndc, LocationInfo info, Map properties) {
        this.timeStamp = timeStamp;
    }

    public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable) {
        this.timeStamp = timeStamp;
    }

    public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable) {
        this.timeStamp = System.currentTimeMillis();
    }

    public String getFQNOfLoggerClass() {
        return null;
    }

    public Level getLevel() {
        return null;
    }

    public LocationInfo getLocationInformation() {
        return null;
    }

    public Category getLogger() {
        return null;
    }

    public String getLoggerName() {
        return null;
    }

    public Object getMDC(String key) {
        return null;
    }

    public void getMDCCopy() {
    }

    public Object getMessage() {
        return null;
    }

    public String getNDC() {
        return null;
    }

    public Map getProperties() {
        return null;
    }

    public String getProperty(String key) {
        return null;
    }

    public Set getPropertyKeySet() {
        return null;
    }

    public String getRenderedMessage() {
        return null;
    }

    public String getThreadName() {
        return null;
    }

    public ThrowableInformation getThrowableInformation() {
        return null;
    }

    public String[] getThrowableStrRep() {
        return null;
    }

    public long getTimeStamp() {
        return 0L;
    }

    public Object removeProperty(String propName) {
        return null;
    }

    public void setProperty(String propName, String propValue) {
    }
}

