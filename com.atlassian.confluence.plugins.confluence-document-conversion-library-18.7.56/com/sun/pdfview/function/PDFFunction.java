/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.function.FunctionType0;
import com.sun.pdfview.function.FunctionType2;
import com.sun.pdfview.function.FunctionType3;
import com.sun.pdfview.function.FunctionType4;
import java.io.IOException;

public abstract class PDFFunction {
    public static final int TYPE_0 = 0;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    public static final int TYPE_4 = 4;
    private int type;
    private float[] domain;
    private float[] range;

    protected PDFFunction(int type) {
        this.type = type;
    }

    public static PDFFunction getFunction(PDFObject obj) throws IOException {
        PDFFunction function;
        float[] domain = null;
        float[] range = null;
        PDFObject typeObj = obj.getDictRef("FunctionType");
        if (typeObj == null) {
            throw new PDFParseException("No FunctionType specified in function!");
        }
        int type = typeObj.getIntValue();
        PDFObject domainObj = obj.getDictRef("Domain");
        if (domainObj == null) {
            throw new PDFParseException("No Domain specified in function!");
        }
        PDFObject[] domainAry = domainObj.getArray();
        domain = new float[domainAry.length];
        for (int i = 0; i < domainAry.length; ++i) {
            domain[i] = domainAry[i].getFloatValue();
        }
        PDFObject rangeObj = obj.getDictRef("Range");
        if (rangeObj != null) {
            PDFObject[] rangeAry = rangeObj.getArray();
            range = new float[rangeAry.length];
            for (int i = 0; i < rangeAry.length; ++i) {
                range[i] = rangeAry[i].getFloatValue();
            }
        }
        switch (type) {
            case 0: {
                if (rangeObj == null) {
                    throw new PDFParseException("No Range specified in Type 0 Function!");
                }
                function = new FunctionType0();
                break;
            }
            case 2: {
                function = new FunctionType2();
                break;
            }
            case 3: {
                function = new FunctionType3();
                break;
            }
            case 4: {
                if (rangeObj == null) {
                    throw new PDFParseException("No Range specified in Type 4 Function!");
                }
                function = new FunctionType4();
                break;
            }
            default: {
                throw new PDFParseException("Unsupported function type: " + type);
            }
        }
        function.setDomain(domain);
        if (range != null) {
            function.setRange(range);
        }
        function.parse(obj);
        return function;
    }

    public static float interpolate(float x, float xmin, float xmax, float ymin, float ymax) {
        float value = (ymax - ymin) / (xmax - xmin);
        value *= x - xmin;
        return value += ymin;
    }

    public int getType() {
        return this.type;
    }

    public int getNumInputs() {
        return this.domain.length / 2;
    }

    public int getNumOutputs() {
        if (this.range == null) {
            return 0;
        }
        return this.range.length / 2;
    }

    protected float getDomain(int i) {
        return this.domain[i];
    }

    protected void setDomain(float[] domain) {
        this.domain = domain;
    }

    protected float getRange(int i) {
        if (this.range == null) {
            if (i % 2 == 0) {
                return Float.MIN_VALUE;
            }
            return Float.MAX_VALUE;
        }
        return this.range[i];
    }

    protected void setRange(float[] range) {
        this.range = range;
    }

    public float[] calculate(float[] inputs) {
        float[] outputs = new float[this.getNumOutputs()];
        this.calculate(inputs, 0, outputs, 0);
        return outputs;
    }

    public float[] calculate(float[] inputs, int inputOffset, float[] outputs, int outputOffset) {
        int i;
        if (inputs.length - inputOffset < this.getNumInputs()) {
            throw new IllegalArgumentException("Wrong number of inputs to function!");
        }
        if (this.range != null && outputs.length - outputOffset < this.getNumOutputs()) {
            throw new IllegalArgumentException("Wrong number of outputs for function!");
        }
        for (i = 0; i < inputs.length; ++i) {
            inputs[i] = Math.max(inputs[i], this.getDomain(2 * i));
            inputs[i] = Math.min(inputs[i], this.getDomain(2 * i + 1));
        }
        this.doFunction(inputs, inputOffset, outputs, outputOffset);
        for (i = 0; this.range != null && i < outputs.length; ++i) {
            outputs[i] = Math.max(outputs[i], this.getRange(2 * i));
            outputs[i] = Math.min(outputs[i], this.getRange(2 * i + 1));
        }
        return outputs;
    }

    protected abstract void doFunction(float[] var1, int var2, float[] var3, int var4);

    protected abstract void parse(PDFObject var1) throws IOException;
}

