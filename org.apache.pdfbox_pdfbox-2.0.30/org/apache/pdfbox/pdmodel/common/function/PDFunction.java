/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType0;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType2;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType3;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType4;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionTypeIdentity;

public abstract class PDFunction
implements COSObjectable {
    private PDStream functionStream = null;
    private COSDictionary functionDictionary = null;
    private COSArray domain = null;
    private COSArray range = null;
    private int numberOfInputValues = -1;
    private int numberOfOutputValues = -1;

    public PDFunction(COSBase function) {
        if (function instanceof COSStream) {
            this.functionStream = new PDStream((COSStream)function);
            this.functionStream.getCOSObject().setItem(COSName.TYPE, (COSBase)COSName.FUNCTION);
        } else if (function instanceof COSDictionary) {
            this.functionDictionary = (COSDictionary)function;
        }
    }

    public abstract int getFunctionType();

    @Override
    public COSDictionary getCOSObject() {
        if (this.functionStream != null) {
            return this.functionStream.getCOSObject();
        }
        return this.functionDictionary;
    }

    protected PDStream getPDStream() {
        return this.functionStream;
    }

    public static PDFunction create(COSBase function) throws IOException {
        if (function == COSName.IDENTITY) {
            return new PDFunctionTypeIdentity(null);
        }
        COSBase base = function;
        if (function instanceof COSObject) {
            base = ((COSObject)function).getObject();
        }
        if (!(base instanceof COSDictionary)) {
            throw new IOException("Error: Function must be a Dictionary, but is " + (base == null ? "(null)" : base.getClass().getSimpleName()));
        }
        COSDictionary functionDictionary = (COSDictionary)base;
        int functionType = functionDictionary.getInt(COSName.FUNCTION_TYPE);
        switch (functionType) {
            case 0: {
                return new PDFunctionType0(functionDictionary);
            }
            case 2: {
                return new PDFunctionType2(functionDictionary);
            }
            case 3: {
                return new PDFunctionType3(functionDictionary);
            }
            case 4: {
                return new PDFunctionType4(functionDictionary);
            }
        }
        throw new IOException("Error: Unknown function type " + functionType);
    }

    public int getNumberOfOutputParameters() {
        if (this.numberOfOutputValues == -1) {
            COSArray rangeValues = this.getRangeValues();
            this.numberOfOutputValues = rangeValues == null ? 0 : rangeValues.size() / 2;
        }
        return this.numberOfOutputValues;
    }

    public PDRange getRangeForOutput(int n) {
        COSArray rangeValues = this.getRangeValues();
        return new PDRange(rangeValues, n);
    }

    public void setRangeValues(COSArray rangeValues) {
        this.range = rangeValues;
        this.getCOSObject().setItem(COSName.RANGE, (COSBase)rangeValues);
    }

    public int getNumberOfInputParameters() {
        if (this.numberOfInputValues == -1) {
            COSArray array = this.getDomainValues();
            this.numberOfInputValues = array.size() / 2;
        }
        return this.numberOfInputValues;
    }

    public PDRange getDomainForInput(int n) {
        COSArray domainValues = this.getDomainValues();
        return new PDRange(domainValues, n);
    }

    public void setDomainValues(COSArray domainValues) {
        this.domain = domainValues;
        this.getCOSObject().setItem(COSName.DOMAIN, (COSBase)domainValues);
    }

    @Deprecated
    public COSArray eval(COSArray input) throws IOException {
        float[] outputValues = this.eval(input.toFloatArray());
        COSArray array = new COSArray();
        array.setFloatArray(outputValues);
        return array;
    }

    public abstract float[] eval(float[] var1) throws IOException;

    protected COSArray getRangeValues() {
        if (this.range == null) {
            this.range = (COSArray)this.getCOSObject().getDictionaryObject(COSName.RANGE);
        }
        return this.range;
    }

    private COSArray getDomainValues() {
        if (this.domain == null) {
            this.domain = (COSArray)this.getCOSObject().getDictionaryObject(COSName.DOMAIN);
        }
        return this.domain;
    }

    protected float[] clipToRange(float[] inputValues) {
        float[] result;
        COSArray rangesArray = this.getRangeValues();
        if (rangesArray != null && rangesArray.size() > 0) {
            float[] rangeValues = rangesArray.toFloatArray();
            int numberOfRanges = rangeValues.length / 2;
            result = new float[numberOfRanges];
            for (int i = 0; i < numberOfRanges; ++i) {
                int index = i << 1;
                result[i] = this.clipToRange(inputValues[i], rangeValues[index], rangeValues[index + 1]);
            }
        } else {
            result = inputValues;
        }
        return result;
    }

    protected float clipToRange(float x, float rangeMin, float rangeMax) {
        if (x < rangeMin) {
            return rangeMin;
        }
        if (x > rangeMax) {
            return rangeMax;
        }
        return x;
    }

    protected float interpolate(float x, float xRangeMin, float xRangeMax, float yRangeMin, float yRangeMax) {
        if (xRangeMax == xRangeMin) {
            return yRangeMin;
        }
        return yRangeMin + (x - xRangeMin) * (yRangeMax - yRangeMin) / (xRangeMax - xRangeMin);
    }

    public String toString() {
        return "FunctionType" + this.getFunctionType();
    }
}

