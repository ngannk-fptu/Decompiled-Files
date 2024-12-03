/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import com.sun.xml.fastinfoset.algorithm.IntegerEncodingAlgorithm;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public class IntEncodingAlgorithm
extends IntegerEncodingAlgorithm {
    @Override
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 4 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfInt", new Object[]{4}));
        }
        return octetLength / 4;
    }

    @Override
    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * 4;
    }

    @Override
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        int[] data = new int[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToIntArray(data, 0, b, start, length);
        return data;
    }

    @Override
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return this.decodeFromInputStreamToIntArray(s);
    }

    @Override
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
        }
        int[] idata = (int[])data;
        this.encodeToOutputStreamFromIntArray(idata, s);
    }

    @Override
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList integerList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener(){

            @Override
            public void word(int start, int end) {
                String iStringValue = cb.subSequence(start, end).toString();
                integerList.add(Integer.valueOf(iStringValue));
            }
        });
        return this.generateArrayFromList(integerList);
    }

    @Override
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
        }
        int[] idata = (int[])data;
        this.convertToCharactersFromIntArray(idata, s);
    }

    public final void decodeFromBytesToIntArray(int[] idata, int istart, byte[] b, int start, int length) {
        int size = length / 4;
        for (int i = 0; i < size; ++i) {
            idata[istart++] = (b[start++] & 0xFF) << 24 | (b[start++] & 0xFF) << 16 | (b[start++] & 0xFF) << 8 | b[start++] & 0xFF;
        }
    }

    public final int[] decodeFromInputStreamToIntArray(InputStream s) throws IOException {
        ArrayList<Integer> integerList = new ArrayList<Integer>();
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
            int i = (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | b[3] & 0xFF;
            integerList.add(i);
        }
        return this.generateArrayFromList(integerList);
    }

    public final void encodeToOutputStreamFromIntArray(int[] idata, OutputStream s) throws IOException {
        for (int i = 0; i < idata.length; ++i) {
            int bits = idata[i];
            s.write(bits >>> 24 & 0xFF);
            s.write(bits >>> 16 & 0xFF);
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }

    @Override
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        this.encodeToBytesFromIntArray((int[])array, astart, alength, b, start);
    }

    public final void encodeToBytesFromIntArray(int[] idata, int istart, int ilength, byte[] b, int start) {
        int iend = istart + ilength;
        for (int i = istart; i < iend; ++i) {
            int bits = idata[i];
            b[start++] = (byte)(bits >>> 24 & 0xFF);
            b[start++] = (byte)(bits >>> 16 & 0xFF);
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }

    public final void convertToCharactersFromIntArray(int[] idata, StringBuffer s) {
        int end = idata.length - 1;
        for (int i = 0; i <= end; ++i) {
            s.append(Integer.toString(idata[i]));
            if (i == end) continue;
            s.append(' ');
        }
    }

    public final int[] generateArrayFromList(List array) {
        int[] idata = new int[array.size()];
        for (int i = 0; i < idata.length; ++i) {
            idata[i] = (Integer)array.get(i);
        }
        return idata;
    }
}

