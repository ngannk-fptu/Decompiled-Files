/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.internal.base;

import com.google.common.base.Preconditions;
import com.google.template.soy.internal.base.CharEscaper;
import com.google.template.soy.internal.base.CharEscaperBuilder;
import com.google.template.soy.internal.base.Escaper;
import com.google.template.soy.internal.base.PercentEscaper;
import java.io.IOException;

public final class CharEscapers {
    private static final CharEscaper NULL_ESCAPER = new CharEscaper(){

        @Override
        public String escape(String string) {
            Preconditions.checkNotNull((Object)string);
            return string;
        }

        @Override
        public Appendable escape(final Appendable out) {
            Preconditions.checkNotNull((Object)out);
            return new Appendable(){

                @Override
                public Appendable append(CharSequence csq) throws IOException {
                    Preconditions.checkNotNull((Object)csq);
                    out.append(csq);
                    return this;
                }

                @Override
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    Preconditions.checkNotNull((Object)csq);
                    out.append(csq, start, end);
                    return this;
                }

                @Override
                public Appendable append(char c) throws IOException {
                    out.append(c);
                    return this;
                }
            };
        }

        @Override
        protected char[] escape(char c) {
            return null;
        }
    };
    private static final CharEscaper XML_ESCAPER = CharEscapers.newBasicXmlEscapeBuilder().addEscape('\"', "&quot;").addEscape('\'', "&apos;").toEscaper();
    private static final CharEscaper XML_CONTENT_ESCAPER = CharEscapers.newBasicXmlEscapeBuilder().toEscaper();
    private static final CharEscaper ASCII_HTML_ESCAPER = new CharEscaperBuilder().addEscape('\"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").toEscaper();
    private static final Escaper URI_ESCAPER = new PercentEscaper("-_.*", true);
    private static final Escaper URI_ESCAPER_NO_PLUS = new PercentEscaper("-_.*", false);
    private static final Escaper URI_PATH_ESCAPER = new PercentEscaper("-_.!~*'()@:$&,;=", false);
    private static final Escaper URI_QUERY_STRING_ESCAPER = new PercentEscaper("-_.!~*'()@:$,;/?:", false);
    private static final Escaper CPP_URI_ESCAPER = new PercentEscaper("!()*-._~,/:", true);
    private static final CharEscaper JAVA_STRING_ESCAPER = new JavaCharEscaper(new CharEscaperBuilder().addEscape('\b', "\\b").addEscape('\f', "\\f").addEscape('\n', "\\n").addEscape('\r', "\\r").addEscape('\t', "\\t").addEscape('\"', "\\\"").addEscape('\\', "\\\\").toArray());
    private static final CharEscaper JAVA_CHAR_ESCAPER = new JavaCharEscaper(new CharEscaperBuilder().addEscape('\b', "\\b").addEscape('\f', "\\f").addEscape('\n', "\\n").addEscape('\r', "\\r").addEscape('\t', "\\t").addEscape('\'', "\\'").addEscape('\"', "\\\"").addEscape('\\', "\\\\").toArray());
    private static final CharEscaper JAVA_STRING_UNICODE_ESCAPER = new CharEscaper(){

        @Override
        protected char[] escape(char c) {
            if (c <= '\u007f') {
                return null;
            }
            char[] r = new char[6];
            r[5] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[4] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[3] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[2] = HEX_DIGITS[c & 0xF];
            r[1] = 117;
            r[0] = 92;
            return r;
        }
    };
    private static final CharEscaper PYTHON_ESCAPER = new CharEscaperBuilder().addEscape('\n', "\\n").addEscape('\r', "\\r").addEscape('\t', "\\t").addEscape('\\', "\\\\").addEscape('\"', "\\\"").addEscape('\'', "\\'").toEscaper();
    private static final CharEscaper JAVASCRIPT_ESCAPER = new JavascriptCharEscaper(new CharEscaperBuilder().addEscape('\'', "\\x27").addEscape('\"', "\\x22").addEscape('<', "\\x3c").addEscape('=', "\\x3d").addEscape('>', "\\x3e").addEscape('&', "\\x26").addEscape('\b', "\\b").addEscape('\t', "\\t").addEscape('\n', "\\n").addEscape('\f', "\\f").addEscape('\r', "\\r").addEscape('\\', "\\\\").toArray());
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private CharEscapers() {
    }

    public static CharEscaper nullEscaper() {
        return NULL_ESCAPER;
    }

    public static CharEscaper xmlEscaper() {
        return XML_ESCAPER;
    }

    public static CharEscaper xmlContentEscaper() {
        return XML_CONTENT_ESCAPER;
    }

    public static CharEscaper htmlEscaper() {
        return HtmlEscaperHolder.HTML_ESCAPER;
    }

    public static CharEscaper asciiHtmlEscaper() {
        return ASCII_HTML_ESCAPER;
    }

    public static Escaper uriEscaper() {
        return CharEscapers.uriEscaper(true);
    }

    public static Escaper uriPathEscaper() {
        return URI_PATH_ESCAPER;
    }

    public static Escaper uriQueryStringEscaper() {
        return URI_QUERY_STRING_ESCAPER;
    }

    public static Escaper uriEscaper(boolean plusForSpace) {
        return plusForSpace ? URI_ESCAPER : URI_ESCAPER_NO_PLUS;
    }

    public static Escaper cppUriEscaper() {
        return CPP_URI_ESCAPER;
    }

    public static CharEscaper javaStringEscaper() {
        return JAVA_STRING_ESCAPER;
    }

    public static CharEscaper javaCharEscaper() {
        return JAVA_CHAR_ESCAPER;
    }

    public static CharEscaper javaStringUnicodeEscaper() {
        return JAVA_STRING_UNICODE_ESCAPER;
    }

    public static CharEscaper pythonEscaper() {
        return PYTHON_ESCAPER;
    }

    public static CharEscaper javascriptEscaper() {
        return JAVASCRIPT_ESCAPER;
    }

    private static CharEscaperBuilder newBasicXmlEscapeBuilder() {
        return new CharEscaperBuilder().addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").addEscapes(new char[]{'\u0000', '\u0001', '\u0002', '\u0003', '\u0004', '\u0005', '\u0006', '\u0007', '\b', '\u000b', '\f', '\u000e', '\u000f', '\u0010', '\u0011', '\u0012', '\u0013', '\u0014', '\u0015', '\u0016', '\u0017', '\u0018', '\u0019', '\u001a', '\u001b', '\u001c', '\u001d', '\u001e', '\u001f'}, "");
    }

    public static CharEscaper fallThrough(CharEscaper primary, CharEscaper secondary) {
        Preconditions.checkNotNull((Object)primary);
        Preconditions.checkNotNull((Object)secondary);
        return new FallThroughCharEscaper(primary, secondary);
    }

    private static class FallThroughCharEscaper
    extends CharEscaper {
        private final CharEscaper primary;
        private final CharEscaper secondary;

        public FallThroughCharEscaper(CharEscaper primary, CharEscaper secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }

        @Override
        protected char[] escape(char c) {
            char[] result = this.primary.escape(c);
            if (result == null) {
                result = this.secondary.escape(c);
            }
            return result;
        }
    }

    private static class HtmlCharEscaper
    extends FastCharEscaper {
        public HtmlCharEscaper(char[][] replacements) {
            super(replacements, '\u0000', '~');
        }

        @Override
        protected char[] escape(char c) {
            int index;
            char[] r;
            if (c < this.replacementLength && (r = this.replacements[c]) != null) {
                return r;
            }
            if (c <= this.safeMax) {
                return null;
            }
            char[] result = new char[index + 2];
            result[0] = 38;
            result[1] = 35;
            result[index + 1] = 59;
            int intValue = c;
            for (index = c < 1000 ? 4 : (c < 10000 ? 5 : 6); index > 1; --index) {
                result[index] = HEX_DIGITS[intValue % 10];
                intValue /= 10;
            }
            return result;
        }
    }

    private static class JavascriptCharEscaper
    extends FastCharEscaper {
        public JavascriptCharEscaper(char[][] replacements) {
            super(replacements, ' ', '~');
        }

        @Override
        protected char[] escape(char c) {
            char[] r;
            if (c < this.replacementLength && (r = this.replacements[c]) != null) {
                return r;
            }
            if (this.safeMin <= c && c <= this.safeMax) {
                return null;
            }
            if (c < '\u0100') {
                r = new char[4];
                r[3] = HEX_DIGITS[c & 0xF];
                c = (char)(c >>> 4);
                r[2] = HEX_DIGITS[c & 0xF];
                r[1] = 120;
                r[0] = 92;
                return r;
            }
            r = new char[6];
            r[5] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[4] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[3] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[2] = HEX_DIGITS[c & 0xF];
            r[1] = 117;
            r[0] = 92;
            return r;
        }
    }

    private static class JavaCharEscaper
    extends FastCharEscaper {
        public JavaCharEscaper(char[][] replacements) {
            super(replacements, ' ', '~');
        }

        @Override
        protected char[] escape(char c) {
            char[] r;
            if (c < this.replacementLength && (r = this.replacements[c]) != null) {
                return r;
            }
            if (this.safeMin <= c && c <= this.safeMax) {
                return null;
            }
            if (c <= '\u00ff') {
                r = new char[4];
                r[0] = 92;
                r[3] = HEX_DIGITS[c & 7];
                c = (char)(c >>> 3);
                r[2] = HEX_DIGITS[c & 7];
                c = (char)(c >>> 3);
                r[1] = HEX_DIGITS[c & 7];
                return r;
            }
            r = new char[6];
            r[0] = 92;
            r[1] = 117;
            r[5] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[4] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[3] = HEX_DIGITS[c & 0xF];
            c = (char)(c >>> 4);
            r[2] = HEX_DIGITS[c & 0xF];
            return r;
        }
    }

    private static abstract class FastCharEscaper
    extends CharEscaper {
        protected final char[][] replacements;
        protected final int replacementLength;
        protected final char safeMin;
        protected final char safeMax;

        public FastCharEscaper(char[][] replacements, char safeMin, char safeMax) {
            this.replacements = replacements;
            this.replacementLength = replacements.length;
            this.safeMin = safeMin;
            this.safeMax = safeMax;
        }

        @Override
        public String escape(String s) {
            int slen = s.length();
            for (int index = 0; index < slen; ++index) {
                char c = s.charAt(index);
                if ((c >= this.replacementLength || this.replacements[c] == null) && c >= this.safeMin && c <= this.safeMax) continue;
                return this.escapeSlow(s, index);
            }
            return s;
        }
    }

    private static class HtmlEscaperHolder {
        private static final CharEscaper HTML_ESCAPER = new HtmlCharEscaper(new CharEscaperBuilder().addEscape('\"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").addEscape('\u00a0', "&nbsp;").addEscape('\u00a1', "&iexcl;").addEscape('\u00a2', "&cent;").addEscape('\u00a3', "&pound;").addEscape('\u00a4', "&curren;").addEscape('\u00a5', "&yen;").addEscape('\u00a6', "&brvbar;").addEscape('\u00a7', "&sect;").addEscape('\u00a8', "&uml;").addEscape('\u00a9', "&copy;").addEscape('\u00aa', "&ordf;").addEscape('\u00ab', "&laquo;").addEscape('\u00ac', "&not;").addEscape('\u00ad', "&shy;").addEscape('\u00ae', "&reg;").addEscape('\u00af', "&macr;").addEscape('\u00b0', "&deg;").addEscape('\u00b1', "&plusmn;").addEscape('\u00b2', "&sup2;").addEscape('\u00b3', "&sup3;").addEscape('\u00b4', "&acute;").addEscape('\u00b5', "&micro;").addEscape('\u00b6', "&para;").addEscape('\u00b7', "&middot;").addEscape('\u00b8', "&cedil;").addEscape('\u00b9', "&sup1;").addEscape('\u00ba', "&ordm;").addEscape('\u00bb', "&raquo;").addEscape('\u00bc', "&frac14;").addEscape('\u00bd', "&frac12;").addEscape('\u00be', "&frac34;").addEscape('\u00bf', "&iquest;").addEscape('\u00c0', "&Agrave;").addEscape('\u00c1', "&Aacute;").addEscape('\u00c2', "&Acirc;").addEscape('\u00c3', "&Atilde;").addEscape('\u00c4', "&Auml;").addEscape('\u00c5', "&Aring;").addEscape('\u00c6', "&AElig;").addEscape('\u00c7', "&Ccedil;").addEscape('\u00c8', "&Egrave;").addEscape('\u00c9', "&Eacute;").addEscape('\u00ca', "&Ecirc;").addEscape('\u00cb', "&Euml;").addEscape('\u00cc', "&Igrave;").addEscape('\u00cd', "&Iacute;").addEscape('\u00ce', "&Icirc;").addEscape('\u00cf', "&Iuml;").addEscape('\u00d0', "&ETH;").addEscape('\u00d1', "&Ntilde;").addEscape('\u00d2', "&Ograve;").addEscape('\u00d3', "&Oacute;").addEscape('\u00d4', "&Ocirc;").addEscape('\u00d5', "&Otilde;").addEscape('\u00d6', "&Ouml;").addEscape('\u00d7', "&times;").addEscape('\u00d8', "&Oslash;").addEscape('\u00d9', "&Ugrave;").addEscape('\u00da', "&Uacute;").addEscape('\u00db', "&Ucirc;").addEscape('\u00dc', "&Uuml;").addEscape('\u00dd', "&Yacute;").addEscape('\u00de', "&THORN;").addEscape('\u00df', "&szlig;").addEscape('\u00e0', "&agrave;").addEscape('\u00e1', "&aacute;").addEscape('\u00e2', "&acirc;").addEscape('\u00e3', "&atilde;").addEscape('\u00e4', "&auml;").addEscape('\u00e5', "&aring;").addEscape('\u00e6', "&aelig;").addEscape('\u00e7', "&ccedil;").addEscape('\u00e8', "&egrave;").addEscape('\u00e9', "&eacute;").addEscape('\u00ea', "&ecirc;").addEscape('\u00eb', "&euml;").addEscape('\u00ec', "&igrave;").addEscape('\u00ed', "&iacute;").addEscape('\u00ee', "&icirc;").addEscape('\u00ef', "&iuml;").addEscape('\u00f0', "&eth;").addEscape('\u00f1', "&ntilde;").addEscape('\u00f2', "&ograve;").addEscape('\u00f3', "&oacute;").addEscape('\u00f4', "&ocirc;").addEscape('\u00f5', "&otilde;").addEscape('\u00f6', "&ouml;").addEscape('\u00f7', "&divide;").addEscape('\u00f8', "&oslash;").addEscape('\u00f9', "&ugrave;").addEscape('\u00fa', "&uacute;").addEscape('\u00fb', "&ucirc;").addEscape('\u00fc', "&uuml;").addEscape('\u00fd', "&yacute;").addEscape('\u00fe', "&thorn;").addEscape('\u00ff', "&yuml;").addEscape('\u0152', "&OElig;").addEscape('\u0153', "&oelig;").addEscape('\u0160', "&Scaron;").addEscape('\u0161', "&scaron;").addEscape('\u0178', "&Yuml;").addEscape('\u0192', "&fnof;").addEscape('\u02c6', "&circ;").addEscape('\u02dc', "&tilde;").addEscape('\u0391', "&Alpha;").addEscape('\u0392', "&Beta;").addEscape('\u0393', "&Gamma;").addEscape('\u0394', "&Delta;").addEscape('\u0395', "&Epsilon;").addEscape('\u0396', "&Zeta;").addEscape('\u0397', "&Eta;").addEscape('\u0398', "&Theta;").addEscape('\u0399', "&Iota;").addEscape('\u039a', "&Kappa;").addEscape('\u039b', "&Lambda;").addEscape('\u039c', "&Mu;").addEscape('\u039d', "&Nu;").addEscape('\u039e', "&Xi;").addEscape('\u039f', "&Omicron;").addEscape('\u03a0', "&Pi;").addEscape('\u03a1', "&Rho;").addEscape('\u03a3', "&Sigma;").addEscape('\u03a4', "&Tau;").addEscape('\u03a5', "&Upsilon;").addEscape('\u03a6', "&Phi;").addEscape('\u03a7', "&Chi;").addEscape('\u03a8', "&Psi;").addEscape('\u03a9', "&Omega;").addEscape('\u03b1', "&alpha;").addEscape('\u03b2', "&beta;").addEscape('\u03b3', "&gamma;").addEscape('\u03b4', "&delta;").addEscape('\u03b5', "&epsilon;").addEscape('\u03b6', "&zeta;").addEscape('\u03b7', "&eta;").addEscape('\u03b8', "&theta;").addEscape('\u03b9', "&iota;").addEscape('\u03ba', "&kappa;").addEscape('\u03bb', "&lambda;").addEscape('\u03bc', "&mu;").addEscape('\u03bd', "&nu;").addEscape('\u03be', "&xi;").addEscape('\u03bf', "&omicron;").addEscape('\u03c0', "&pi;").addEscape('\u03c1', "&rho;").addEscape('\u03c2', "&sigmaf;").addEscape('\u03c3', "&sigma;").addEscape('\u03c4', "&tau;").addEscape('\u03c5', "&upsilon;").addEscape('\u03c6', "&phi;").addEscape('\u03c7', "&chi;").addEscape('\u03c8', "&psi;").addEscape('\u03c9', "&omega;").addEscape('\u03d1', "&thetasym;").addEscape('\u03d2', "&upsih;").addEscape('\u03d6', "&piv;").addEscape('\u2002', "&ensp;").addEscape('\u2003', "&emsp;").addEscape('\u2009', "&thinsp;").addEscape('\u200c', "&zwnj;").addEscape('\u200d', "&zwj;").addEscape('\u200e', "&lrm;").addEscape('\u200f', "&rlm;").addEscape('\u2013', "&ndash;").addEscape('\u2014', "&mdash;").addEscape('\u2018', "&lsquo;").addEscape('\u2019', "&rsquo;").addEscape('\u201a', "&sbquo;").addEscape('\u201c', "&ldquo;").addEscape('\u201d', "&rdquo;").addEscape('\u201e', "&bdquo;").addEscape('\u2020', "&dagger;").addEscape('\u2021', "&Dagger;").addEscape('\u2022', "&bull;").addEscape('\u2026', "&hellip;").addEscape('\u2030', "&permil;").addEscape('\u2032', "&prime;").addEscape('\u2033', "&Prime;").addEscape('\u2039', "&lsaquo;").addEscape('\u203a', "&rsaquo;").addEscape('\u203e', "&oline;").addEscape('\u2044', "&frasl;").addEscape('\u20ac', "&euro;").addEscape('\u2111', "&image;").addEscape('\u2118', "&weierp;").addEscape('\u211c', "&real;").addEscape('\u2122', "&trade;").addEscape('\u2135', "&alefsym;").addEscape('\u2190', "&larr;").addEscape('\u2191', "&uarr;").addEscape('\u2192', "&rarr;").addEscape('\u2193', "&darr;").addEscape('\u2194', "&harr;").addEscape('\u21b5', "&crarr;").addEscape('\u21d0', "&lArr;").addEscape('\u21d1', "&uArr;").addEscape('\u21d2', "&rArr;").addEscape('\u21d3', "&dArr;").addEscape('\u21d4', "&hArr;").addEscape('\u2200', "&forall;").addEscape('\u2202', "&part;").addEscape('\u2203', "&exist;").addEscape('\u2205', "&empty;").addEscape('\u2207', "&nabla;").addEscape('\u2208', "&isin;").addEscape('\u2209', "&notin;").addEscape('\u220b', "&ni;").addEscape('\u220f', "&prod;").addEscape('\u2211', "&sum;").addEscape('\u2212', "&minus;").addEscape('\u2217', "&lowast;").addEscape('\u221a', "&radic;").addEscape('\u221d', "&prop;").addEscape('\u221e', "&infin;").addEscape('\u2220', "&ang;").addEscape('\u2227', "&and;").addEscape('\u2228', "&or;").addEscape('\u2229', "&cap;").addEscape('\u222a', "&cup;").addEscape('\u222b', "&int;").addEscape('\u2234', "&there4;").addEscape('\u223c', "&sim;").addEscape('\u2245', "&cong;").addEscape('\u2248', "&asymp;").addEscape('\u2260', "&ne;").addEscape('\u2261', "&equiv;").addEscape('\u2264', "&le;").addEscape('\u2265', "&ge;").addEscape('\u2282', "&sub;").addEscape('\u2283', "&sup;").addEscape('\u2284', "&nsub;").addEscape('\u2286', "&sube;").addEscape('\u2287', "&supe;").addEscape('\u2295', "&oplus;").addEscape('\u2297', "&otimes;").addEscape('\u22a5', "&perp;").addEscape('\u22c5', "&sdot;").addEscape('\u2308', "&lceil;").addEscape('\u2309', "&rceil;").addEscape('\u230a', "&lfloor;").addEscape('\u230b', "&rfloor;").addEscape('\u2329', "&lang;").addEscape('\u232a', "&rang;").addEscape('\u25ca', "&loz;").addEscape('\u2660', "&spades;").addEscape('\u2663', "&clubs;").addEscape('\u2665', "&hearts;").addEscape('\u2666', "&diams;").toArray());

        private HtmlEscaperHolder() {
        }
    }
}

