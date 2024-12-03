/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.catalina.valves.rewrite;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.catalina.util.URLEncoder;
import org.apache.catalina.valves.rewrite.RewriteMap;
import org.apache.tomcat.util.buf.UDecoder;

public class InternalRewriteMap {
    public static RewriteMap toMap(String name) {
        if ("toupper".equals(name)) {
            return new UpperCase();
        }
        if ("tolower".equals(name)) {
            return new LowerCase();
        }
        if ("escape".equals(name)) {
            return new Escape();
        }
        if ("unescape".equals(name)) {
            return new Unescape();
        }
        return null;
    }

    public static class UpperCase
    implements RewriteMap {
        private Locale locale = Locale.getDefault();

        @Override
        public String setParameters(String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }

        @Override
        public String lookup(String key) {
            if (key != null) {
                return key.toUpperCase(this.locale);
            }
            return null;
        }
    }

    public static class LowerCase
    implements RewriteMap {
        private Locale locale = Locale.getDefault();

        @Override
        public String setParameters(String params) {
            this.locale = Locale.forLanguageTag(params);
            return null;
        }

        @Override
        public String lookup(String key) {
            if (key != null) {
                return key.toLowerCase(this.locale);
            }
            return null;
        }
    }

    public static class Escape
    implements RewriteMap {
        private Charset charset = StandardCharsets.UTF_8;

        @Override
        public String setParameters(String params) {
            this.charset = Charset.forName(params);
            return null;
        }

        @Override
        public String lookup(String key) {
            if (key != null) {
                return URLEncoder.DEFAULT.encode(key, this.charset);
            }
            return null;
        }
    }

    public static class Unescape
    implements RewriteMap {
        private Charset charset = StandardCharsets.UTF_8;

        @Override
        public String setParameters(String params) {
            this.charset = Charset.forName(params);
            return null;
        }

        @Override
        public String lookup(String key) {
            if (key != null) {
                return UDecoder.URLDecode((String)key, (Charset)this.charset);
            }
            return null;
        }
    }
}

