/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public final class PDTristimulus
implements COSObjectable {
    private final COSArray values;

    public PDTristimulus() {
        this.values = new COSArray();
        this.values.add(new COSFloat(0.0f));
        this.values.add(new COSFloat(0.0f));
        this.values.add(new COSFloat(0.0f));
    }

    public PDTristimulus(COSArray array) {
        this.values = array;
    }

    public PDTristimulus(float[] array) {
        this.values = new COSArray();
        for (int i = 0; i < array.length && i < 3; ++i) {
            this.values.add(new COSFloat(array[i]));
        }
    }

    @Override
    public COSBase getCOSObject() {
        return this.values;
    }

    public float getX() {
        return ((COSNumber)this.values.get(0)).floatValue();
    }

    public void setX(float x) {
        this.values.set(0, new COSFloat(x));
    }

    public float getY() {
        return ((COSNumber)this.values.get(1)).floatValue();
    }

    public void setY(float y) {
        this.values.set(1, new COSFloat(y));
    }

    public float getZ() {
        return ((COSNumber)this.values.get(2)).floatValue();
    }

    public void setZ(float z) {
        this.values.set(2, new COSFloat(z));
    }
}

