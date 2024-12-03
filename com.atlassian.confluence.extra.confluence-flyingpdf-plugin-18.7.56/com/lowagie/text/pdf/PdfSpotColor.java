/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.ExtendedColor;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfFunction;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public class PdfSpotColor {
    public PdfName name;
    public Color altcs;

    public PdfSpotColor(String name, Color altcs) {
        this.name = new PdfName(name);
        this.altcs = altcs;
    }

    public Color getAlternativeCS() {
        return this.altcs;
    }

    protected PdfObject getSpotObject(PdfWriter writer) {
        PdfArray array = new PdfArray(PdfName.SEPARATION);
        array.add(this.name);
        PdfFunction func = null;
        if (this.altcs instanceof ExtendedColor) {
            int type = ((ExtendedColor)this.altcs).type;
            switch (type) {
                case 1: {
                    array.add(PdfName.DEVICEGRAY);
                    func = PdfFunction.type2(writer, new float[]{0.0f, 1.0f}, null, new float[]{0.0f}, new float[]{((GrayColor)this.altcs).getGray()}, 1.0f);
                    break;
                }
                case 2: {
                    array.add(PdfName.DEVICECMYK);
                    CMYKColor cmyk = (CMYKColor)this.altcs;
                    func = PdfFunction.type2(writer, new float[]{0.0f, 1.0f}, null, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack()}, 1.0f);
                    break;
                }
                default: {
                    throw new RuntimeException(MessageLocalization.getComposedMessage("only.rgb.gray.and.cmyk.are.supported.as.alternative.color.spaces"));
                }
            }
        } else {
            array.add(PdfName.DEVICERGB);
            func = PdfFunction.type2(writer, new float[]{0.0f, 1.0f}, null, new float[]{1.0f, 1.0f, 1.0f}, new float[]{(float)this.altcs.getRed() / 255.0f, (float)this.altcs.getGreen() / 255.0f, (float)this.altcs.getBlue() / 255.0f}, 1.0f);
        }
        array.add(func.getReference());
        return array;
    }
}

