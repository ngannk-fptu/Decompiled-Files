/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

public class PDFunctionType0
extends PDFunction {
    private static final Log LOG = LogFactory.getLog(PDFunctionType0.class);
    private COSArray encode = null;
    private COSArray decode = null;
    private COSArray size = null;
    private int[][] samples = null;

    public PDFunctionType0(COSBase function) {
        super(function);
    }

    @Override
    public int getFunctionType() {
        return 0;
    }

    public COSArray getSize() {
        if (this.size == null) {
            this.size = (COSArray)this.getCOSObject().getDictionaryObject(COSName.SIZE);
        }
        return this.size;
    }

    public int getBitsPerSample() {
        return this.getCOSObject().getInt(COSName.BITS_PER_SAMPLE);
    }

    public int getOrder() {
        return this.getCOSObject().getInt(COSName.ORDER, 1);
    }

    public void setBitsPerSample(int bps) {
        this.getCOSObject().setInt(COSName.BITS_PER_SAMPLE, bps);
    }

    private COSArray getEncodeValues() {
        if (this.encode == null) {
            this.encode = (COSArray)this.getCOSObject().getDictionaryObject(COSName.ENCODE);
            if (this.encode == null) {
                this.encode = new COSArray();
                COSArray sizeValues = this.getSize();
                int sizeValuesSize = sizeValues.size();
                for (int i = 0; i < sizeValuesSize; ++i) {
                    this.encode.add(COSInteger.ZERO);
                    this.encode.add(COSInteger.get((long)sizeValues.getInt(i) - 1L));
                }
            }
        }
        return this.encode;
    }

    private COSArray getDecodeValues() {
        if (this.decode == null) {
            this.decode = (COSArray)this.getCOSObject().getDictionaryObject(COSName.DECODE);
            if (this.decode == null) {
                this.decode = this.getRangeValues();
            }
        }
        return this.decode;
    }

    public PDRange getEncodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray encodeValues = this.getEncodeValues();
        if (encodeValues != null && encodeValues.size() >= paramNum * 2 + 1) {
            retval = new PDRange(encodeValues, paramNum);
        }
        return retval;
    }

    public void setEncodeValues(COSArray encodeValues) {
        this.encode = encodeValues;
        this.getCOSObject().setItem(COSName.ENCODE, (COSBase)encodeValues);
    }

    public PDRange getDecodeForParameter(int paramNum) {
        PDRange retval = null;
        COSArray decodeValues = this.getDecodeValues();
        if (decodeValues != null && decodeValues.size() >= paramNum * 2 + 1) {
            retval = new PDRange(decodeValues, paramNum);
        }
        return retval;
    }

    public void setDecodeValues(COSArray decodeValues) {
        this.decode = decodeValues;
        this.getCOSObject().setItem(COSName.DECODE, (COSBase)decodeValues);
    }

    @Override
    public float[] eval(float[] input) throws IOException {
        float[] sizeValues = this.getSize().toFloatArray();
        int bitsPerSample = this.getBitsPerSample();
        float maxSample = (float)(Math.pow(2.0, bitsPerSample) - 1.0);
        int numberOfInputValues = input.length;
        int numberOfOutputValues = this.getNumberOfOutputParameters();
        int[] inputPrev = new int[numberOfInputValues];
        int[] inputNext = new int[numberOfInputValues];
        input = (float[])input.clone();
        for (int i = 0; i < numberOfInputValues; ++i) {
            PDRange domain = this.getDomainForInput(i);
            PDRange encodeValues = this.getEncodeForParameter(i);
            input[i] = this.clipToRange(input[i], domain.getMin(), domain.getMax());
            input[i] = this.interpolate(input[i], domain.getMin(), domain.getMax(), encodeValues.getMin(), encodeValues.getMax());
            input[i] = this.clipToRange(input[i], 0.0f, sizeValues[i] - 1.0f);
            inputPrev[i] = (int)Math.floor(input[i]);
            inputNext[i] = (int)Math.ceil(input[i]);
        }
        float[] outputValues = new Rinterpol(input, inputPrev, inputNext).rinterpolate();
        for (int i = 0; i < numberOfOutputValues; ++i) {
            PDRange range = this.getRangeForOutput(i);
            PDRange decodeValues = this.getDecodeForParameter(i);
            if (decodeValues == null) {
                throw new IOException("Range missing in function /Decode entry");
            }
            outputValues[i] = this.interpolate(outputValues[i], 0.0f, maxSample, decodeValues.getMin(), decodeValues.getMax());
            outputValues[i] = this.clipToRange(outputValues[i], range.getMin(), range.getMax());
        }
        return outputValues;
    }

    static /* synthetic */ int[][] access$002(PDFunctionType0 x0, int[][] x1) {
        x0.samples = x1;
        return x1;
    }

    private class Rinterpol {
        private final float[] in;
        private final int[] inPrev;
        private final int[] inNext;
        private final int numberOfInputValues;
        private final int numberOfOutputValues;

        Rinterpol(float[] input, int[] inputPrev, int[] inputNext) {
            this.numberOfOutputValues = PDFunctionType0.this.getNumberOfOutputParameters();
            this.in = input;
            this.inPrev = inputPrev;
            this.inNext = inputNext;
            this.numberOfInputValues = input.length;
        }

        float[] rinterpolate() {
            return this.rinterpol(new int[this.numberOfInputValues], 0);
        }

        private float[] rinterpol(int[] coord, int step) {
            float[] resultSample = new float[this.numberOfOutputValues];
            if (step == this.in.length - 1) {
                if (this.inPrev[step] == this.inNext[step]) {
                    coord[step] = this.inPrev[step];
                    int[] tmpSample = this.getSamples()[this.calcSampleIndex(coord)];
                    for (int i = 0; i < this.numberOfOutputValues; ++i) {
                        resultSample[i] = tmpSample[i];
                    }
                    return resultSample;
                }
                coord[step] = this.inPrev[step];
                int[] sample1 = this.getSamples()[this.calcSampleIndex(coord)];
                coord[step] = this.inNext[step];
                int[] sample2 = this.getSamples()[this.calcSampleIndex(coord)];
                for (int i = 0; i < this.numberOfOutputValues; ++i) {
                    resultSample[i] = PDFunctionType0.this.interpolate(this.in[step], this.inPrev[step], this.inNext[step], sample1[i], sample2[i]);
                }
                return resultSample;
            }
            if (this.inPrev[step] == this.inNext[step]) {
                coord[step] = this.inPrev[step];
                return this.rinterpol(coord, step + 1);
            }
            coord[step] = this.inPrev[step];
            float[] sample1 = this.rinterpol(coord, step + 1);
            coord[step] = this.inNext[step];
            float[] sample2 = this.rinterpol(coord, step + 1);
            for (int i = 0; i < this.numberOfOutputValues; ++i) {
                resultSample[i] = PDFunctionType0.this.interpolate(this.in[step], this.inPrev[step], this.inNext[step], sample1[i], sample2[i]);
            }
            return resultSample;
        }

        private int calcSampleIndex(int[] vector) {
            int i;
            float[] sizeValues = PDFunctionType0.this.getSize().toFloatArray();
            int index = 0;
            int sizeProduct = 1;
            int dimension = vector.length;
            for (i = dimension - 2; i >= 0; --i) {
                sizeProduct = (int)((float)sizeProduct * sizeValues[i]);
            }
            for (i = dimension - 1; i >= 0; --i) {
                index += sizeProduct * vector[i];
                if (i - 1 < 0) continue;
                sizeProduct = (int)((float)sizeProduct / sizeValues[i - 1]);
            }
            return index;
        }

        private int[][] getSamples() {
            if (PDFunctionType0.this.samples == null) {
                int arraySize = 1;
                int nIn = PDFunctionType0.this.getNumberOfInputParameters();
                int nOut = PDFunctionType0.this.getNumberOfOutputParameters();
                COSArray sizes = PDFunctionType0.this.getSize();
                for (int i = 0; i < nIn; ++i) {
                    arraySize *= sizes.getInt(i);
                }
                PDFunctionType0.access$002(PDFunctionType0.this, new int[arraySize][nOut]);
                int bitsPerSample = PDFunctionType0.this.getBitsPerSample();
                int index = 0;
                try {
                    COSInputStream inputStream = PDFunctionType0.this.getPDStream().createInputStream();
                    MemoryCacheImageInputStream mciis = new MemoryCacheImageInputStream(inputStream);
                    for (int i = 0; i < arraySize; ++i) {
                        for (int k = 0; k < nOut; ++k) {
                            ((PDFunctionType0)PDFunctionType0.this).samples[index][k] = (int)mciis.readBits(bitsPerSample);
                        }
                        ++index;
                    }
                    mciis.close();
                    ((InputStream)inputStream).close();
                }
                catch (IOException exception) {
                    LOG.error((Object)"IOException while reading the sample values of this function.", (Throwable)exception);
                }
            }
            return PDFunctionType0.this.samples;
        }
    }
}

