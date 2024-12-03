/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;

public class PDFourColours
implements COSObjectable {
    private final COSArray array;

    public PDFourColours() {
        this.array = new COSArray();
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
        this.array.add(COSNull.NULL);
    }

    public PDFourColours(COSArray array) {
        this.array = array;
        if (this.array.size() < 4) {
            for (int i = this.array.size() - 1; i < 4; ++i) {
                this.array.add(COSNull.NULL);
            }
        }
    }

    public PDGamma getBeforeColour() {
        return this.getColourByIndex(0);
    }

    public void setBeforeColour(PDGamma colour) {
        this.setColourByIndex(0, colour);
    }

    public PDGamma getAfterColour() {
        return this.getColourByIndex(1);
    }

    public void setAfterColour(PDGamma colour) {
        this.setColourByIndex(1, colour);
    }

    public PDGamma getStartColour() {
        return this.getColourByIndex(2);
    }

    public void setStartColour(PDGamma colour) {
        this.setColourByIndex(2, colour);
    }

    public PDGamma getEndColour() {
        return this.getColourByIndex(3);
    }

    public void setEndColour(PDGamma colour) {
        this.setColourByIndex(3, colour);
    }

    @Override
    public COSBase getCOSObject() {
        return this.array;
    }

    private PDGamma getColourByIndex(int index) {
        PDGamma retval = null;
        COSBase item = this.array.getObject(index);
        if (item instanceof COSArray) {
            retval = new PDGamma((COSArray)item);
        }
        return retval;
    }

    private void setColourByIndex(int index, PDGamma colour) {
        COSBase base = colour == null ? COSNull.NULL : colour.getCOSArray();
        this.array.set(index, base);
    }
}

