/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;

public class PdfBorderDictionary
extends PdfDictionary {
    public static final int STYLE_SOLID = 0;
    public static final int STYLE_DASHED = 1;
    public static final int STYLE_BEVELED = 2;
    public static final int STYLE_INSET = 3;
    public static final int STYLE_UNDERLINE = 4;

    public PdfBorderDictionary(float borderWidth, int borderStyle, PdfDashPattern dashes) {
        this.put(PdfName.W, new PdfNumber(borderWidth));
        switch (borderStyle) {
            case 0: {
                this.put(PdfName.S, PdfName.S);
                break;
            }
            case 1: {
                if (dashes != null) {
                    this.put(PdfName.D, dashes);
                }
                this.put(PdfName.S, PdfName.D);
                break;
            }
            case 2: {
                this.put(PdfName.S, PdfName.B);
                break;
            }
            case 3: {
                this.put(PdfName.S, PdfName.I);
                break;
            }
            case 4: {
                this.put(PdfName.S, PdfName.U);
                break;
            }
            default: {
                throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.border.style"));
            }
        }
    }

    public PdfBorderDictionary(float borderWidth, int borderStyle) {
        this(borderWidth, borderStyle, null);
    }
}

