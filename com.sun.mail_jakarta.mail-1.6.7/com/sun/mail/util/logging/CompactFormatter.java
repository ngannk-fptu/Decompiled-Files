/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.util.logging;

import com.sun.mail.util.logging.LogManagerProperties;
import com.sun.mail.util.logging.SeverityComparator;
import java.util.Date;
import java.util.Formattable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CompactFormatter
extends Formatter {
    private final String fmt;

    private static Class<?>[] loadDeclaredClasses() {
        return new Class[]{Alternate.class};
    }

    public CompactFormatter() {
        String p = this.getClass().getName();
        this.fmt = this.initFormat(p);
    }

    public CompactFormatter(String format) {
        String p = this.getClass().getName();
        this.fmt = format == null ? this.initFormat(p) : format;
    }

    @Override
    public String format(LogRecord record) {
        ResourceBundle rb = record.getResourceBundle();
        Locale l = rb == null ? null : rb.getLocale();
        String msg = this.formatMessage(record);
        String thrown = this.formatThrown(record);
        String err = this.formatError(record);
        Object[] params = new Object[]{this.formatZonedDateTime(record), this.formatSource(record), this.formatLoggerName(record), this.formatLevel(record), msg, thrown, new Alternate(msg, thrown), new Alternate(thrown, msg), record.getSequenceNumber(), this.formatThreadID(record), err, new Alternate(msg, err), new Alternate(err, msg), this.formatBackTrace(record), record.getResourceBundleName(), record.getMessage()};
        if (l == null) {
            return String.format(this.fmt, params);
        }
        return String.format(l, this.fmt, params);
    }

    @Override
    public String formatMessage(LogRecord record) {
        String msg = super.formatMessage(record);
        msg = CompactFormatter.replaceClassName(msg, record.getThrown());
        msg = CompactFormatter.replaceClassName(msg, record.getParameters());
        return msg;
    }

    public String formatMessage(Throwable t) {
        String r;
        if (t != null) {
            Throwable apply = this.apply(t);
            String m = apply.getLocalizedMessage();
            String s = apply.toString();
            String sn = CompactFormatter.simpleClassName(apply.getClass());
            r = !CompactFormatter.isNullOrSpaces(m) ? (s.contains(m) ? (s.startsWith(apply.getClass().getName()) || s.startsWith(sn) ? CompactFormatter.replaceClassName(m, t) : CompactFormatter.replaceClassName(CompactFormatter.simpleClassName(s), t)) : CompactFormatter.replaceClassName(CompactFormatter.simpleClassName(s) + ": " + m, t)) : CompactFormatter.replaceClassName(CompactFormatter.simpleClassName(s), t);
            if (!r.contains(sn)) {
                r = sn + ": " + r;
            }
        } else {
            r = "";
        }
        return r;
    }

    public String formatLevel(LogRecord record) {
        return record.getLevel().getLocalizedName();
    }

    public String formatSource(LogRecord record) {
        String source = record.getSourceClassName();
        source = source != null ? (record.getSourceMethodName() != null ? CompactFormatter.simpleClassName(source) + " " + record.getSourceMethodName() : CompactFormatter.simpleClassName(source)) : CompactFormatter.simpleClassName(record.getLoggerName());
        return source;
    }

    public String formatLoggerName(LogRecord record) {
        return CompactFormatter.simpleClassName(record.getLoggerName());
    }

    public Number formatThreadID(LogRecord record) {
        Long id = LogManagerProperties.getLongThreadID(record);
        if (id == null) {
            id = (long)record.getThreadID() & 0xFFFFFFFFL;
        }
        return id;
    }

    public String formatThrown(LogRecord record) {
        String msg;
        Throwable t = record.getThrown();
        if (t != null) {
            String site = this.formatBackTrace(record);
            msg = this.formatMessage(t) + (CompactFormatter.isNullOrSpaces(site) ? "" : ' ' + site);
        } else {
            msg = "";
        }
        return msg;
    }

    public String formatError(LogRecord record) {
        return this.formatMessage(record.getThrown());
    }

    public String formatBackTrace(LogRecord record) {
        Throwable root;
        StackTraceElement[] trace;
        String site = "";
        Throwable t = record.getThrown();
        if (t != null && CompactFormatter.isNullOrSpaces(site = this.findAndFormat(trace = (root = this.apply(t)).getStackTrace()))) {
            StackTraceElement[] ste;
            int limit = 0;
            for (Throwable c = t; c != null && CompactFormatter.isNullOrSpaces(site = this.findAndFormat(ste = c.getStackTrace())); c = c.getCause()) {
                if (trace.length == 0) {
                    trace = ste;
                }
                if (++limit == 65536) break;
            }
            if (CompactFormatter.isNullOrSpaces(site) && trace.length != 0) {
                site = this.formatStackTraceElement(trace[0]);
            }
        }
        return site;
    }

    private String findAndFormat(StackTraceElement[] trace) {
        String site = "";
        for (StackTraceElement s : trace) {
            if (this.ignore(s)) continue;
            site = this.formatStackTraceElement(s);
            break;
        }
        if (CompactFormatter.isNullOrSpaces(site)) {
            for (StackTraceElement s : trace) {
                if (this.defaultIgnore(s)) continue;
                site = this.formatStackTraceElement(s);
                break;
            }
        }
        return site;
    }

    private String formatStackTraceElement(StackTraceElement s) {
        String v = CompactFormatter.simpleClassName(s.getClassName());
        String result = s.toString().replace(s.getClassName(), v);
        v = CompactFormatter.simpleFileName(s.getFileName());
        if (v != null && result.startsWith(v)) {
            result = result.replace(s.getFileName(), "");
        }
        return result;
    }

    protected Throwable apply(Throwable t) {
        return SeverityComparator.getInstance().apply(t);
    }

    protected boolean ignore(StackTraceElement s) {
        return this.isUnknown(s) || this.defaultIgnore(s);
    }

    protected String toAlternate(String s) {
        return s != null ? s.replaceAll("[\\x00-\\x1F\\x7F]+", "") : null;
    }

    private Comparable<?> formatZonedDateTime(LogRecord record) {
        Date zdt = LogManagerProperties.getZonedDateTime(record);
        if (zdt == null) {
            zdt = new Date(record.getMillis());
        }
        return zdt;
    }

    private boolean defaultIgnore(StackTraceElement s) {
        return this.isSynthetic(s) || this.isStaticUtility(s) || this.isReflection(s);
    }

    private boolean isStaticUtility(StackTraceElement s) {
        try {
            return LogManagerProperties.isStaticUtilityClass(s.getClassName());
        }
        catch (RuntimeException runtimeException) {
        }
        catch (Exception | LinkageError throwable) {
            // empty catch block
        }
        String cn = s.getClassName();
        return cn.endsWith("s") && !cn.endsWith("es") || cn.contains("Util") || cn.endsWith("Throwables");
    }

    private boolean isSynthetic(StackTraceElement s) {
        return s.getMethodName().indexOf(36) > -1;
    }

    private boolean isUnknown(StackTraceElement s) {
        return s.getLineNumber() < 0;
    }

    private boolean isReflection(StackTraceElement s) {
        try {
            return LogManagerProperties.isReflectionClass(s.getClassName());
        }
        catch (RuntimeException runtimeException) {
        }
        catch (Exception | LinkageError throwable) {
            // empty catch block
        }
        return s.getClassName().startsWith("java.lang.reflect.") || s.getClassName().startsWith("sun.reflect.");
    }

    private String initFormat(String p) {
        String v = LogManagerProperties.fromLogManager(p.concat(".format"));
        if (CompactFormatter.isNullOrSpaces(v)) {
            v = "%7$#.160s%n";
        }
        return v;
    }

    private static String replaceClassName(String msg, Throwable t) {
        if (!CompactFormatter.isNullOrSpaces(msg)) {
            int limit = 0;
            for (Throwable c = t; c != null; c = c.getCause()) {
                Class<?> k = c.getClass();
                msg = msg.replace(k.getName(), CompactFormatter.simpleClassName(k));
                if (++limit == 65536) break;
            }
        }
        return msg;
    }

    private static String replaceClassName(String msg, Object[] p) {
        if (!CompactFormatter.isNullOrSpaces(msg) && p != null) {
            for (Object o : p) {
                if (o == null) continue;
                Class<?> k = o.getClass();
                msg = msg.replace(k.getName(), CompactFormatter.simpleClassName(k));
            }
        }
        return msg;
    }

    private static String simpleClassName(Class<?> k) {
        try {
            return k.getSimpleName();
        }
        catch (InternalError internalError) {
            return CompactFormatter.simpleClassName(k.getName());
        }
    }

    private static String simpleClassName(String name) {
        if (name != null) {
            int cursor;
            int c;
            int dot;
            int sign = -1;
            int prev = dot = -1;
            for (cursor = 0; cursor < name.length(); cursor += Character.charCount(c)) {
                c = name.codePointAt(cursor);
                if (!Character.isJavaIdentifierPart(c)) {
                    if (c == 46) {
                        if (dot + 1 != cursor && dot + 1 != sign) {
                            prev = dot;
                            dot = cursor;
                            continue;
                        }
                        return name;
                    }
                    if (dot + 1 != cursor) break;
                    dot = prev;
                    break;
                }
                if (c != 36) continue;
                sign = cursor;
            }
            if (dot > -1 && ++dot < cursor && ++sign < cursor) {
                name = name.substring(sign > dot ? sign : dot);
            }
        }
        return name;
    }

    private static String simpleFileName(String name) {
        if (name != null) {
            int index = name.lastIndexOf(46);
            name = index > -1 ? name.substring(0, index) : name;
        }
        return name;
    }

    private static boolean isNullOrSpaces(String s) {
        return s == null || s.trim().isEmpty();
    }

    static {
        CompactFormatter.loadDeclaredClasses();
    }

    private class Alternate
    implements Formattable {
        private final String left;
        private final String right;

        Alternate(String left, String right) {
            this.left = String.valueOf(left);
            this.right = String.valueOf(right);
        }

        @Override
        public void formatTo(java.util.Formatter formatter, int flags, int width, int precision) {
            String l = this.left;
            String r = this.right;
            if ((flags & 2) == 2) {
                l = l.toUpperCase(formatter.locale());
                r = r.toUpperCase(formatter.locale());
            }
            if ((flags & 4) == 4) {
                l = CompactFormatter.this.toAlternate(l);
                r = CompactFormatter.this.toAlternate(r);
            }
            int lc = 0;
            int rc = 0;
            if (precision >= 0) {
                lc = this.minCodePointCount(l, precision);
                rc = this.minCodePointCount(r, precision);
                if (lc > precision >> 1) {
                    lc = Math.max(lc - rc, lc >> 1);
                }
                rc = Math.min(precision - lc, rc);
                l = l.substring(0, l.offsetByCodePoints(0, lc));
                r = r.substring(0, r.offsetByCodePoints(0, rc));
            }
            if (width > 0) {
                int half;
                if (precision < 0) {
                    lc = this.minCodePointCount(l, width);
                    rc = this.minCodePointCount(r, width);
                }
                if (lc < (half = width >> 1)) {
                    l = this.pad(flags, l, half - lc);
                }
                if (rc < half) {
                    r = this.pad(flags, r, half - rc);
                }
            }
            formatter.format(l, new Object[0]);
            if (!l.isEmpty() && !r.isEmpty()) {
                formatter.format("|", new Object[0]);
            }
            formatter.format(r, new Object[0]);
        }

        private int minCodePointCount(String s, int limit) {
            int len = s.length();
            if (len - limit >= limit) {
                return limit;
            }
            return Math.min(s.codePointCount(0, len), limit);
        }

        private String pad(int flags, String s, int padding) {
            StringBuilder b = new StringBuilder(Math.max(s.length() + padding, padding));
            if ((flags & 1) == 1) {
                for (int i = 0; i < padding; ++i) {
                    b.append(' ');
                }
                b.append(s);
            } else {
                b.append(s);
                for (int i = 0; i < padding; ++i) {
                    b.append(' ');
                }
            }
            return b.toString();
        }
    }
}

