/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.JavaTemplateDateFormat;
import freemarker.core.TemplateDateFormat;
import freemarker.core.TemplateDateFormatFactory;
import freemarker.core.UnknownDateTypeFormattingUnsupportedException;
import freemarker.log.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

class JavaTemplateDateFormatFactory
extends TemplateDateFormatFactory {
    static final JavaTemplateDateFormatFactory INSTANCE = new JavaTemplateDateFormatFactory();
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static final ConcurrentHashMap<CacheKey, DateFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap();
    private static final int LEAK_ALERT_DATE_FORMAT_CACHE_SIZE = 1024;

    private JavaTemplateDateFormatFactory() {
    }

    @Override
    public TemplateDateFormat get(String params, int dateType, Locale locale, TimeZone timeZone, boolean zonelessInput, Environment env) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        return new JavaTemplateDateFormat(this.getJavaDateFormat(dateType, params, locale, timeZone));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private DateFormat getJavaDateFormat(int dateType, String nameOrPattern, Locale locale, TimeZone timeZone) throws UnknownDateTypeFormattingUnsupportedException, InvalidFormatParametersException {
        DateFormat prevJFormat;
        int tok1Style;
        CacheKey cacheKey = new CacheKey(dateType, nameOrPattern, locale, timeZone);
        DateFormat jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
        if (jFormat != null) return (DateFormat)jFormat.clone();
        StringTokenizer tok = new StringTokenizer(nameOrPattern, "_");
        int n = tok1Style = tok.hasMoreTokens() ? this.parseDateStyleToken(tok.nextToken()) : 2;
        if (tok1Style != -1) {
            switch (dateType) {
                case 0: {
                    throw new UnknownDateTypeFormattingUnsupportedException();
                }
                case 1: {
                    jFormat = DateFormat.getTimeInstance(tok1Style, cacheKey.locale);
                    break;
                }
                case 2: {
                    jFormat = DateFormat.getDateInstance(tok1Style, cacheKey.locale);
                    break;
                }
                case 3: {
                    int tok2Style;
                    int n2 = tok2Style = tok.hasMoreTokens() ? this.parseDateStyleToken(tok.nextToken()) : tok1Style;
                    if (tok2Style == -1) break;
                    jFormat = DateFormat.getDateTimeInstance(tok1Style, tok2Style, cacheKey.locale);
                    break;
                }
            }
        }
        if (jFormat == null) {
            try {
                jFormat = new SimpleDateFormat(nameOrPattern, cacheKey.locale);
            }
            catch (IllegalArgumentException e) {
                String string;
                String msg = e.getMessage();
                if (msg != null) {
                    string = msg;
                    throw new InvalidFormatParametersException(string, e);
                }
                string = "Invalid SimpleDateFormat pattern";
                throw new InvalidFormatParametersException(string, e);
            }
        }
        jFormat.setTimeZone(cacheKey.timeZone);
        if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
            boolean triggered = false;
            Class<JavaTemplateDateFormatFactory> clazz = JavaTemplateDateFormatFactory.class;
            // MONITORENTER : freemarker.core.JavaTemplateDateFormatFactory.class
            if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                triggered = true;
                GLOBAL_FORMAT_CACHE.clear();
            }
            // MONITOREXIT : clazz
            if (triggered) {
                LOG.warn("Global Java DateFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
            }
        }
        if ((prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat)) == null) return (DateFormat)jFormat.clone();
        jFormat = prevJFormat;
        return (DateFormat)jFormat.clone();
    }

    private int parseDateStyleToken(String token) {
        if ("short".equals(token)) {
            return 3;
        }
        if ("medium".equals(token)) {
            return 2;
        }
        if ("long".equals(token)) {
            return 1;
        }
        if ("full".equals(token)) {
            return 0;
        }
        return -1;
    }

    private static final class CacheKey {
        private final int dateType;
        private final String pattern;
        private final Locale locale;
        private final TimeZone timeZone;

        CacheKey(int dateType, String pattern, Locale locale, TimeZone timeZone) {
            this.dateType = dateType;
            this.pattern = pattern;
            this.locale = locale;
            this.timeZone = timeZone;
        }

        public boolean equals(Object o) {
            if (o instanceof CacheKey) {
                CacheKey fk = (CacheKey)o;
                return this.dateType == fk.dateType && fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale) && fk.timeZone.equals(this.timeZone);
            }
            return false;
        }

        public int hashCode() {
            return this.dateType ^ this.pattern.hashCode() ^ this.locale.hashCode() ^ this.timeZone.hashCode();
        }
    }
}

