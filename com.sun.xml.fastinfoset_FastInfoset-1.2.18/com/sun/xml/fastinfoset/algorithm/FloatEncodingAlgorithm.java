/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.IEEE754FloatingPointEncodingAlgorithm;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public class FloatEncodingAlgorithm
extends IEEE754FloatingPointEncodingAlgorithm {
    @Override
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 4 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfFloat", new Object[]{4}));
        }
        return octetLength / 4;
    }

    @Override
    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * 4;
    }

    @Override
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        float[] data = new float[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToFloatArray(data, 0, b, start, length);
        return data;
    }

    @Override
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return this.decodeFromInputStreamToFloatArray(s);
    }

    @Override
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
        }
        float[] fdata = (float[])data;
        this.encodeToOutputStreamFromFloatArray(fdata, s);
    }

    @Override
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList floatList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener(){

            @Override
            public void word(int start, int end) {
                String fStringValue = cb.subSequence(start, end).toString();
                floatList.add(Float.valueOf(fStringValue));
            }
        });
        return this.generateArrayFromList(floatList);
    }

    @Override
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
        }
        float[] fdata = (float[])data;
        this.convertToCharactersFromFloatArray(fdata, s);
    }

    public final void decodeFromBytesToFloatArray(float[] data, int fstart, byte[] b, int start, int length) {
        int size = length / 4;
        for (int i = 0; i < size; ++i) {
            int bits = (b[start++] & 0xFF) << 24 | (b[start++] & 0xFF) << 16 | (b[start++] & 0xFF) << 8 | b[start++] & 0xFF;
            data[fstart++] = Float.intBitsToFloat(bits);
        }
    }

    public final float[] decodeFromInputStreamToFloatArray(InputStream s) throws IOException {
        ArrayList<Float> floatList = new ArrayList<Float>();
        byte[] b = new byte[4];
        while (true) {
            int n;
            if ((n = s.read(b)) != 4) {
                if (n == -1) break;
                while (n != 4) {
                    int m = s.read(b, n, 4 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            int bits = (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | b[3] & 0xFF;
            floatList.add(Float.valueOf(Float.intBitsToFloat(bits)));
        }
        return this.generateArrayFromList(floatList);
    }

    public final void encodeToOutputStreamFromFloatArray(float[] fdata, OutputStream s) throws IOException {
        for (int i = 0; i < fdata.length; ++i) {
            int bits = Float.floatToIntBits(fdata[i]);
            s.write(bits >>> 24 & 0xFF);
            s.write(bits >>> 16 & 0xFF);
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }

    @Override
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        this.encodeToBytesFromFloatArray((float[])array, astart, alength, b, start);
    }

    public final void encodeToBytesFromFloatArray(float[] fdata, int fstart, int flength, byte[] b, int start) {
        int fend = fstart + flength;
        for (int i = fstart; i < fend; ++i) {
            int bits = Float.floatToIntBits(fdata[i]);
            b[start++] = (byte)(bits >>> 24 & 0xFF);
            b[start++] = (byte)(bits >>> 16 & 0xFF);
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }

    public final void convertToCharactersFromFloatArray(float[] fdata, StringBuffer s) {
        int end = fdata.length - 1;
        for (int i = 0; i <= end; ++i) {
            s.append(Float.toString(fdata[i]));
            if (i == end) continue;
            s.append(' ');
        }
    }

    public final float[] generateArrayFromList(List array) {
        float[] fdata = new float[array.size()];
        for (int i = 0; i < fdata.length; ++i) {
            fdata[i] = ((Float)array.get(i)).floatValue();
        }
        return fdata;
    }
}

