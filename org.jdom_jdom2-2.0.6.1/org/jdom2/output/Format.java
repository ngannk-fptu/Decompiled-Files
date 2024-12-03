/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Locale;
import org.jdom2.IllegalDataException;
import org.jdom2.Verifier;
import org.jdom2.output.EscapeStrategy;
import org.jdom2.output.LineSeparator;

public class Format
implements Cloneable {
    private static final EscapeStrategy UTFEscapeStrategy = new EscapeStrategyUTF();
    private static final EscapeStrategy Bits8EscapeStrategy = new EscapeStrategy8Bits();
    private static final EscapeStrategy Bits7EscapeStrategy = new EscapeStrategy7Bits();
    private static final EscapeStrategy DefaultEscapeStrategy = new EscapeStrategy(){

        public boolean shouldEscape(char ch) {
            return Verifier.isHighSurrogate(ch);
        }
    };
    private static final String STANDARD_INDENT = "  ";
    private static final String STANDARD_LINE_SEPARATOR = LineSeparator.DEFAULT.value();
    private static final String STANDARD_ENCODING = "UTF-8";
    String indent = null;
    String lineSeparator = STANDARD_LINE_SEPARATOR;
    String encoding = "UTF-8";
    boolean omitDeclaration = false;
    boolean omitEncoding = false;
    boolean specifiedAttributesOnly = false;
    boolean expandEmptyElements = false;
    boolean ignoreTrAXEscapingPIs = false;
    TextMode mode = TextMode.PRESERVE;
    EscapeStrategy escapeStrategy = DefaultEscapeStrategy;

    public static Format getRawFormat() {
        return new Format();
    }

    public static Format getPrettyFormat() {
        Format f = new Format();
        f.setIndent(STANDARD_INDENT);
        f.setTextMode(TextMode.TRIM);
        return f;
    }

    public static Format getCompactFormat() {
        Format f = new Format();
        f.setTextMode(TextMode.NORMALIZE);
        return f;
    }

    public static final String compact(String str) {
        int left;
        int right = str.length() - 1;
        for (left = 0; left <= right && Verifier.isXMLWhitespace(str.charAt(left)); ++left) {
        }
        while (right > left && Verifier.isXMLWhitespace(str.charAt(right))) {
            --right;
        }
        if (left > right) {
            return "";
        }
        boolean space = true;
        StringBuilder buffer = new StringBuilder(right - left + 1);
        while (left <= right) {
            char c = str.charAt(left);
            if (Verifier.isXMLWhitespace(c)) {
                if (space) {
                    buffer.append(' ');
                    space = false;
                }
            } else {
                buffer.append(c);
                space = true;
            }
            ++left;
        }
        return buffer.toString();
    }

    public static final String trimRight(String str) {
        int right;
        for (right = str.length() - 1; right >= 0 && Verifier.isXMLWhitespace(str.charAt(right)); --right) {
        }
        if (right < 0) {
            return "";
        }
        return str.substring(0, right + 1);
    }

    public static final String trimLeft(String str) {
        int left;
        int right = str.length();
        for (left = 0; left < right && Verifier.isXMLWhitespace(str.charAt(left)); ++left) {
        }
        if (left >= right) {
            return "";
        }
        return str.substring(left);
    }

    public static final String trimBoth(String str) {
        int left;
        int right;
        for (right = str.length() - 1; right > 0 && Verifier.isXMLWhitespace(str.charAt(right)); --right) {
        }
        for (left = 0; left <= right && Verifier.isXMLWhitespace(str.charAt(left)); ++left) {
        }
        if (left > right) {
            return "";
        }
        return str.substring(left, right + 1);
    }

    public static final String escapeAttribute(EscapeStrategy strategy, String value) {
        char ch;
        int idx;
        int len = value.length();
        for (idx = 0; idx < len && (ch = value.charAt(idx)) != '<' && ch != '>' && ch != '&' && ch != '\r' && ch != '\n' && ch != '\"' && ch != '\t' && !strategy.shouldEscape(ch); ++idx) {
        }
        if (idx == len) {
            return value;
        }
        char highsurrogate = '\u0000';
        StringBuilder sb = new StringBuilder(len + 5);
        sb.append(value, 0, idx);
        block10: while (idx < len) {
            char ch2 = value.charAt(idx++);
            if (highsurrogate > '\u0000') {
                if (!Verifier.isLowSurrogate(ch2)) {
                    throw new IllegalDataException("Could not decode surrogate pair 0x" + Integer.toHexString(highsurrogate) + " / 0x" + Integer.toHexString(ch2));
                }
                int chp = Verifier.decodeSurrogatePair(highsurrogate, ch2);
                sb.append("&#x");
                sb.append(Integer.toHexString(chp));
                sb.append(';');
                highsurrogate = '\u0000';
                continue;
            }
            switch (ch2) {
                case '<': {
                    sb.append("&lt;");
                    continue block10;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block10;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block10;
                }
                case '\r': {
                    sb.append("&#xD;");
                    continue block10;
                }
                case '\"': {
                    sb.append("&quot;");
                    continue block10;
                }
                case '\t': {
                    sb.append("&#x9;");
                    continue block10;
                }
                case '\n': {
                    sb.append("&#xA;");
                    continue block10;
                }
            }
            if (strategy.shouldEscape(ch2)) {
                if (Verifier.isHighSurrogate(ch2)) {
                    highsurrogate = ch2;
                    continue;
                }
                sb.append("&#x");
                sb.append(Integer.toHexString(ch2));
                sb.append(';');
                continue;
            }
            sb.append(ch2);
        }
        if (highsurrogate > '\u0000') {
            throw new IllegalDataException("Surrogate pair 0x" + Integer.toHexString(highsurrogate) + "truncated");
        }
        return sb.toString();
    }

    public static final String escapeText(EscapeStrategy strategy, String eol, String value) {
        char ch;
        int idx;
        int right = value.length();
        for (idx = 0; idx < right && (ch = value.charAt(idx)) != '<' && ch != '>' && ch != '&' && ch != '\r' && ch != '\n' && !strategy.shouldEscape(ch); ++idx) {
        }
        if (idx == right) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        if (idx > 0) {
            sb.append(value, 0, idx);
        }
        char highsurrogate = '\u0000';
        block8: while (idx < right) {
            char ch2 = value.charAt(idx++);
            if (highsurrogate > '\u0000') {
                if (!Verifier.isLowSurrogate(ch2)) {
                    throw new IllegalDataException("Could not decode surrogate pair 0x" + Integer.toHexString(highsurrogate) + " / 0x" + Integer.toHexString(ch2));
                }
                int chp = Verifier.decodeSurrogatePair(highsurrogate, ch2);
                sb.append("&#x" + Integer.toHexString(chp) + ";");
                highsurrogate = '\u0000';
                continue;
            }
            switch (ch2) {
                case '<': {
                    sb.append("&lt;");
                    continue block8;
                }
                case '>': {
                    sb.append("&gt;");
                    continue block8;
                }
                case '&': {
                    sb.append("&amp;");
                    continue block8;
                }
                case '\r': {
                    sb.append("&#xD;");
                    continue block8;
                }
                case '\n': {
                    if (eol != null) {
                        sb.append(eol);
                        continue block8;
                    }
                    sb.append('\n');
                    continue block8;
                }
            }
            if (strategy.shouldEscape(ch2)) {
                if (Verifier.isHighSurrogate(ch2)) {
                    highsurrogate = ch2;
                    continue;
                }
                sb.append("&#x" + Integer.toHexString(ch2) + ";");
                continue;
            }
            sb.append(ch2);
        }
        if (highsurrogate > '\u0000') {
            throw new IllegalDataException("Surrogate pair 0x" + Integer.toHexString(highsurrogate) + "truncated");
        }
        return sb.toString();
    }

    private static final EscapeStrategy chooseStrategy(String encoding) {
        if (STANDARD_ENCODING.equalsIgnoreCase(encoding) || "UTF-16".equalsIgnoreCase(encoding)) {
            return UTFEscapeStrategy;
        }
        if (encoding.toUpperCase(Locale.ENGLISH).startsWith("ISO-8859-") || "Latin1".equalsIgnoreCase(encoding)) {
            return Bits8EscapeStrategy;
        }
        if ("US-ASCII".equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding)) {
            return Bits7EscapeStrategy;
        }
        try {
            CharsetEncoder cse = Charset.forName(encoding).newEncoder();
            return new DefaultCharsetEscapeStrategy(cse);
        }
        catch (Exception exception) {
            return DefaultEscapeStrategy;
        }
    }

    private Format() {
        this.setEncoding(STANDARD_ENCODING);
    }

    public Format setEscapeStrategy(EscapeStrategy strategy) {
        this.escapeStrategy = strategy;
        return this;
    }

    public EscapeStrategy getEscapeStrategy() {
        return this.escapeStrategy;
    }

    public Format setLineSeparator(String separator) {
        this.lineSeparator = "".equals(separator) ? null : separator;
        return this;
    }

    public Format setLineSeparator(LineSeparator separator) {
        return this.setLineSeparator(separator == null ? STANDARD_LINE_SEPARATOR : separator.value());
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public Format setOmitEncoding(boolean omitEncoding) {
        this.omitEncoding = omitEncoding;
        return this;
    }

    public boolean getOmitEncoding() {
        return this.omitEncoding;
    }

    public Format setOmitDeclaration(boolean omitDeclaration) {
        this.omitDeclaration = omitDeclaration;
        return this;
    }

    public boolean getOmitDeclaration() {
        return this.omitDeclaration;
    }

    public Format setExpandEmptyElements(boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
        return this;
    }

    public boolean getExpandEmptyElements() {
        return this.expandEmptyElements;
    }

    public void setIgnoreTrAXEscapingPIs(boolean ignoreTrAXEscapingPIs) {
        this.ignoreTrAXEscapingPIs = ignoreTrAXEscapingPIs;
    }

    public boolean getIgnoreTrAXEscapingPIs() {
        return this.ignoreTrAXEscapingPIs;
    }

    public Format setTextMode(TextMode mode) {
        this.mode = mode;
        return this;
    }

    public TextMode getTextMode() {
        return this.mode;
    }

    public Format setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public String getIndent() {
        return this.indent;
    }

    public Format setEncoding(String encoding) {
        this.encoding = encoding;
        this.escapeStrategy = Format.chooseStrategy(encoding);
        return this;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public boolean isSpecifiedAttributesOnly() {
        return this.specifiedAttributesOnly;
    }

    public void setSpecifiedAttributesOnly(boolean specifiedAttributesOnly) {
        this.specifiedAttributesOnly = specifiedAttributesOnly;
    }

    public Format clone() {
        Format format = null;
        try {
            format = (Format)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return format;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TextMode {
        PRESERVE,
        TRIM,
        NORMALIZE,
        TRIM_FULL_WHITE;

    }

    private static final class DefaultCharsetEscapeStrategy
    implements EscapeStrategy {
        private final CharsetEncoder encoder;

        public DefaultCharsetEscapeStrategy(CharsetEncoder cse) {
            this.encoder = cse;
        }

        public boolean shouldEscape(char ch) {
            if (Verifier.isHighSurrogate(ch)) {
                return true;
            }
            return !this.encoder.canEncode(ch);
        }
    }

    private static final class EscapeStrategy7Bits
    implements EscapeStrategy {
        private EscapeStrategy7Bits() {
        }

        public boolean shouldEscape(char ch) {
            return ch >>> 7 != 0;
        }
    }

    private static final class EscapeStrategy8Bits
    implements EscapeStrategy {
        private EscapeStrategy8Bits() {
        }

        public boolean shouldEscape(char ch) {
            return ch >>> 8 != 0;
        }
    }

    private static final class EscapeStrategyUTF
    implements EscapeStrategy {
        private EscapeStrategyUTF() {
        }

        public final boolean shouldEscape(char ch) {
            return Verifier.isHighSurrogate(ch);
        }
    }
}

