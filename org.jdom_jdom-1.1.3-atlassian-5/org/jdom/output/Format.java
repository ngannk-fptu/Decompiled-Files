/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.output;

import java.lang.reflect.Method;
import org.jdom.Verifier;
import org.jdom.output.EscapeStrategy;

public class Format
implements Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Format.java,v $ $Revision: 1.14 $ $Date: 2009/07/23 05:54:23 $ $Name:  $";
    private static final String STANDARD_INDENT = "  ";
    private static final String STANDARD_LINE_SEPARATOR = "\r\n";
    private static final String STANDARD_ENCODING = "UTF-8";
    String indent = null;
    String lineSeparator = "\r\n";
    String encoding = "UTF-8";
    boolean omitDeclaration = false;
    boolean omitEncoding = false;
    boolean expandEmptyElements = false;
    boolean ignoreTrAXEscapingPIs = false;
    TextMode mode = TextMode.PRESERVE;
    EscapeStrategy escapeStrategy = new DefaultEscapeStrategy(this.encoding);

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

    private Format() {
    }

    public Format setEscapeStrategy(EscapeStrategy strategy) {
        this.escapeStrategy = strategy;
        return this;
    }

    public EscapeStrategy getEscapeStrategy() {
        return this.escapeStrategy;
    }

    public Format setLineSeparator(String separator) {
        this.lineSeparator = separator;
        return this;
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
        this.escapeStrategy = new DefaultEscapeStrategy(encoding);
        return this;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public Object clone() {
        Format format = null;
        try {
            format = (Format)super.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return format;
    }

    public static class TextMode {
        public static final TextMode PRESERVE = new TextMode("PRESERVE");
        public static final TextMode TRIM = new TextMode("TRIM");
        public static final TextMode NORMALIZE = new TextMode("NORMALIZE");
        public static final TextMode TRIM_FULL_WHITE = new TextMode("TRIM_FULL_WHITE");
        private final String name;

        private TextMode(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }

    class DefaultEscapeStrategy
    implements EscapeStrategy {
        private int bits;
        Object encoder;
        Method canEncode;

        public DefaultEscapeStrategy(String encoding) {
            if (Format.STANDARD_ENCODING.equalsIgnoreCase(encoding) || "UTF-16".equalsIgnoreCase(encoding)) {
                this.bits = 16;
            } else if ("ISO-8859-1".equalsIgnoreCase(encoding) || "Latin1".equalsIgnoreCase(encoding)) {
                this.bits = 8;
            } else if ("US-ASCII".equalsIgnoreCase(encoding) || "ASCII".equalsIgnoreCase(encoding)) {
                this.bits = 7;
            } else {
                this.bits = 0;
                try {
                    Class<?> charsetClass = Class.forName("java.nio.charset.Charset");
                    Class<?> encoderClass = Class.forName("java.nio.charset.CharsetEncoder");
                    Method forName = charsetClass.getMethod("forName", String.class);
                    Object charsetObj = forName.invoke(null, encoding);
                    Method newEncoder = charsetClass.getMethod("newEncoder", null);
                    this.encoder = newEncoder.invoke(charsetObj, null);
                    this.canEncode = encoderClass.getMethod("canEncode", Character.TYPE);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }

        @Override
        public boolean shouldEscape(char ch) {
            if (this.bits == 16) {
                return Verifier.isHighSurrogate(ch);
            }
            if (this.bits == 8) {
                return ch > '\u00ff';
            }
            if (this.bits == 7) {
                return ch > '\u007f';
            }
            if (Verifier.isHighSurrogate(ch)) {
                return true;
            }
            if (this.canEncode != null && this.encoder != null) {
                try {
                    Boolean val = (Boolean)this.canEncode.invoke(this.encoder, new Character(ch));
                    return val == false;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return false;
        }
    }
}

