/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.shared.restricted;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.internal.base.Escaper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class EscapingConventions {
    public static final String INNOCUOUS_OUTPUT = "zSoyz";
    public static final Pattern HTML_TAG_CONTENT = Pattern.compile("<(?:!|/?([a-zA-Z][a-zA-Z0-9:\\-]*))(?:[^>'\"]|\"[^\"]*\"|'[^']*')*>");

    public static Iterable<CrossLanguageStringXform> getAllEscapers() {
        return ImmutableList.of((Object)EscapeHtml.INSTANCE, (Object)NormalizeHtml.INSTANCE, (Object)EscapeHtmlNospace.INSTANCE, (Object)NormalizeHtmlNospace.INSTANCE, (Object)EscapeJsString.INSTANCE, (Object)EscapeJsRegex.INSTANCE, (Object)EscapeCssString.INSTANCE, (Object)FilterCssValue.INSTANCE, (Object)EscapeUri.INSTANCE, (Object)NormalizeUri.INSTANCE, (Object)FilterNormalizeUri.INSTANCE, (Object)FilterImageDataUri.INSTANCE, (Object[])new CrossLanguageStringXform[]{FilterHtmlAttributes.INSTANCE, FilterHtmlElementName.INSTANCE});
    }

    private static String toFullWidth(String ascii) {
        int numChars = ascii.length();
        StringBuilder sb = new StringBuilder(ascii);
        for (int i = 0; i < numChars; ++i) {
            char ch = ascii.charAt(i);
            if (ch >= '\u0080') continue;
            sb.setCharAt(i, (char)(ch + 65280 - 32));
        }
        return sb.toString();
    }

    public static final class FilterHtmlElementName
    extends CrossLanguageStringXform {
        public static final FilterHtmlElementName INSTANCE = new FilterHtmlElementName();

        private FilterHtmlElementName() {
            super(Pattern.compile("^(?!script|style|title|textarea|xmp|no)[a-z0-9_$:-]*\\z", 2), null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return ImmutableList.of();
        }
    }

    public static final class FilterHtmlAttributes
    extends CrossLanguageStringXform {
        public static final FilterHtmlAttributes INSTANCE = new FilterHtmlAttributes();

        private FilterHtmlAttributes() {
            super(Pattern.compile("^(?!style|on|action|archive|background|cite|classid|codebase|data|dsync|href|longdesc|src|usemap)(?:[a-z0-9_$:-]*)\\z", 2), null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return ImmutableList.of();
        }
    }

    public static final class EscapeUri
    extends CrossLanguageStringXform {
        public static final EscapeUri INSTANCE = new EscapeUri();

        private EscapeUri() {
            super(null, "%");
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new UriEscapeListBuilder().escapeAllInRangeExcept(0, 128, '-', '.', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '~').build();
        }

        @Override
        public List<String> getLangFunctionNames(EscapingLanguage language) {
            if (language == EscapingLanguage.JAVASCRIPT) {
                return ImmutableList.of((Object)"goog.string.urlEncode", (Object)"encodeURIComponent");
            }
            return super.getLangFunctionNames(language);
        }
    }

    public static final class FilterImageDataUri
    extends CrossLanguageStringXform {
        public static final FilterImageDataUri INSTANCE = new FilterImageDataUri();

        private FilterImageDataUri() {
            super(Pattern.compile("^data:image/(?:bmp|gif|jpe?g|png|tiff|webp);base64,[a-z0-9+/]+=*\\z", 2), null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return ImmutableList.of();
        }

        @Override
        public String getInnocuousOutput() {
            return "data:image/gif;base64,zSoyz";
        }
    }

    public static final class FilterNormalizeUri
    extends CrossLanguageStringXform {
        public static final FilterNormalizeUri INSTANCE = new FilterNormalizeUri();

        private FilterNormalizeUri() {
            super(Pattern.compile("^(?:(?:https?|mailto):|[^&:\\/?#]*(?:[\\/?#]|\\z))", 2), null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return NormalizeUri.INSTANCE.defineEscapes();
        }

        @Override
        public String getInnocuousOutput() {
            return "#zSoyz";
        }
    }

    public static final class NormalizeUri
    extends CrossLanguageStringXform {
        public static final NormalizeUri INSTANCE = new NormalizeUri();

        private NormalizeUri() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new UriEscapeListBuilder().escapeAll("\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007").escapeAll("\b\t\n\u000b\f\r\u000e\u000f").escapeAll("\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017").escapeAll("\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f").escape('\u007f').escapeAll(" (){}\"'\\<>").escapeAll("\u0085\u00a0\u2028\u2029").escapeAll(EscapingConventions.toFullWidth(":/?#[]@!$&'()*+,;=")).build();
        }
    }

    private static final class UriEscapeListBuilder
    extends EscapeListBuilder {
        private UriEscapeListBuilder() {
        }

        @Override
        String getNumericEscapeFor(char plainText) {
            byte[] bytes = Character.toString(plainText).getBytes(Charsets.UTF_8);
            int numBytes = bytes.length;
            StringBuilder sb = new StringBuilder(numBytes * 3);
            for (int i = 0; i < numBytes; ++i) {
                sb.append(String.format("%%%02X", bytes[i]));
            }
            return sb.toString();
        }
    }

    public static final class FilterCssValue
    extends CrossLanguageStringXform {
        public static final Pattern CSS_WORD = Pattern.compile("^(?!-*(?:expression|(?:moz-)?binding))(?:[.#]?-?(?:[_a-z0-9-]+)(?:-[_a-z0-9-]+)*-?|-?(?:[0-9]+(?:\\.[0-9]*)?|\\.[0-9]+)(?:[a-z]{1,2}|%)?|!important|)\\z", 2);
        public static final FilterCssValue INSTANCE = new FilterCssValue();

        private FilterCssValue() {
            super(CSS_WORD, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return ImmutableList.of();
        }
    }

    public static final class EscapeCssString
    extends CrossLanguageStringXform {
        public static final EscapeCssString INSTANCE = new EscapeCssString();

        private EscapeCssString() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new CssEscapeListBuilder().escapeAll("\u0000\b\t\n\u000b\f\r\u0085\u00a0\u2028\u2029\"'\\<>&{};:()@/=*").build();
        }
    }

    private static final class CssEscapeListBuilder
    extends EscapeListBuilder {
        private CssEscapeListBuilder() {
        }

        @Override
        String getNumericEscapeFor(char plainText) {
            return String.format("\\%x ", plainText);
        }
    }

    public static final class EscapeJsRegex
    extends CrossLanguageStringXform {
        public static final EscapeJsRegex INSTANCE = new EscapeJsRegex();

        private EscapeJsRegex() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new JsEscapeListBuilder().escape('\u0000').escape('\b').escape('\t', "\\t").escape('\n', "\\n").escape('\u000b').escape('\f', "\\f").escape('\r', "\\r").escape('\\', "\\\\").escapeAll("\u2028\u2029").escape('\u0085').escape('\"').escape('\'').escape('/', "\\/").escapeAll("<>&=").escapeAll("$()*+-.:?[]^{|},").build();
        }
    }

    public static final class EscapeJsString
    extends CrossLanguageStringXform {
        public static final EscapeJsString INSTANCE = new EscapeJsString();

        private EscapeJsString() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new JsEscapeListBuilder().escape('\u0000').escape('\b').escape('\t', "\\t").escape('\n', "\\n").escape('\u000b').escape('\f', "\\f").escape('\r', "\\r").escape('\\', "\\\\").escape('\"').escape('\'').escape('/', "\\/").escapeAll("\u2028\u2029").escape('\u0085').escapeAll("<>&=").build();
        }
    }

    private static final class JsEscapeListBuilder
    extends EscapeListBuilder {
        private JsEscapeListBuilder() {
        }

        @Override
        String getNumericEscapeFor(char plainText) {
            return String.format(plainText < '\u0100' ? "\\x%02x" : "\\u%04x", plainText);
        }
    }

    public static final class NormalizeHtmlNospace
    extends CrossLanguageStringXform {
        public static final NormalizeHtmlNospace INSTANCE = new NormalizeHtmlNospace();

        private NormalizeHtmlNospace() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            ImmutableList.Builder escapes = ImmutableList.builder();
            for (Escape esc : EscapeHtmlNospace.INSTANCE.getEscapes()) {
                if (esc.plainText == '&') continue;
                escapes.add((Object)esc);
            }
            return escapes.build();
        }
    }

    public static final class EscapeHtmlNospace
    extends CrossLanguageStringXform {
        public static final EscapeHtmlNospace INSTANCE = new EscapeHtmlNospace();

        private EscapeHtmlNospace() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new HtmlEscapeListBuilder().escape('&', "&amp;").escape('<', "&lt;").escape('>', "&gt;").escape('\"', "&quot;").escapeAll("\u0000\t\n\u000b\f\r '-/=`\u0085\u00a0\u2028\u2029").build();
        }
    }

    public static final class NormalizeHtml
    extends CrossLanguageStringXform {
        public static final NormalizeHtml INSTANCE = new NormalizeHtml();

        private NormalizeHtml() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            ImmutableList.Builder escapes = ImmutableList.builder();
            for (Escape esc : EscapeHtml.INSTANCE.getEscapes()) {
                if (esc.plainText == '&') continue;
                escapes.add((Object)esc);
            }
            return escapes.build();
        }
    }

    public static final class EscapeHtml
    extends CrossLanguageStringXform {
        public static final EscapeHtml INSTANCE = new EscapeHtml();

        private EscapeHtml() {
            super(null, null);
        }

        @Override
        protected ImmutableList<Escape> defineEscapes() {
            return new HtmlEscapeListBuilder().escape('&', "&amp;").escape('<', "&lt;").escape('>', "&gt;").escape('\"', "&quot;").escapeAll("\u0000'").build();
        }
    }

    private static final class HtmlEscapeListBuilder
    extends EscapeListBuilder {
        private HtmlEscapeListBuilder() {
        }

        @Override
        String getNumericEscapeFor(char plainText) {
            return "&#" + plainText + ";";
        }
    }

    private static abstract class EscapeListBuilder {
        private final List<Escape> escapes = Lists.newArrayList();

        private EscapeListBuilder() {
        }

        abstract String getNumericEscapeFor(char var1);

        final EscapeListBuilder escape(char plainText, String escaped) {
            this.escapes.add(new Escape(plainText, escaped));
            return this;
        }

        final EscapeListBuilder escape(char plainText) {
            this.escapes.add(new Escape(plainText, this.getNumericEscapeFor(plainText)));
            return this;
        }

        final EscapeListBuilder escapeAll(String plainTextCodeUnits) {
            int numCodeUnits = plainTextCodeUnits.length();
            for (int i = 0; i < numCodeUnits; ++i) {
                this.escape(plainTextCodeUnits.charAt(i));
            }
            return this;
        }

        final EscapeListBuilder escapeAllInRangeExcept(int startInclusive, int endExclusive, char ... notEscaped) {
            notEscaped = (char[])notEscaped.clone();
            Arrays.sort(notEscaped);
            int k = 0;
            int numNotEscaped = notEscaped.length;
            for (int i = startInclusive; i < endExclusive; ++i) {
                while (k < numNotEscaped && notEscaped[k] < i) {
                    ++k;
                }
                if (k < numNotEscaped && notEscaped[k] == i) continue;
                this.escape((char)i);
            }
            return this;
        }

        final ImmutableList<Escape> build() {
            Collections.sort(this.escapes);
            return ImmutableList.copyOf(this.escapes);
        }
    }

    public static abstract class CrossLanguageStringXform
    implements Escaper {
        private final String directiveName;
        @Nullable
        private final Pattern valueFilter;
        private final ImmutableList<Escape> escapes;
        private final String[] escapesByCodeUnit;
        private final char[] nonAsciiCodeUnits;
        private final String[] nonAsciiEscapes;
        @Nullable
        private final String nonAsciiPrefix;
        private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        protected CrossLanguageStringXform(@Nullable Pattern valueFilter, @Nullable String nonAsciiPrefix) {
            int numAsciiEscapes;
            String simpleName = this.getClass().getSimpleName();
            this.directiveName = "|" + Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
            this.valueFilter = valueFilter;
            this.escapes = this.defineEscapes();
            int numEscapes = this.escapes.size();
            for (numAsciiEscapes = this.escapes.size(); numAsciiEscapes > 0 && ((Escape)this.escapes.get(numAsciiEscapes - 1)).plainText >= '\u0080'; --numAsciiEscapes) {
            }
            if (numAsciiEscapes != 0) {
                this.escapesByCodeUnit = new String[((Escape)this.escapes.get(numAsciiEscapes - 1)).plainText + '\u0001'];
                for (Escape escape : this.escapes.subList(0, numAsciiEscapes)) {
                    this.escapesByCodeUnit[((Escape)escape).plainText] = escape.escaped;
                }
            } else {
                this.escapesByCodeUnit = new String[0];
            }
            if (numEscapes != numAsciiEscapes) {
                int numNonAsciiEscapes = numEscapes - numAsciiEscapes;
                this.nonAsciiCodeUnits = new char[numNonAsciiEscapes];
                this.nonAsciiEscapes = new String[numNonAsciiEscapes];
                for (int i = 0; i < numNonAsciiEscapes; ++i) {
                    Escape esc = (Escape)this.escapes.get(numAsciiEscapes + i);
                    this.nonAsciiCodeUnits[i] = esc.plainText;
                    this.nonAsciiEscapes[i] = esc.escaped;
                }
            } else {
                this.nonAsciiCodeUnits = new char[0];
                this.nonAsciiEscapes = new String[0];
            }
            this.nonAsciiPrefix = nonAsciiPrefix;
        }

        protected abstract ImmutableList<Escape> defineEscapes();

        public String getDirectiveName() {
            return this.directiveName;
        }

        @Nullable
        public final String getNonAsciiPrefix() {
            return this.nonAsciiPrefix;
        }

        @Nullable
        public final Pattern getValueFilter() {
            return this.valueFilter;
        }

        public final ImmutableList<Escape> getEscapes() {
            return this.escapes;
        }

        public List<String> getLangFunctionNames(EscapingLanguage language) {
            return ImmutableList.of();
        }

        public String getInnocuousOutput() {
            return EscapingConventions.INNOCUOUS_OUTPUT;
        }

        @Override
        public final String escape(String string) {
            StringBuilder sb = this.maybeEscapeOnto(string, null);
            return sb != null ? sb.toString() : string;
        }

        @Override
        public final Appendable escape(final Appendable out) {
            return new Appendable(){

                @Override
                public Appendable append(CharSequence csq) throws IOException {
                    this.maybeEscapeOnto(csq, out, 0, csq.length());
                    return this;
                }

                @Override
                public Appendable append(CharSequence csq, int start, int end) throws IOException {
                    this.maybeEscapeOnto(csq, out, start, end);
                    return this;
                }

                @Override
                public Appendable append(char c) throws IOException {
                    if (c < escapesByCodeUnit.length) {
                        String esc = escapesByCodeUnit[c];
                        if (esc != null) {
                            out.append(esc);
                            return this;
                        }
                    } else if (c >= '\u0080') {
                        int index = Arrays.binarySearch(nonAsciiCodeUnits, c);
                        if (index >= 0) {
                            out.append(nonAsciiEscapes[index]);
                            return this;
                        }
                        if (nonAsciiPrefix != null) {
                            this.escapeUsingPrefix(c, out);
                            return this;
                        }
                    }
                    out.append(c);
                    return this;
                }
            };
        }

        @Nullable
        private StringBuilder maybeEscapeOnto(CharSequence s, @Nullable StringBuilder out) {
            try {
                return (StringBuilder)this.maybeEscapeOnto(s, out, 0, s.length());
            }
            catch (IOException ex) {
                throw new AssertionError((Object)ex);
            }
        }

        @Nullable
        private Appendable maybeEscapeOnto(CharSequence s, @Nullable Appendable out, int start, int end) throws IOException {
            int pos = start;
            for (int i = start; i < end; ++i) {
                char c = s.charAt(i);
                if (c < this.escapesByCodeUnit.length) {
                    String esc = this.escapesByCodeUnit[c];
                    if (esc == null) continue;
                    if (out == null) {
                        out = new StringBuilder(end - start + 32);
                    }
                    out.append(s, pos, i).append(esc);
                    pos = i + 1;
                    continue;
                }
                if (c < '\u0080') continue;
                int index = Arrays.binarySearch(this.nonAsciiCodeUnits, c);
                if (index >= 0) {
                    if (out == null) {
                        out = new StringBuilder(end - start + 32);
                    }
                    out.append(s, pos, i).append(this.nonAsciiEscapes[index]);
                    pos = i + 1;
                    continue;
                }
                if (this.nonAsciiPrefix == null) continue;
                if (out == null) {
                    out = new StringBuilder(end - start + 32);
                }
                out.append(s, pos, i);
                this.escapeUsingPrefix(c, out);
                pos = i + 1;
            }
            if (out != null) {
                out.append(s, pos, end);
            }
            return out;
        }

        private void escapeUsingPrefix(char c, Appendable out) throws IOException {
            if ("%".equals(this.nonAsciiPrefix)) {
                if (c < '\u0800') {
                    out.append('%');
                    this.appendHexPair(c >>> 6 & 0x1F | 0xC0, out);
                } else {
                    out.append('%');
                    this.appendHexPair(c >>> 12 & 0xF | 0xE0, out);
                    out.append('%');
                    this.appendHexPair(c >>> 6 & 0x3F | 0x80, out);
                }
                out.append('%');
                this.appendHexPair(c & 0x3F | 0x80, out);
            } else {
                out.append(this.nonAsciiPrefix);
                this.appendHexPair(c >>> 8 & 0xFF, out);
                this.appendHexPair(c & 0xFF, out);
                if ("\\".equals(this.nonAsciiPrefix)) {
                    out.append(' ');
                }
            }
        }

        private void appendHexPair(int b, Appendable out) throws IOException {
            out.append(HEX_DIGITS[b >>> 4]);
            out.append(HEX_DIGITS[b & 0xF]);
        }
    }

    public static final class Escape
    implements Comparable<Escape> {
        private final char plainText;
        private final String escaped;

        public Escape(char plainText, String escaped) {
            this.plainText = plainText;
            this.escaped = escaped;
        }

        public char getPlainText() {
            return this.plainText;
        }

        public String getEscaped() {
            return this.escaped;
        }

        @Override
        public int compareTo(Escape b) {
            return this.plainText - b.plainText;
        }
    }

    public static enum EscapingLanguage {
        JAVASCRIPT;

    }
}

