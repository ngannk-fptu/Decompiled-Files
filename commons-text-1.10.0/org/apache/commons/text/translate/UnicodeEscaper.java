/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.text.translate.CodePointTranslator;

public class UnicodeEscaper
extends CodePointTranslator {
    private final int below;
    private final int above;
    private final boolean between;

    public static UnicodeEscaper above(int codePoint) {
        return UnicodeEscaper.outsideOf(0, codePoint);
    }

    public static UnicodeEscaper below(int codePoint) {
        return UnicodeEscaper.outsideOf(codePoint, Integer.MAX_VALUE);
    }

    public static UnicodeEscaper between(int codePointLow, int codePointHigh) {
        return new UnicodeEscaper(codePointLow, codePointHigh, true);
    }

    public static UnicodeEscaper outsideOf(int codePointLow, int codePointHigh) {
        return new UnicodeEscaper(codePointLow, codePointHigh, false);
    }

    public UnicodeEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    protected UnicodeEscaper(int below, int above, boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }

    protected String toUtf16Escape(int codePoint) {
        return "\\u" + UnicodeEscaper.hex(codePoint);
    }

    @Override
    public boolean translate(int codePoint, Writer writer) throws IOException {
        if (this.between ? codePoint < this.below || codePoint > this.above : codePoint >= this.below && codePoint <= this.above) {
            return false;
        }
        if (codePoint > 65535) {
            writer.write(this.toUtf16Escape(codePoint));
        } else {
            writer.write("\\u");
            writer.write(HEX_DIGITS[codePoint >> 12 & 0xF]);
            writer.write(HEX_DIGITS[codePoint >> 8 & 0xF]);
            writer.write(HEX_DIGITS[codePoint >> 4 & 0xF]);
            writer.write(HEX_DIGITS[codePoint & 0xF]);
        }
        return true;
    }
}

