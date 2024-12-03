/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.algorithm;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithm;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public class BooleanEncodingAlgorithm
extends BuiltInEncodingAlgorithm {
    private static final int[] BIT_TABLE = new int[]{128, 64, 32, 16, 8, 4, 2, 1};

    @Override
    public int getPrimtiveLengthFromOctetLength(int octetLength) throws EncodingAlgorithmException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getOctetLengthFromPrimitiveLength(int primitiveLength) {
        if (primitiveLength < 5) {
            return 1;
        }
        int div = primitiveLength / 8;
        return div == 0 ? 2 : 1 + div;
    }

    @Override
    public final Object decodeFromBytes(byte[] b, int start, int length) throws EncodingAlgorithmException {
        int blength = this.getPrimtiveLengthFromOctetLength(length, b[start]);
        boolean[] data = new boolean[blength];
        this.decodeFromBytesToBooleanArray(data, 0, blength, b, start, length);
        return data;
    }

    @Override
    public final Object decodeFromInputStream(InputStream s) throws IOException {
        ArrayList<Boolean> booleanList = new ArrayList<Boolean>();
        int value = s.read();
        if (value == -1) {
            throw new EOFException();
        }
        int unusedBits = value >> 4 & 0xFF;
        int bitPosition = 4;
        int bitPositionEnd = 8;
        int valueNext = 0;
        do {
            if ((valueNext = s.read()) == -1) {
                bitPositionEnd -= unusedBits;
            }
            while (bitPosition < bitPositionEnd) {
                booleanList.add((value & BIT_TABLE[bitPosition++]) > 0);
            }
        } while ((value = valueNext) != -1);
        return this.generateArrayFromList(booleanList);
    }

    @Override
    public void encodeToOutputStream(Object data, OutputStream s) throws IOException {
        if (!(data instanceof boolean[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
        }
        boolean[] array = (boolean[])data;
        int alength = array.length;
        int mod = (alength + 4) % 8;
        int unusedBits = mod == 0 ? 0 : 8 - mod;
        int bitPosition = 4;
        int value = unusedBits << 4;
        int astart = 0;
        while (astart < alength) {
            if (array[astart++]) {
                value |= BIT_TABLE[bitPosition];
            }
            if (++bitPosition != 8) continue;
            s.write(value);
            value = 0;
            bitPosition = 0;
        }
        if (bitPosition != 8) {
            s.write(value);
        }
    }

    @Override
    public final Object convertFromCharacters(char[] ch, int start, int length) {
        if (length == 0) {
            return new boolean[0];
        }
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final ArrayList booleanList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new BuiltInEncodingAlgorithm.WordListener(){

            @Override
            public void word(int start, int end) {
                if (cb.charAt(start) == 't') {
                    booleanList.add(Boolean.TRUE);
                } else {
                    booleanList.add(Boolean.FALSE);
                }
            }
        });
        return this.generateArrayFromList(booleanList);
    }

    @Override
    public final void convertToCharacters(Object data, StringBuffer s) {
        if (data == null) {
            return;
        }
        boolean[] value = (boolean[])data;
        if (value.length == 0) {
            return;
        }
        s.ensureCapacity(value.length * 5);
        int end = value.length - 1;
        for (int i = 0; i <= end; ++i) {
            if (value[i]) {
                s.append("true");
            } else {
                s.append("false");
            }
            if (i == end) continue;
            s.append(' ');
        }
    }

    public int getPrimtiveLengthFromOctetLength(int octetLength, int firstOctet) throws EncodingAlgorithmException {
        int unusedBits = firstOctet >> 4 & 0xFF;
        if (octetLength == 1) {
            if (unusedBits > 3) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits4"));
            }
            return 4 - unusedBits;
        }
        if (unusedBits > 7) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits8"));
        }
        return octetLength * 8 - 4 - unusedBits;
    }

    public final void decodeFromBytesToBooleanArray(boolean[] bdata, int bstart, int blength, byte[] b, int start, int length) {
        int value = b[start++] & 0xFF;
        int bitPosition = 4;
        int bend = bstart + blength;
        while (bstart < bend) {
            if (bitPosition == 8) {
                value = b[start++] & 0xFF;
                bitPosition = 0;
            }
            bdata[bstart++] = (value & BIT_TABLE[bitPosition++]) > 0;
        }
    }

    @Override
    public void encodeToBytes(Object array, int astart, int alength, byte[] b, int start) {
        if (!(array instanceof boolean[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
        }
        this.encodeToBytesFromBooleanArray((boolean[])array, astart, alength, b, start);
    }

    public void encodeToBytesFromBooleanArray(boolean[] array, int astart, int alength, byte[] b, int start) {
        int mod = (alength + 4) % 8;
        int unusedBits = mod == 0 ? 0 : 8 - mod;
        int bitPosition = 4;
        int value = unusedBits << 4;
        int aend = astart + alength;
        while (astart < aend) {
            if (array[astart++]) {
                value |= BIT_TABLE[bitPosition];
            }
            if (++bitPosition != 8) continue;
            b[start++] = (byte)value;
            value = 0;
            bitPosition = 0;
        }
        if (bitPosition > 0) {
            b[start] = (byte)value;
        }
    }

    private boolean[] generateArrayFromList(List array) {
        boolean[] bdata = new boolean[array.size()];
        for (int i = 0; i < bdata.length; ++i) {
            bdata[i] = (Boolean)array.get(i);
        }
        return bdata;
    }
}

