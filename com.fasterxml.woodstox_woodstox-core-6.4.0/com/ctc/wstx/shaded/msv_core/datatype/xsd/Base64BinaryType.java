/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryBaseType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BinaryValueType;

public class Base64BinaryType
extends BinaryBaseType {
    public static final Base64BinaryType theInstance = new Base64BinaryType();
    private static final byte[] decodeMap = Base64BinaryType.initDecodeMap();
    private static final byte PADDING = 127;
    private static final char[] encodeMap = Base64BinaryType.initEncodeMap();
    private static final long serialVersionUID = 1L;

    private Base64BinaryType() {
        super("base64Binary");
    }

    private static byte[] initDecodeMap() {
        int i;
        byte[] map = new byte[256];
        for (i = 0; i < 256; ++i) {
            map[i] = -1;
        }
        for (i = 65; i <= 90; ++i) {
            map[i] = (byte)(i - 65);
        }
        for (i = 97; i <= 122; ++i) {
            map[i] = (byte)(i - 97 + 26);
        }
        for (i = 48; i <= 57; ++i) {
            map[i] = (byte)(i - 48 + 52);
        }
        map[43] = 62;
        map[47] = 63;
        map[61] = 127;
        return map;
    }

    public Object _createValue(String lexicalValue, ValidationContext context) {
        byte[] buf = Base64BinaryType.load(lexicalValue);
        if (buf == null) {
            return null;
        }
        return new BinaryValueType(buf);
    }

    public static byte[] load(String lexicalValue) {
        char[] buf = lexicalValue.toCharArray();
        int outlen = Base64BinaryType.calcLength(buf);
        if (outlen == -1) {
            return null;
        }
        byte[] out = new byte[outlen];
        int o = 0;
        int len = buf.length;
        byte[] quadruplet = new byte[4];
        int q = 0;
        for (int i = 0; i < len; ++i) {
            byte v = decodeMap[buf[i]];
            if (v != -1) {
                quadruplet[q++] = v;
            }
            if (q != 4) continue;
            out[o++] = (byte)(quadruplet[0] << 2 | quadruplet[1] >> 4);
            if (quadruplet[2] != 127) {
                out[o++] = (byte)(quadruplet[1] << 4 | quadruplet[2] >> 2);
            }
            if (quadruplet[3] != 127) {
                out[o++] = (byte)(quadruplet[2] << 6 | quadruplet[3]);
            }
            q = 0;
        }
        if (q != 0) {
            throw new IllegalStateException();
        }
        return out;
    }

    protected boolean checkFormat(String lexicalValue, ValidationContext context) {
        return Base64BinaryType.calcLength(lexicalValue.toCharArray()) != -1;
    }

    private static int calcLength(char[] buf) {
        int i;
        int len = buf.length;
        int base64count = 0;
        int paddingCount = 0;
        for (i = 0; i < len && buf[i] != '='; ++i) {
            if (buf[i] >= '\u0100') {
                return -1;
            }
            if (decodeMap[buf[i]] == -1) continue;
            ++base64count;
        }
        while (i < len) {
            if (buf[i] == '=') {
                ++paddingCount;
            } else {
                if (buf[i] >= '\u0100') {
                    return -1;
                }
                if (decodeMap[buf[i]] != -1) {
                    return -1;
                }
            }
            ++i;
        }
        if (paddingCount > 2) {
            return -1;
        }
        if ((base64count + paddingCount) % 4 != 0) {
            return -1;
        }
        return (base64count + paddingCount) / 4 * 3 - paddingCount;
    }

    private static char[] initEncodeMap() {
        int i;
        char[] map = new char[64];
        for (i = 0; i < 26; ++i) {
            map[i] = (char)(65 + i);
        }
        for (i = 26; i < 52; ++i) {
            map[i] = (char)(97 + (i - 26));
        }
        for (i = 52; i < 62; ++i) {
            map[i] = (char)(48 + (i - 52));
        }
        map[62] = 43;
        map[63] = 47;
        return map;
    }

    protected static char encode(int i) {
        return encodeMap[i & 0x3F];
    }

    public String serializeJavaObject(Object value, SerializationContext context) {
        if (!(value instanceof byte[])) {
            throw new IllegalArgumentException();
        }
        return Base64BinaryType.save((byte[])value);
    }

    public static String save(byte[] input) {
        StringBuffer r = new StringBuffer(input.length * 4 / 3);
        block4: for (int i = 0; i < input.length; i += 3) {
            switch (input.length - i) {
                case 1: {
                    r.append(Base64BinaryType.encode(input[i] >> 2));
                    r.append(Base64BinaryType.encode((input[i] & 3) << 4));
                    r.append("==");
                    continue block4;
                }
                case 2: {
                    r.append(Base64BinaryType.encode(input[i] >> 2));
                    r.append(Base64BinaryType.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF));
                    r.append(Base64BinaryType.encode((input[i + 1] & 0xF) << 2));
                    r.append("=");
                    continue block4;
                }
                default: {
                    r.append(Base64BinaryType.encode(input[i] >> 2));
                    r.append(Base64BinaryType.encode((input[i] & 3) << 4 | input[i + 1] >> 4 & 0xF));
                    r.append(Base64BinaryType.encode((input[i + 1] & 0xF) << 2 | input[i + 2] >> 6 & 3));
                    r.append(Base64BinaryType.encode(input[i + 2] & 0x3F));
                }
            }
        }
        return r.toString();
    }

    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof BinaryValueType)) {
            throw new IllegalArgumentException();
        }
        return this.serializeJavaObject(((BinaryValueType)value).rawData, context);
    }
}

