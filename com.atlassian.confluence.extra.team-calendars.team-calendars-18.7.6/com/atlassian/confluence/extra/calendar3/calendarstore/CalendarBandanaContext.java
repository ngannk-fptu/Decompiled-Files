/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.confluence.setup.bandana.KeyedBandanaContext
 */
package com.atlassian.confluence.extra.calendar3.calendarstore;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import org.apache.commons.lang.StringUtils;

public class CalendarBandanaContext
implements KeyedBandanaContext {
    private static final String CONTEXT_KEY_PREFIX = "_CALENDAR";
    private final String contextKey;

    public CalendarBandanaContext(String baseContextKey, String subCalendarId) {
        if (StringUtils.isBlank(subCalendarId)) {
            throw new IllegalArgumentException("Blank sub-calendar ID specified");
        }
        this.contextKey = this.createBaseContextKey(baseContextKey).append('_').append(subCalendarId).toString();
    }

    private StringBuilder createBaseContextKey(String baseContextKey) {
        return new StringBuilder("_CALENDAR_").append(baseContextKey);
    }

    public CalendarBandanaContext(String baseContextKey) {
        this.contextKey = this.createBaseContextKey(baseContextKey).toString();
    }

    public String getContextKey() {
        return this.contextKey;
    }

    public BandanaContext getParentContext() {
        return null;
    }

    public boolean hasParentContext() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CalendarBandanaContext that = (CalendarBandanaContext)o;
        return this.contextKey != null ? this.contextKey.equals(that.contextKey) : that.contextKey == null;
    }

    public int hashCode() {
        return this.contextKey != null ? this.contextKey.hashCode() : 0;
    }
}

