/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.function;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.function.PDFFunction;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FunctionType0
extends PDFFunction {
    protected static final int LINEAR_INTERPOLATION = 1;
    protected static final int CUBIC_INTERPOLATION = 3;
    private int[] size;
    private int bitsPerSample;
    private int order = 1;
    private float[] encode;
    private float[] decode;
    private int[][] samples;

    protected FunctionType0() {
        super(0);
    }

    @Override
    protected void parse(PDFObject obj) throws IOException {
        PDFObject decodeObj;
        PDFObject encodeObj;
        PDFObject sizeObj = obj.getDictRef("Size");
        if (sizeObj == null) {
            throw new PDFParseException("Size required for function type 0!");
        }
        PDFObject[] sizeAry = sizeObj.getArray();
        int[] size = new int[sizeAry.length];
        for (int i = 0; i < sizeAry.length; ++i) {
            size[i] = sizeAry[i].getIntValue();
        }
        this.setSize(size);
        PDFObject bpsObj = obj.getDictRef("BitsPerSample");
        if (bpsObj == null) {
            throw new PDFParseException("BitsPerSample required for function type 0!");
        }
        this.setBitsPerSample(bpsObj.getIntValue());
        PDFObject orderObj = obj.getDictRef("Order");
        if (orderObj != null) {
            this.setOrder(orderObj.getIntValue());
        }
        if ((encodeObj = obj.getDictRef("Encode")) != null) {
            PDFObject[] encodeAry = encodeObj.getArray();
            float[] encode = new float[encodeAry.length];
            for (int i = 0; i < encodeAry.length; ++i) {
                encode[i] = encodeAry[i].getFloatValue();
            }
            this.setEncode(encode);
        }
        if ((decodeObj = obj.getDictRef("Decode")) != null) {
            PDFObject[] decodeAry = decodeObj.getArray();
            float[] decode = new float[decodeAry.length];
            for (int i = 0; i < decodeAry.length; ++i) {
                decode[i] = decodeAry[i].getFloatValue();
            }
            this.setDecode(decode);
        }
        this.setSamples(this.readSamples(obj.getStreamBuffer()));
    }

    @Override
    protected void doFunction(float[] inputs, int inputOffset, float[] outputs, int outputOffset) {
        int i;
        float[] encoded = new float[this.getNumInputs()];
        for (i = 0; i < this.getNumInputs(); ++i) {
            encoded[i] = FunctionType0.interpolate(inputs[i + inputOffset], this.getDomain(2 * i), this.getDomain(2 * i + 1), this.getEncode(2 * i), this.getEncode(2 * i + 1));
            encoded[i] = Math.max(encoded[i], 0.0f);
            encoded[i] = Math.min(encoded[i], (float)(this.size[i] - 1));
        }
        for (i = 0; i < this.getNumOutputs(); ++i) {
            outputs[i + outputOffset] = this.getOrder() == 1 ? this.multilinearInterpolate(encoded, i) : this.multicubicInterpolate(encoded, i);
        }
        for (i = 0; i < outputs.length; ++i) {
            outputs[i + outputOffset] = FunctionType0.interpolate(outputs[i + outputOffset], 0.0f, (float)Math.pow(2.0, this.getBitsPerSample()) - 1.0f, this.getDecode(2 * i), this.getDecode(2 * i + 1));
        }
    }

    protected int getSize(int dimension) {
        return this.size[dimension];
    }

    protected void setSize(int[] size) {
        this.size = size;
    }

    protected int getBitsPerSample() {
        return this.bitsPerSample;
    }

    protected void setBitsPerSample(int bits) {
        this.bitsPerSample = bits;
    }

    protected int getOrder() {
        return this.order;
    }

    protected void setOrder(int order) {
        this.order = order;
    }

    protected float getEncode(int i) {
        if (this.encode != null) {
            return this.encode[i];
        }
        if (i % 2 == 0) {
            return 0.0f;
        }
        return this.getSize(i / 2) - 1;
    }

    protected void setEncode(float[] encode) {
        this.encode = encode;
    }

    protected float getDecode(int i) {
        if (this.decode != null) {
            return this.decode[i];
        }
        return this.getRange(i);
    }

    protected void setDecode(float[] decode) {
        this.decode = decode;
    }

    protected int getSample(int[] values, int od) {
        int mult = 1;
        int index = 0;
        for (int i = 0; i < values.length; ++i) {
            index += mult * values[i];
            mult *= this.getSize(i);
        }
        return this.samples[index][od];
    }

    protected void setSamples(int[][] samples) {
        this.samples = samples;
    }

    private int[][] readSamples(ByteBuffer buf) {
        int size = 1;
        for (int i = 0; i < this.getNumInputs(); ++i) {
            size *= this.getSize(i);
        }
        int[][] samples = new int[size][this.getNumOutputs()];
        int bitLoc = 0;
        int byteLoc = 0;
        int index = 0;
        for (int i = 0; i < this.getNumInputs(); ++i) {
            for (int j = 0; j < this.getSize(i); ++j) {
                for (int k = 0; k < this.getNumOutputs(); ++k) {
                    int value = 0;
                    byte curByte = buf.get(byteLoc);
                    for (int toRead = this.getBitsPerSample(); toRead > 0; --toRead) {
                        int nextBit = curByte >> 7 - bitLoc & 1;
                        value |= nextBit << toRead - 1;
                        if (++bitLoc != 8) continue;
                        bitLoc = 0;
                        ++byteLoc;
                        if (toRead <= 1) continue;
                        curByte = buf.get(byteLoc);
                    }
                    samples[index][k] = value;
                }
                ++index;
            }
        }
        return samples;
    }

    private float multilinearInterpolate(float[] encoded, int od) {
        float val;
        float[] dists = new float[encoded.length];
        for (int i = 0; i < dists.length; ++i) {
            dists[i] = (float)((double)encoded[i] - Math.floor(encoded[i]));
        }
        int map = 0;
        float prev = val = this.getSample(encoded, map, od);
        for (int i = 0; i < dists.length; ++i) {
            int idx = 0;
            float largest = -1.0f;
            for (int c = 0; c < dists.length; ++c) {
                if (!(dists[c] > largest)) continue;
                largest = dists[c];
                idx = c;
            }
            float cur = this.getSample(encoded, map |= 1 << idx, od);
            prev = val += dists[idx] * (cur - prev);
            dists[idx] = -1.0f;
        }
        return val;
    }

    private float multicubicInterpolate(float[] encoded, int od) {
        System.out.println("Cubic interpolation not supported!");
        return this.multilinearInterpolate(encoded, od);
    }

    public static float interpolate(float x, float xmin, float xmax, float ymin, float ymax) {
        float value = (ymax - ymin) / (xmax - xmin);
        value *= x - xmin;
        return value += ymin;
    }

    private float getSample(float[] encoded, int map, int od) {
        int[] controls = new int[encoded.length];
        for (int i = 0; i < controls.length; ++i) {
            controls[i] = (map & 1 << i) == 0 ? (int)Math.floor(encoded[i]) : (int)Math.ceil(encoded[i]);
        }
        return this.getSample(controls, od);
    }
}

