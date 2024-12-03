/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.Range
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.Range;
import org.apache.commons.text.translate.CodePointTranslator;

public class NumericEntityEscaper
extends CodePointTranslator {
    private final boolean between;
    private final Range<Integer> range;

    public static NumericEntityEscaper above(int codePoint) {
        return NumericEntityEscaper.outsideOf(0, codePoint);
    }

    public static NumericEntityEscaper below(int codePoint) {
        return NumericEntityEscaper.outsideOf(codePoint, Integer.MAX_VALUE);
    }

    public static NumericEntityEscaper between(int codePointLow, int codePointHigh) {
        return new NumericEntityEscaper(codePointLow, codePointHigh, true);
    }

    public static NumericEntityEscaper outsideOf(int codePointLow, int codePointHigh) {
        return new NumericEntityEscaper(codePointLow, codePointHigh, false);
    }

    public NumericEntityEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }

    private NumericEntityEscaper(int below, int above, boolean between) {
        this.range = Range.between((Comparable)Integer.valueOf(below), (Comparable)Integer.valueOf(above));
        this.between = between;
    }

    @Override
    public boolean translate(int codePoint, Writer writer) throws IOException {
        if (this.between != this.range.contains((Object)codePoint)) {
            return false;
        }
        writer.write("&#");
        writer.write(Integer.toString(codePoint, 10));
        writer.write(59);
        return true;
    }
}

