/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.apache.juli.DateFormatCache;

public class OneLineFormatter
extends Formatter {
    private static final String UNKNOWN_THREAD_NAME = "Unknown thread with ID ";
    private static final Object threadMxBeanLock = new Object();
    private static volatile ThreadMXBean threadMxBean = null;
    private static final int THREAD_NAME_CACHE_SIZE = 10000;
    private static final ThreadLocal<ThreadNameCache> threadNameCache = ThreadLocal.withInitial(() -> new ThreadNameCache(10000));
    private static final String DEFAULT_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss.SSS";
    private static final int globalCacheSize = 30;
    private static final int localCacheSize = 5;
    private ThreadLocal<DateFormatCache> localDateCache;
    private volatile MillisHandling millisHandling = MillisHandling.APPEND;

    public OneLineFormatter() {
        String timeFormat = LogManager.getLogManager().getProperty(OneLineFormatter.class.getName() + ".timeFormat");
        if (timeFormat == null) {
            timeFormat = DEFAULT_TIME_FORMAT;
        }
        this.setTimeFormat(timeFormat);
    }

    public void setTimeFormat(String timeFormat) {
        String cachedTimeFormat;
        if (timeFormat.endsWith(".SSS")) {
            cachedTimeFormat = timeFormat.substring(0, timeFormat.length() - 4);
            this.millisHandling = MillisHandling.APPEND;
        } else if (timeFormat.contains("SSS")) {
            this.millisHandling = MillisHandling.REPLACE_SSS;
            cachedTimeFormat = timeFormat;
        } else if (timeFormat.contains("SS")) {
            this.millisHandling = MillisHandling.REPLACE_SS;
            cachedTimeFormat = timeFormat;
        } else if (timeFormat.contains("S")) {
            this.millisHandling = MillisHandling.REPLACE_S;
            cachedTimeFormat = timeFormat;
        } else {
            this.millisHandling = MillisHandling.NONE;
            cachedTimeFormat = timeFormat;
        }
        DateFormatCache globalDateCache = new DateFormatCache(30, cachedTimeFormat, null);
        this.localDateCache = ThreadLocal.withInitial(() -> new DateFormatCache(5, cachedTimeFormat, globalDateCache));
    }

    public String getTimeFormat() {
        return this.localDateCache.get().getTimeFormat();
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        this.addTimestamp(sb, record.getMillis());
        sb.append(' ');
        sb.append(record.getLevel().getLocalizedName());
        sb.append(' ');
        sb.append('[');
        String threadName = Thread.currentThread().getName();
        if (threadName != null && threadName.startsWith("AsyncFileHandlerWriter-")) {
            sb.append(OneLineFormatter.getThreadName(record.getThreadID()));
        } else {
            sb.append(threadName);
        }
        sb.append(']');
        sb.append(' ');
        sb.append(record.getSourceClassName());
        sb.append('.');
        sb.append(record.getSourceMethodName());
        sb.append(' ');
        sb.append(this.formatMessage(record));
        sb.append(System.lineSeparator());
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            IndentingPrintWriter pw = new IndentingPrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.getBuffer());
        }
        return sb.toString();
    }

    protected void addTimestamp(StringBuilder buf, long timestamp) {
        String cachedTimeStamp = this.localDateCache.get().getFormat(timestamp);
        if (this.millisHandling == MillisHandling.NONE) {
            buf.append(cachedTimeStamp);
        } else if (this.millisHandling == MillisHandling.APPEND) {
            buf.append(cachedTimeStamp);
            long frac = timestamp % 1000L;
            buf.append('.');
            if (frac < 100L) {
                if (frac < 10L) {
                    buf.append('0');
                    buf.append('0');
                } else {
                    buf.append('0');
                }
            }
            buf.append(frac);
        } else {
            long frac = timestamp % 1000L;
            int insertStart = cachedTimeStamp.indexOf(35);
            buf.append(cachedTimeStamp.subSequence(0, insertStart));
            if (frac < 100L && this.millisHandling == MillisHandling.REPLACE_SSS) {
                buf.append('0');
                if (frac < 10L) {
                    buf.append('0');
                }
            } else if (frac < 10L && this.millisHandling == MillisHandling.REPLACE_SS) {
                buf.append('0');
            }
            buf.append(frac);
            if (this.millisHandling == MillisHandling.REPLACE_SSS) {
                buf.append(cachedTimeStamp.substring(insertStart + 3));
            } else if (this.millisHandling == MillisHandling.REPLACE_SS) {
                buf.append(cachedTimeStamp.substring(insertStart + 2));
            } else {
                buf.append(cachedTimeStamp.substring(insertStart + 1));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String getThreadName(int logRecordThreadId) {
        Map cache = threadNameCache.get();
        String result = (String)cache.get(logRecordThreadId);
        if (result != null) {
            return result;
        }
        if (logRecordThreadId > 0x3FFFFFFF) {
            result = UNKNOWN_THREAD_NAME + logRecordThreadId;
        } else {
            ThreadInfo threadInfo;
            if (threadMxBean == null) {
                Object object = threadMxBeanLock;
                synchronized (object) {
                    if (threadMxBean == null) {
                        threadMxBean = ManagementFactory.getThreadMXBean();
                    }
                }
            }
            if ((threadInfo = threadMxBean.getThreadInfo(logRecordThreadId)) == null) {
                return Long.toString(logRecordThreadId);
            }
            result = threadInfo.getThreadName();
        }
        cache.put(logRecordThreadId, result);
        return result;
    }

    private static enum MillisHandling {
        NONE,
        APPEND,
        REPLACE_S,
        REPLACE_SS,
        REPLACE_SSS;

    }

    private static class IndentingPrintWriter
    extends PrintWriter {
        IndentingPrintWriter(Writer out) {
            super(out);
        }

        @Override
        public void println(Object x) {
            super.print('\t');
            super.println(x);
        }
    }

    private static class ThreadNameCache
    extends LinkedHashMap<Integer, String> {
        private static final long serialVersionUID = 1L;
        private final int cacheSize;

        ThreadNameCache(int cacheSize) {
            super(cacheSize, 0.75f, true);
            this.cacheSize = cacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
            return this.size() > this.cacheSize;
        }
    }
}

