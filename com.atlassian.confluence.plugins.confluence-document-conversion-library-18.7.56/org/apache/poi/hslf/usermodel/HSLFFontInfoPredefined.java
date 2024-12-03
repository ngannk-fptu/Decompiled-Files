/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontFamily;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.common.usermodel.fonts.FontPitch;

public enum HSLFFontInfoPredefined implements FontInfo
{
    ARIAL("Arial", FontCharset.ANSI, FontPitch.VARIABLE, FontFamily.FF_SWISS),
    TIMES_NEW_ROMAN("Times New Roman", FontCharset.ANSI, FontPitch.VARIABLE, FontFamily.FF_ROMAN),
    COURIER_NEW("Courier New", FontCharset.ANSI, FontPitch.FIXED, FontFamily.FF_MODERN),
    WINGDINGS("Wingdings", FontCharset.SYMBOL, FontPitch.VARIABLE, FontFamily.FF_DONTCARE);

    private String typeface;
    private FontCharset charset;
    private FontPitch pitch;
    private FontFamily family;

    private HSLFFontInfoPredefined(String typeface, FontCharset charset, FontPitch pitch, FontFamily family) {
        this.typeface = typeface;
        this.charset = charset;
        this.pitch = pitch;
        this.family = family;
    }

    @Override
    public String getTypeface() {
        return this.typeface;
    }

    @Override
    public FontCharset getCharset() {
        return this.charset;
    }

    @Override
    public FontFamily getFamily() {
        return this.family;
    }

    @Override
    public FontPitch getPitch() {
        return this.pitch;
    }
}

