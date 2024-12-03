/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

public class PDFunctionType2
extends PDFunction {
    private final COSArray c0;
    private final COSArray c1;
    private final float exponent;

    public PDFunctionType2(COSBase function) {
        super(function);
        COSArray cosArray1;
        COSArray cosArray0 = this.getCOSObject().getCOSArray(COSName.C0);
        this.c0 = cosArray0 != null ? cosArray0 : new COSArray();
        if (this.c0.size() == 0) {
            this.c0.add(new COSFloat(0.0f));
        }
        this.c1 = (cosArray1 = this.getCOSObject().getCOSArray(COSName.C1)) != null ? cosArray1 : new COSArray();
        if (this.c1.size() == 0) {
            this.c1.add(new COSFloat(1.0f));
        }
        this.exponent = this.getCOSObject().getFloat(COSName.N);
    }

    @Override
    public int getFunctionType() {
        return 2;
    }

    @Override
    public float[] eval(float[] input) throws IOException {
        float xToN = (float)Math.pow(input[0], this.exponent);
        float[] result = new float[Math.min(this.c0.size(), this.c1.size())];
        for (int j = 0; j < result.length; ++j) {
            float c0j = ((COSNumber)this.c0.get(j)).floatValue();
            float c1j = ((COSNumber)this.c1.get(j)).floatValue();
            result[j] = c0j + xToN * (c1j - c0j);
        }
        return this.clipToRange(result);
    }

    public COSArray getC0() {
        return this.c0;
    }

    public COSArray getC1() {
        return this.c1;
    }

    public float getN() {
        return this.exponent;
    }

    @Override
    public String toString() {
        return "FunctionType2{C0: " + this.getC0() + " C1: " + this.getC1() + " N: " + this.getN() + "}";
    }
}

