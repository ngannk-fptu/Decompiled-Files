/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import com.lowagie.text.error_messages.MessageLocalization;
import java.net.URL;

public class ImgCCITT
extends Image {
    ImgCCITT(Image image) {
        super(image);
    }

    public ImgCCITT(int width, int height, boolean reverseBits, int typeCCITT, int parameters, byte[] data) throws BadElementException {
        super((URL)null);
        if (typeCCITT != 256 && typeCCITT != 257 && typeCCITT != 258) {
            throw new BadElementException(MessageLocalization.getComposedMessage("the.ccitt.compression.type.must.be.ccittg4.ccittg3.1d.or.ccittg3.2d"));
        }
        if (reverseBits) {
            throw new BadElementException("Reversing bits is not supported");
        }
        this.type = 34;
        this.scaledHeight = height;
        this.setTop(this.scaledHeight);
        this.scaledWidth = width;
        this.setRight(this.scaledWidth);
        this.colorspace = parameters;
        this.bpc = typeCCITT;
        this.rawData = data;
        this.plainWidth = this.getWidth();
        this.plainHeight = this.getHeight();
    }
}

