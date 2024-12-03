/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfException;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfStream;
import java.awt.color.ICC_Profile;

public class PdfICCBased
extends PdfStream {
    public PdfICCBased(ICC_Profile profile) {
        this(profile, -1);
    }

    public PdfICCBased(ICC_Profile profile, int compressionLevel) {
        try {
            int numberOfComponents = profile.getNumComponents();
            switch (numberOfComponents) {
                case 1: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICEGRAY);
                    break;
                }
                case 3: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICERGB);
                    break;
                }
                case 4: {
                    this.put(PdfName.ALTERNATE, PdfName.DEVICECMYK);
                    break;
                }
                default: {
                    throw new PdfException(MessageLocalization.getComposedMessage("1.component.s.is.not.supported", numberOfComponents));
                }
            }
            this.put(PdfName.N, new PdfNumber(numberOfComponents));
            this.bytes = profile.getData();
            this.flateCompress(compressionLevel);
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }
}

