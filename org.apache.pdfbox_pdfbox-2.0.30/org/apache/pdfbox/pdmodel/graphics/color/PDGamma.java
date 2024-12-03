/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public final class PDGamma
implements COSObjectable {
    private final COSArray values;

    public PDGamma() {
        this.values = new COSArray();
        this.values.add(new COSFloat(0.0f));
        this.values.add(new COSFloat(0.0f));
        this.values.add(new COSFloat(0.0f));
    }

    public PDGamma(COSArray array) {
        this.values = array;
    }

    @Override
    public COSBase getCOSObject() {
        return this.values;
    }

    public COSArray getCOSArray() {
        return this.values;
    }

    public float getR() {
        return ((COSNumber)this.values.get(0)).floatValue();
    }

    public void setR(float r) {
        this.values.set(0, new COSFloat(r));
    }

    public float getG() {
        return ((COSNumber)this.values.get(1)).floatValue();
    }

    public void setG(float g) {
        this.values.set(1, new COSFloat(g));
    }

    public float getB() {
        return ((COSNumber)this.values.get(2)).floatValue();
    }

    public void setB(float b) {
        this.values.set(2, new COSFloat(b));
    }
}

