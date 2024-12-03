/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.function.PDFFunction;
import java.io.IOException;

public class FunctionType3
extends PDFFunction {
    private PDFFunction[] functions;
    private float[] bounds;
    private float[] encode;

    protected FunctionType3() {
        super(3);
    }

    @Override
    protected void parse(PDFObject obj) throws IOException {
        PDFObject functionsObj = obj.getDictRef("Functions");
        if (functionsObj == null) {
            throw new PDFParseException("Functions required for function type 3!");
        }
        PDFObject[] functionsAry = functionsObj.getArray();
        this.functions = new PDFFunction[functionsAry.length];
        for (int i = 0; i < functionsAry.length; ++i) {
            this.functions[i] = PDFFunction.getFunction(functionsAry[i]);
        }
        PDFObject boundsObj = obj.getDictRef("Bounds");
        if (boundsObj == null) {
            throw new PDFParseException("Bounds required for function type 3!");
        }
        PDFObject[] boundsAry = boundsObj.getArray();
        this.bounds = new float[boundsAry.length + 2];
        if (this.bounds.length - 2 != this.functions.length - 1) {
            throw new PDFParseException("Bounds array must be of length " + (this.functions.length - 1));
        }
        for (int i = 0; i < boundsAry.length; ++i) {
            this.bounds[i + 1] = boundsAry[i].getFloatValue();
        }
        this.bounds[0] = this.getDomain(0);
        this.bounds[this.bounds.length - 1] = this.getDomain(1);
        PDFObject encodeObj = obj.getDictRef("Encode");
        if (encodeObj == null) {
            throw new PDFParseException("Encode required for function type 3!");
        }
        PDFObject[] encodeAry = encodeObj.getArray();
        this.encode = new float[encodeAry.length];
        if (this.encode.length != 2 * this.functions.length) {
            throw new PDFParseException("Encode array must be of length " + 2 * this.functions.length);
        }
        for (int i = 0; i < encodeAry.length; ++i) {
            this.encode[i] = encodeAry[i].getFloatValue();
        }
    }

    @Override
    protected void doFunction(float[] inputs, int inputOffset, float[] outputs, int outputOffset) {
        float x = inputs[inputOffset];
        int p = this.bounds.length - 2;
        while (x < this.bounds[p]) {
            --p;
        }
        x = FunctionType3.interpolate(x, this.bounds[p], this.bounds[p + 1], this.encode[2 * p], this.encode[2 * p + 1]);
        float[] out = this.functions[p].calculate(new float[]{x});
        for (int i = 0; i < out.length; ++i) {
            outputs[i + outputOffset] = out[i];
        }
    }

    @Override
    public int getNumInputs() {
        return 1;
    }

    @Override
    public int getNumOutputs() {
        return this.functions[0].getNumOutputs();
    }
}

