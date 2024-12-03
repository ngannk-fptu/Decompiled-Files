/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateFormatCache {
    public static final char MSEC_PATTERN = '#';
    private final String format;
    private final int cacheSize;
    private final Cache cache;

    private String tidyFormat(String format) {
        boolean escape = false;
        StringBuilder result = new StringBuilder();
        int len = format.length();
        for (int i = 0; i < len; ++i) {
            char x = format.charAt(i);
            if (escape || x != 'S') {
                result.append(x);
            } else {
                result.append('#');
            }
            if (x != '\'') continue;
            escape = !escape;
        }
        return result.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DateFormatCache(int size, String format, DateFormatCache parent) {
        this.cacheSize = size;
        this.format = this.tidyFormat(format);
        Cache parentCache = null;
        if (parent != null) {
            DateFormatCache dateFormatCache = parent;
            synchronized (dateFormatCache) {
                parentCache = parent.cache;
            }
        }
        this.cache = new Cache(parentCache);
    }

    public String getFormat(long time) {
        return this.cache.getFormat(time);
    }

    public String getTimeFormat() {
        return this.format;
    }

    private class Cache {
        private long previousSeconds = Long.MIN_VALUE;
        private String previousFormat = "";
        private long first = Long.MIN_VALUE;
        private long last = Long.MIN_VALUE;
        private int offset = 0;
        private final Date currentDate = new Date();
        private String[] cache;
        private SimpleDateFormat formatter;
        private Cache parent = null;

        private Cache(Cache parent) {
            this.cache = new String[DateFormatCache.this.cacheSize];
            this.formatter = new SimpleDateFormat(DateFormatCache.this.format, Locale.US);
            this.formatter.setTimeZone(TimeZone.getDefault());
            this.parent = parent;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private String getFormat(long time) {
            int i;
            long seconds = time / 1000L;
            if (seconds == this.previousSeconds) {
                return this.previousFormat;
            }
            this.previousSeconds = seconds;
            int index = (this.offset + (int)(seconds - this.first)) % DateFormatCache.this.cacheSize;
            if (index < 0) {
                index += DateFormatCache.this.cacheSize;
            }
            if (seconds >= this.first && seconds <= this.last) {
                if (this.cache[index] != null) {
                    this.previousFormat = this.cache[index];
                    return this.previousFormat;
                }
            } else if (seconds >= this.last + (long)DateFormatCache.this.cacheSize || seconds <= this.first - (long)DateFormatCache.this.cacheSize) {
                this.first = seconds;
                this.last = this.first + (long)DateFormatCache.this.cacheSize - 1L;
                index = 0;
                this.offset = 0;
                for (i = 1; i < DateFormatCache.this.cacheSize; ++i) {
                    this.cache[i] = null;
                }
            } else if (seconds > this.last) {
                i = 1;
                while ((long)i < seconds - this.last) {
                    this.cache[(index + ((DateFormatCache)DateFormatCache.this).cacheSize - i) % ((DateFormatCache)DateFormatCache.this).cacheSize] = null;
                    ++i;
                }
                this.first = seconds - (long)(DateFormatCache.this.cacheSize - 1);
                this.last = seconds;
                this.offset = (index + 1) % DateFormatCache.this.cacheSize;
            } else if (seconds < this.first) {
                i = 1;
                while ((long)i < this.first - seconds) {
                    this.cache[(index + i) % ((DateFormatCache)DateFormatCache.this).cacheSize] = null;
                    ++i;
                }
                this.first = seconds;
                this.last = seconds + (long)(DateFormatCache.this.cacheSize - 1);
                this.offset = index;
            }
            if (this.parent != null) {
                Cache cache = this.parent;
                synchronized (cache) {
                    this.previousFormat = this.parent.getFormat(time);
                }
            } else {
                this.currentDate.setTime(time);
                this.previousFormat = this.formatter.format(this.currentDate);
            }
            this.cache[index] = this.previousFormat;
            return this.previousFormat;
        }
    }
}

