/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.fontbox.FontBoxFont
 */
package org.apache.pdfbox.pdmodel.font;

import org.apache.fontbox.FontBoxFont;
import org.apache.pdfbox.pdmodel.font.CIDSystemInfo;
import org.apache.pdfbox.pdmodel.font.FontFormat;
import org.apache.pdfbox.pdmodel.font.PDPanoseClassification;

public abstract class FontInfo {
    public abstract String getPostScriptName();

    public abstract FontFormat getFormat();

    public abstract CIDSystemInfo getCIDSystemInfo();

    public abstract FontBoxFont getFont();

    public abstract int getFamilyClass();

    public abstract int getWeightClass();

    final int getWeightClassAsPanose() {
        int usWeightClass = this.getWeightClass();
        switch (usWeightClass) {
            case -1: {
                return 0;
            }
            case 0: {
                return 0;
            }
            case 100: {
                return 2;
            }
            case 200: {
                return 3;
            }
            case 300: {
                return 4;
            }
            case 400: {
                return 5;
            }
            case 500: {
                return 6;
            }
            case 600: {
                return 7;
            }
            case 700: {
                return 8;
            }
            case 800: {
                return 9;
            }
            case 900: {
                return 10;
            }
        }
        return 0;
    }

    public abstract int getCodePageRange1();

    public abstract int getCodePageRange2();

    final long getCodePageRange() {
        long range1 = (long)this.getCodePageRange1() & 0xFFFFFFFFL;
        long range2 = (long)this.getCodePageRange2() & 0xFFFFFFFFL;
        return range2 << 32 | range1;
    }

    public abstract int getMacStyle();

    public abstract PDPanoseClassification getPanose();

    public String toString() {
        return this.getPostScriptName() + " (" + (Object)((Object)this.getFormat()) + ", mac: 0x" + Integer.toHexString(this.getMacStyle()) + ", os/2: 0x" + Integer.toHexString(this.getFamilyClass()) + ", cid: " + this.getCIDSystemInfo() + ")";
    }
}

