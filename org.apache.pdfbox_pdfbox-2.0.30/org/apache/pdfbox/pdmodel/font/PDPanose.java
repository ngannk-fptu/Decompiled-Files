/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.font;

import java.util.Arrays;
import org.apache.pdfbox.pdmodel.font.PDPanoseClassification;

public class PDPanose {
    public static final int LENGTH = 12;
    private final byte[] bytes;

    public PDPanose(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getFamilyClass() {
        return this.bytes[0] << 8 | this.bytes[1] & 0xFF;
    }

    public PDPanoseClassification getPanose() {
        byte[] panose = Arrays.copyOfRange(this.bytes, 2, 12);
        return new PDPanoseClassification(panose);
    }
}

