/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

class HttpUtil {
    HttpUtil() {
    }

    static String mergeCSL(String prefix, Map<String, String> kv) {
        StringWriter sw = new StringWriter();
        sw.write(prefix);
        sw.write(32);
        boolean comma = false;
        for (Map.Entry<String, String> ent : kv.entrySet()) {
            if (!comma) {
                comma = true;
            } else {
                sw.write(44);
            }
            sw.write(ent.getKey());
            sw.write("=\"");
            sw.write(ent.getValue());
            sw.write(34);
        }
        return sw.toString();
    }

    static Map<String, String> splitCSL(String skip, String src) {
        if ((src = src.trim()).startsWith(skip)) {
            src = src.substring(skip.length());
        }
        return new PartLexer(src).Parse();
    }

    public static String[] append(String[] a, String b) {
        if (a == null) {
            return new String[]{b};
        }
        int length = a.length;
        String[] result = new String[length + 1];
        System.arraycopy(a, 0, result, 0, length);
        result[length] = b;
        return result;
    }

    static class Headers
    extends HashMap<String, String[]> {
        private static final String EMPTY = "";

        public String getFirstValue(String key) {
            String[] j = this.getValues(key);
            if (j != null && j.length > 0) {
                return j[0];
            }
            return null;
        }

        public String getFirstValueOrEmpty(String key) {
            String[] j = this.getValues(key);
            if (j != null && j.length > 0) {
                return j[0];
            }
            return EMPTY;
        }

        public String[] getValues(String key) {
            if ((key = this.actualKey(key)) == null) {
                return null;
            }
            return (String[])this.get(key);
        }

        private String actualKey(String header) {
            if (this.containsKey(header)) {
                return header;
            }
            for (String k : this.keySet()) {
                if (!header.equalsIgnoreCase(k)) continue;
                return k;
            }
            return null;
        }

        private boolean hasHeader(String header) {
            return this.actualKey(header) != null;
        }

        public void set(String key, String value) {
            this.put(key, new String[]{value});
        }

        public void add(String key, String value) {
            this.put(key, HttpUtil.append((String[])this.get(key), value));
        }

        public void ensureHeader(String key, String value) {
            if (!this.containsKey(key)) {
                this.set(key, value);
            }
        }

        @Override
        public Object clone() {
            Headers n = new Headers();
            for (Map.Entry v : this.entrySet()) {
                n.put((String)v.getKey(), this.copy((String[])v.getValue()));
            }
            return n;
        }

        private String[] copy(String[] vs) {
            String[] rv = new String[vs.length];
            System.arraycopy(vs, 0, rv, 0, rv.length);
            return rv;
        }
    }

    static class PartLexer {
        private final String src;
        int last = 0;
        int p = 0;

        PartLexer(String src) {
            this.src = src;
        }

        Map<String, String> Parse() {
            HashMap<String, String> out = new HashMap<String, String>();
            String key = null;
            String value = null;
            while (this.p < this.src.length()) {
                this.skipWhiteSpace();
                key = this.consumeAlpha();
                if (key.length() == 0) {
                    throw new IllegalArgumentException("Expecting alpha label.");
                }
                this.skipWhiteSpace();
                if (!this.consumeIf('=')) {
                    throw new IllegalArgumentException("Expecting assign: '='");
                }
                this.skipWhiteSpace();
                if (!this.consumeIf('\"')) {
                    throw new IllegalArgumentException("Expecting start quote: '\"'");
                }
                this.discard();
                value = this.consumeUntil('\"');
                this.discard(1);
                out.put(key, value);
                this.skipWhiteSpace();
                if (!this.consumeIf(',')) break;
                this.discard();
            }
            return out;
        }

        private String consumeAlpha() {
            char c = this.src.charAt(this.p);
            while (this.p < this.src.length() && (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')) {
                ++this.p;
                c = this.src.charAt(this.p);
            }
            String s = this.src.substring(this.last, this.p);
            this.last = this.p;
            return s;
        }

        private void skipWhiteSpace() {
            while (this.p < this.src.length() && this.src.charAt(this.p) < '!') {
                ++this.p;
            }
            this.last = this.p;
        }

        private boolean consumeIf(char c) {
            if (this.p < this.src.length() && this.src.charAt(this.p) == c) {
                ++this.p;
                return true;
            }
            return false;
        }

        private String consumeUntil(char c) {
            while (this.p < this.src.length() && this.src.charAt(this.p) != c) {
                ++this.p;
            }
            String s = this.src.substring(this.last, this.p);
            this.last = this.p;
            return s;
        }

        private void discard() {
            this.last = this.p;
        }

        private void discard(int i) {
            this.p += i;
            this.last = this.p;
        }
    }
}

