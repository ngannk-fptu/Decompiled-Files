/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.codec.wmf;

import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.codec.wmf.InputMeta;
import com.lowagie.text.pdf.codec.wmf.MetaObject;
import com.lowagie.text.pdf.codec.wmf.MetaState;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class MetaFont
extends MetaObject {
    static final String[] fontNames = new String[]{"Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic", "Symbol", "ZapfDingbats"};
    static final int MARKER_BOLD = 1;
    static final int MARKER_ITALIC = 2;
    static final int MARKER_COURIER = 0;
    static final int MARKER_HELVETICA = 4;
    static final int MARKER_TIMES = 8;
    static final int MARKER_SYMBOL = 12;
    static final int DEFAULT_PITCH = 0;
    static final int FIXED_PITCH = 1;
    static final int VARIABLE_PITCH = 2;
    static final int FF_DONTCARE = 0;
    static final int FF_ROMAN = 1;
    static final int FF_SWISS = 2;
    static final int FF_MODERN = 3;
    static final int FF_SCRIPT = 4;
    static final int FF_DECORATIVE = 5;
    static final int BOLDTHRESHOLD = 600;
    static final int nameSize = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;
    int height;
    float angle;
    int bold;
    int italic;
    boolean underline;
    boolean strikeout;
    int charset;
    int pitchAndFamily;
    String faceName = "arial";
    BaseFont font = null;

    public MetaFont() {
        this.type = 3;
    }

    public void init(InputMeta in) throws IOException {
        int c;
        int k;
        this.height = Math.abs(in.readShort());
        in.skip(2);
        this.angle = (float)((double)in.readShort() / 1800.0 * Math.PI);
        in.skip(2);
        this.bold = in.readShort() >= 600 ? 1 : 0;
        this.italic = in.readByte() != 0 ? 2 : 0;
        this.underline = in.readByte() != 0;
        this.strikeout = in.readByte() != 0;
        this.charset = in.readByte();
        in.skip(3);
        this.pitchAndFamily = in.readByte();
        byte[] name = new byte[32];
        for (k = 0; k < 32 && (c = in.readByte()) != 0; ++k) {
            name[k] = (byte)c;
        }
        try {
            this.faceName = new String(name, 0, k, "Cp1252");
        }
        catch (UnsupportedEncodingException e) {
            this.faceName = new String(name, 0, k);
        }
        this.faceName = this.faceName.toLowerCase(Locale.ROOT);
    }

    public BaseFont getFont() {
        String fontName;
        if (this.font != null) {
            return this.font;
        }
        Font ff2 = FontFactory.getFont(this.faceName, "Cp1252", true, 10.0f, (this.italic != 0 ? 2 : 0) | (this.bold != 0 ? 1 : 0));
        this.font = ff2.getBaseFont();
        if (this.font != null) {
            return this.font;
        }
        if (this.faceName.contains("courier") || this.faceName.contains("terminal") || this.faceName.contains("fixedsys")) {
            fontName = fontNames[0 + this.italic + this.bold];
        } else if (this.faceName.contains("ms sans serif") || this.faceName.contains("arial") || this.faceName.contains("system")) {
            fontName = fontNames[4 + this.italic + this.bold];
        } else if (this.faceName.contains("arial black")) {
            fontName = fontNames[4 + this.italic + 1];
        } else if (this.faceName.contains("times") || this.faceName.contains("ms serif") || this.faceName.contains("roman")) {
            fontName = fontNames[8 + this.italic + this.bold];
        } else if (this.faceName.contains("symbol")) {
            fontName = fontNames[12];
        } else {
            int pitch = this.pitchAndFamily & 3;
            int family = this.pitchAndFamily >> 4 & 7;
            block1 : switch (family) {
                case 3: {
                    fontName = fontNames[0 + this.italic + this.bold];
                    break;
                }
                case 1: {
                    fontName = fontNames[8 + this.italic + this.bold];
                    break;
                }
                case 2: 
                case 4: 
                case 5: {
                    fontName = fontNames[4 + this.italic + this.bold];
                    break;
                }
                default: {
                    switch (pitch) {
                        case 1: {
                            fontName = fontNames[0 + this.italic + this.bold];
                            break block1;
                        }
                    }
                    fontName = fontNames[4 + this.italic + this.bold];
                }
            }
        }
        try {
            this.font = BaseFont.createFont(fontName, "Cp1252", false);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        return this.font;
    }

    public float getAngle() {
        return this.angle;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isStrikeout() {
        return this.strikeout;
    }

    public float getFontSize(MetaState state) {
        return Math.abs(state.transformY(this.height) - state.transformY(0)) * Document.wmfFontCorrection;
    }
}

