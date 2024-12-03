/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics;

import java.util.Arrays;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public final class PDLineDashPattern
implements COSObjectable {
    private final int phase;
    private final float[] array;

    public PDLineDashPattern() {
        this.array = new float[0];
        this.phase = 0;
    }

    public PDLineDashPattern(COSArray array, int phase) {
        this.array = array.toFloatArray();
        if (phase < 0) {
            float sum2 = 0.0f;
            for (float f : this.array) {
                sum2 += f;
            }
            phase = (sum2 *= 2.0f) > 0.0f ? (int)((double)phase + ((float)(-phase) < sum2 ? (double)sum2 : (Math.floor((float)(-phase) / sum2) + 1.0) * (double)sum2)) : 0;
        }
        this.phase = phase;
    }

    @Override
    public COSBase getCOSObject() {
        COSArray cos = new COSArray();
        COSArray patternArray = new COSArray();
        patternArray.setFloatArray(this.array);
        cos.add(patternArray);
        cos.add(COSInteger.get(this.phase));
        return cos;
    }

    public int getPhase() {
        return this.phase;
    }

    public float[] getDashArray() {
        return (float[])this.array.clone();
    }

    public String toString() {
        return "PDLineDashPattern{array=" + Arrays.toString(this.array) + ", phase=" + this.phase + "}";
    }
}

