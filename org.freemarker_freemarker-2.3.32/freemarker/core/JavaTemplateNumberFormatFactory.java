/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ExtendedDecimalFormatParser;
import freemarker.core.InvalidFormatParametersException;
import freemarker.core.JavaTemplateNumberFormat;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateNumberFormatFactory;
import freemarker.log.Logger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

class JavaTemplateNumberFormatFactory
extends TemplateNumberFormatFactory {
    static final JavaTemplateNumberFormatFactory INSTANCE = new JavaTemplateNumberFormatFactory();
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private static final ConcurrentHashMap<CacheKey, NumberFormat> GLOBAL_FORMAT_CACHE = new ConcurrentHashMap();
    private static final int LEAK_ALERT_NUMBER_FORMAT_CACHE_SIZE = 1024;

    private JavaTemplateNumberFormatFactory() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public TemplateNumberFormat get(String params, Locale locale, Environment env) throws InvalidFormatParametersException {
        CacheKey cacheKey = new CacheKey(params, locale);
        NumberFormat jFormat = GLOBAL_FORMAT_CACHE.get(cacheKey);
        if (jFormat == null) {
            NumberFormat prevJFormat;
            if ("number".equals(params)) {
                jFormat = NumberFormat.getNumberInstance(locale);
            } else if ("currency".equals(params)) {
                jFormat = NumberFormat.getCurrencyInstance(locale);
            } else if ("percent".equals(params)) {
                jFormat = NumberFormat.getPercentInstance(locale);
            } else {
                try {
                    jFormat = ExtendedDecimalFormatParser.parse(params, locale);
                }
                catch (ParseException e) {
                    String string;
                    String msg = e.getMessage();
                    if (msg != null) {
                        string = msg;
                        throw new InvalidFormatParametersException(string, e);
                    }
                    string = "Invalid DecimalFormat pattern";
                    throw new InvalidFormatParametersException(string, e);
                }
            }
            if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                boolean triggered = false;
                Class<JavaTemplateNumberFormatFactory> clazz = JavaTemplateNumberFormatFactory.class;
                // MONITORENTER : freemarker.core.JavaTemplateNumberFormatFactory.class
                if (GLOBAL_FORMAT_CACHE.size() >= 1024) {
                    triggered = true;
                    GLOBAL_FORMAT_CACHE.clear();
                }
                // MONITOREXIT : clazz
                if (triggered) {
                    LOG.warn("Global Java NumberFormat cache has exceeded 1024 entries => cache flushed. Typical cause: Some template generates high variety of format pattern strings.");
                }
            }
            if ((prevJFormat = GLOBAL_FORMAT_CACHE.putIfAbsent(cacheKey, jFormat)) != null) {
                jFormat = prevJFormat;
            }
        }
        jFormat = (NumberFormat)jFormat.clone();
        return new JavaTemplateNumberFormat(jFormat, params);
    }

    private static final class CacheKey {
        private final String pattern;
        private final Locale locale;

        CacheKey(String pattern, Locale locale) {
            this.pattern = pattern;
            this.locale = locale;
        }

        public boolean equals(Object o) {
            if (o instanceof CacheKey) {
                CacheKey fk = (CacheKey)o;
                return fk.pattern.equals(this.pattern) && fk.locale.equals(this.locale);
            }
            return false;
        }

        public int hashCode() {
            return this.pattern.hashCode() ^ this.locale.hashCode();
        }
    }
}

