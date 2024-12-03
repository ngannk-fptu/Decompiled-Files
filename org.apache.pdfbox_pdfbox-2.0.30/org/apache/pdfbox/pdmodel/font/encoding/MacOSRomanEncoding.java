/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font.encoding;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.pdmodel.font.encoding.MacRomanEncoding;

public class MacOSRomanEncoding
extends MacRomanEncoding {
    private static final Object[][] MAC_OS_ROMAN_ENCODING_TABLE = new Object[][]{{255, "notequal"}, {260, "infinity"}, {262, "lessequal"}, {263, "greaterequal"}, {266, "partialdiff"}, {267, "summation"}, {270, "product"}, {271, "pi"}, {272, "integral"}, {275, "Omega"}, {303, "radical"}, {305, "approxequal"}, {306, "Delta"}, {327, "lozenge"}, {333, "Euro"}, {360, "apple"}};
    public static final MacOSRomanEncoding INSTANCE = new MacOSRomanEncoding();

    public MacOSRomanEncoding() {
        for (Object[] encodingEntry : MAC_OS_ROMAN_ENCODING_TABLE) {
            this.add((Integer)encodingEntry[0], encodingEntry[1].toString());
        }
    }

    @Override
    public COSBase getCOSObject() {
        return null;
    }
}

