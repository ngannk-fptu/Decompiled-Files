/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryValueType;

public class HexBinaryType
extends BinaryBaseType {
    public static final HexBinaryType theInstance = new HexBinaryType();
    private static final long serialVersionUID = 1L;

    private HexBinaryType() {
        super("hexBinary");
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - 48;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 65 + 10;
        }
        if ('a' <= ch && ch <= 'f') {
            return ch - 97 + 10;
        }
        return -1;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        byte[] buf = HexBinaryType.load(lexicalValue);
        if (buf == null) {
            return null;
        }
        return new BinaryValueType(buf);
    }

    public static byte[] load(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            return null;
        }
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int h = HexBinaryType.hexToBin(s.charAt(i));
            int l = HexBinaryType.hexToBin(s.charAt(i + 1));
            if (h == -1 || l == -1) {
                return null;
            }
            out[i / 2] = (byte)(h * 16 + l);
        }
        return out;
    }

    protected boolean checkFormat(String lexicalValue, ValidationContext context) {
        int len = lexicalValue.length();
        if (len % 2 != 0) {
            return false;
        }
        for (int i = 0; i < len; ++i) {
            if (HexBinaryType.hexToBin(lexicalValue.charAt(i)) != -1) continue;
            return false;
        }
        return true;
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof byte[])) {
            throw new IllegalArgumentException();
        }
        return HexBinaryType.save((byte[])value);
    }

    public static String save(byte[] data) {
        StringBuffer r = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; ++i) {
            r.append(HexBinaryType.encode(data[i] >> 4));
            r.append(HexBinaryType.encode(data[i] & 0xF));
        }
        return r.toString();
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof BinaryValueType)) {
            throw new IllegalArgumentException();
        }
        return this.serializeJavaObject(((BinaryValueType)value).rawData, context);
    }

    public static char encode(int ch) {
        if ((ch &= 0xF) < 10) {
            return (char)(48 + ch);
        }
        return (char)(65 + (ch - 10));
    }
}

