/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.Barcode;
import com.lowagie.text.pdf.PdfContentByte;
import java.awt.Color;
import java.awt.Image;

public class BarcodeEANSUPP
extends Barcode {
    protected Barcode ean;
    protected Barcode supp;

    public BarcodeEANSUPP(Barcode ean, Barcode supp) {
        this.n = 8.0f;
        this.ean = ean;
        this.supp = supp;
    }

    @Override
    public Rectangle getBarcodeSize() {
        Rectangle rect = this.ean.getBarcodeSize();
        rect.setRight(rect.getWidth() + this.supp.getBarcodeSize().getWidth() + this.n);
        return rect;
    }

    @Override
    public Rectangle placeBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        if (this.supp.getFont() != null) {
            this.supp.setBarHeight(this.ean.getBarHeight() + this.supp.getBaseline() - this.supp.getFont().getFontDescriptor(2, this.supp.getSize()));
        } else {
            this.supp.setBarHeight(this.ean.getBarHeight());
        }
        Rectangle eanR = this.ean.getBarcodeSize();
        cb.saveState();
        this.ean.placeBarcode(cb, barColor, textColor);
        cb.restoreState();
        cb.saveState();
        cb.concatCTM(1.0f, 0.0f, 0.0f, 1.0f, eanR.getWidth() + this.n, eanR.getHeight() - this.ean.getBarHeight());
        this.supp.placeBarcode(cb, barColor, textColor);
        cb.restoreState();
        return this.getBarcodeSize();
    }

    @Override
    public Image createAwtImage(Color foreground, Color background) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("the.two.barcodes.must.be.composed.externally"));
    }
}

