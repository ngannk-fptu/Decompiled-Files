/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.afm.CharMetric
 *  org.apache.fontbox.afm.FontMetrics
 *  org.apache.fontbox.encoding.Encoding
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import java.util.Map;
import org.apache.fontbox.afm.CharMetric;
import org.apache.fontbox.afm.FontMetrics;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

public class Type1Encoding
extends Encoding {
    public static Type1Encoding fromFontBox(org.apache.fontbox.encoding.Encoding encoding) {
        Map codeToName = encoding.getCodeToNameMap();
        Type1Encoding enc = new Type1Encoding();
        for (Map.Entry entry : codeToName.entrySet()) {
            enc.add((Integer)entry.getKey(), (String)entry.getValue());
        }
        return enc;
    }

    public Type1Encoding() {
    }

    public Type1Encoding(FontMetrics fontMetrics) {
        for (CharMetric nextMetric : fontMetrics.getCharMetrics()) {
            this.add(nextMetric.getCharacterCode(), nextMetric.getName());
        }
    }

    @Override
    public COSBase getCOSObject() {
        return null;
    }

    @Override
    public String getEncodingName() {
        return "built-in (Type 1)";
    }
}

