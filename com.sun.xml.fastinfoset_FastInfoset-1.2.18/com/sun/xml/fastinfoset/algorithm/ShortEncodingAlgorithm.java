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

public class ShortEncodingAlgorithm
extends IntegerEncodingAlgorithm {
    @Override
    public final int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 2 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfShort", new Object[]{2}));
        }
        return octetLength / 2;
    }

    @Override
    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        return primitiveLength * 2;
    }

    @Override
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        short[] data = new short[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToShortArray(data, 0, b, start, length);
        return data;
    }

    @Override
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        return this.decodeFromInputStreamToShortArray(s);
    }

    @Override
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
        }
        short[] idata = (short[])data;
        this.encodeToOutputStreamFromShortArray(idata, s);
    }

    @Override
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList shortList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener(){

            @Override
            public void word(int start, int end) {
                String iStringValue = cb.subSequence(start, end).toString();
                shortList.add(Short.valueOf(iStringValue));
            }
        });
        return this.generateArrayFromList(shortList);
    }

    @Override
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
        }
        short[] idata = (short[])data;
        this.convertToCharactersFromShortArray(idata, s);
    }

    public final void decodeFromBytesToShortArray(short[] sdata, int istart, byte[] b, int start, int length) {
        int size = length / 2;
        for (int i = 0; i < size; ++i) {
            sdata[istart++] = (short)((b[start++] & 0xFF) << 8 | b[start++] & 0xFF);
        }
    }

    public final short[] decodeFromInputStreamToShortArray(InputStream s) throws IOException {
        ArrayList<Short> shortList = new ArrayList<Short>();
        byte[] b = new byte[2];
        while (true) {
            int n;
            if ((n = s.read(b)) != 2) {
                if (n == -1) break;
                while (n != 2) {
                    int m = s.read(b, n, 2 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            int i = (b[0] & 0xFF) << 8 | b[1] & 0xFF;
            shortList.add((short)i);
        }
        return this.generateArrayFromList(shortList);
    }

    public final void encodeToOutputStreamFromShortArray(short[] idata, OutputStream s) throws IOException {
        for (int i = 0; i < idata.length; ++i) {
            short bits = idata[i];
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }

    @Override
    public final void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        this.encodeToBytesFromShortArray((short[])array, astart, alength, b, start);
    }

    public final void encodeToBytesFromShortArray(short[] sdata, int istart, int ilength, byte[] b, int start) {
        int iend = istart + ilength;
        for (int i = istart; i < iend; ++i) {
            short bits = sdata[i];
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }

    public final void convertToCharactersFromShortArray(short[] sdata, StringBuffer s) {
        int end = sdata.length - 1;
        for (int i = 0; i <= end; ++i) {
            s.append(Short.toString(sdata[i]));
            if (i == end) continue;
            s.append(' ');
        }
    }

    public final short[] generateArrayFromList(List array) {
        short[] sdata = new short[array.size()];
        for (int i = 0; i < sdata.length; ++i) {
            sdata[i] = (Short)array.get(i);
        }
        return sdata;
    }
}

