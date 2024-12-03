/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.function.PDFFunction;
import java.io.IOException;

public class FunctionType2
extends PDFFunction {
    private float[] c0 = new float[]{0.0f};
    private float[] c1 = new float[]{1.0f};
    private float n;

    public FunctionType2() {
        super(2);
    }

    @Override
    protected void parse(PDFObject obj) throws IOException {
        PDFObject cOneObj;
        PDFObject nObj = obj.getDictRef("N");
        if (nObj == null) {
            throw new PDFParseException("Exponent required for function type 2!");
        }
        this.setN(nObj.getFloatValue());
        PDFObject cZeroObj = obj.getDictRef("C0");
        if (cZeroObj != null) {
            PDFObject[] cZeroAry = cZeroObj.getArray();
            float[] cZero = new float[cZeroAry.length];
            for (int i = 0; i < cZeroAry.length; ++i) {
                cZero[i] = cZeroAry[i].getFloatValue();
            }
            this.setC0(cZero);
        }
        if ((cOneObj = obj.getDictRef("C1")) != null) {
            PDFObject[] cOneAry = cOneObj.getArray();
            float[] cOne = new float[cOneAry.length];
            for (int i = 0; i < cOneAry.length; ++i) {
                cOne[i] = cOneAry[i].getFloatValue();
            }
            this.setC1(cOne);
        }
    }

    @Override
    protected void doFunction(float[] inputs, int inputOffset, float[] outputs, int outputOffset) {
        float input = inputs[inputOffset];
        for (int i = 0; i < this.getNumOutputs(); ++i) {
            outputs[i + outputOffset] = this.getC0(i) + (float)(Math.pow(input, this.getN()) * (double)(this.getC1(i) - this.getC0(i)));
        }
    }

    @Override
    public int getNumOutputs() {
        return this.c0.length;
    }

    public float getN() {
        return this.n;
    }

    protected void setN(float n) {
        this.n = n;
    }

    public float getC0(int index) {
        return this.c0[index];
    }

    protected void setC0(float[] c0) {
        this.c0 = c0;
    }

    public float getC1(int index) {
        return this.c1[index];
    }

    protected void setC1(float[] c1) {
        this.c1 = c1;
    }
}

