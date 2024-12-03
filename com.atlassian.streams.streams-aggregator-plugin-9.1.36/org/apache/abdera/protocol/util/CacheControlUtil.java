/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.protocol.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.protocol.util.AbstractRequest;
import org.apache.abdera.protocol.util.AbstractResponse;

public class CacheControlUtil {
    public static boolean isIdempotent(String method) {
        try {
            Idempotent.valueOf(method.toUpperCase());
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static long value(String val) {
        return val != null ? Long.parseLong(val) : -1L;
    }

    private static void append(StringBuilder buf, String value) {
        if (buf.length() > 0) {
            buf.append(", ");
        }
        buf.append(value);
    }

    public static String buildCacheControl(AbstractRequest request) {
        StringBuilder buf = new StringBuilder();
        if (request.isNoCache()) {
            CacheControlUtil.append(buf, "no-cache");
        }
        if (request.isNoStore()) {
            CacheControlUtil.append(buf, "no-store");
        }
        if (request.isNoTransform()) {
            CacheControlUtil.append(buf, "no-transform");
        }
        if (request.isOnlyIfCached()) {
            CacheControlUtil.append(buf, "only-if-cached");
        }
        if (request.getMaxAge() != -1L) {
            CacheControlUtil.append(buf, "max-age=" + request.getMaxAge());
        }
        if (request.getMaxStale() != -1L) {
            CacheControlUtil.append(buf, "max-stale=" + request.getMaxStale());
        }
        if (request.getMinFresh() != -1L) {
            CacheControlUtil.append(buf, "min-fresh=" + request.getMinFresh());
        }
        return buf.toString();
    }

    public static void parseCacheControl(String cc, AbstractRequest request) {
        if (cc == null || cc.length() == 0) {
            return;
        }
        CacheControlParser parser = new CacheControlParser(cc);
        request.setNoCache(false);
        request.setNoStore(false);
        request.setNoTransform(false);
        request.setOnlyIfCached(false);
        request.setMaxAge(-1L);
        request.setMaxStale(-1L);
        request.setMinFresh(-1L);
        for (Directive directive : parser) {
            switch (directive) {
                case NOCACHE: {
                    request.setNoCache(true);
                    break;
                }
                case NOSTORE: {
                    request.setNoStore(true);
                    break;
                }
                case NOTRANSFORM: {
                    request.setNoTransform(true);
                    break;
                }
                case ONLYIFCACHED: {
                    request.setOnlyIfCached(true);
                    break;
                }
                case MAXAGE: {
                    request.setMaxAge(CacheControlUtil.value(parser.getValue(directive)));
                    break;
                }
                case MAXSTALE: {
                    request.setMaxStale(CacheControlUtil.value(parser.getValue(directive)));
                    break;
                }
                case MINFRESH: {
                    request.setMinFresh(CacheControlUtil.value(parser.getValue(directive)));
                }
            }
        }
    }

    public static void parseCacheControl(String cc, AbstractResponse response) {
        if (cc == null) {
            return;
        }
        CacheControlParser parser = new CacheControlParser(cc);
        response.setNoCache(false);
        response.setNoStore(false);
        response.setNoTransform(false);
        response.setMustRevalidate(false);
        response.setPrivate(false);
        response.setPublic(false);
        response.setMaxAge(-1L);
        for (Directive directive : parser) {
            switch (directive) {
                case NOCACHE: {
                    response.setNoCache(true);
                    response.setNoCacheHeaders(parser.getValues(directive));
                    break;
                }
                case NOSTORE: {
                    response.setNoStore(true);
                    break;
                }
                case NOTRANSFORM: {
                    response.setNoTransform(true);
                    break;
                }
                case MUSTREVALIDATE: {
                    response.setMustRevalidate(true);
                    break;
                }
                case PUBLIC: {
                    response.setPublic(true);
                    break;
                }
                case PRIVATE: {
                    response.setPrivate(true);
                    response.setPrivateHeaders(parser.getValues(directive));
                    break;
                }
                case MAXAGE: {
                    response.setMaxAge(CacheControlUtil.value(parser.getValue(directive)));
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class CacheControlParser
    implements Iterable<Directive> {
        private static final String REGEX = "\\s*([\\w\\-]+)\\s*(=)?\\s*(\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\")?\\s*";
        private static final Pattern pattern = Pattern.compile("\\s*([\\w\\-]+)\\s*(=)?\\s*(\\d+|\\\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)+\\\")?\\s*");
        private HashMap<Directive, String> values = new HashMap();

        public CacheControlParser(String value) {
            Matcher matcher = pattern.matcher(value);
            while (matcher.find()) {
                String d = matcher.group(1);
                Directive directive = Directive.select(d);
                if (directive == Directive.UNKNOWN) continue;
                this.values.put(directive, matcher.group(3));
            }
        }

        public Map<Directive, String> getValues() {
            return this.values;
        }

        public String getValue(Directive directive) {
            return this.values.get((Object)directive);
        }

        @Override
        public Iterator<Directive> iterator() {
            return this.values.keySet().iterator();
        }

        public String[] getValues(Directive directive) {
            String value = this.getValue(directive);
            if (value != null) {
                return CompressionUtil.splitAndTrim(value, ",", true);
            }
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Directive {
        MAXAGE,
        MAXSTALE,
        MINFRESH,
        NOCACHE,
        NOSTORE,
        NOTRANSFORM,
        ONLYIFCACHED,
        MUSTREVALIDATE,
        PRIVATE,
        PROXYREVALIDATE,
        PUBLIC,
        SMAXAGE,
        UNKNOWN;


        public static Directive select(String d) {
            try {
                d = d.toUpperCase().replaceAll("-", "");
                return Directive.valueOf(d);
            }
            catch (Exception exception) {
                return UNKNOWN;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static enum Idempotent {
        GET,
        HEAD,
        OPTIONS;

    }
}

