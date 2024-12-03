/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.Schema;

public class SchemaNormalization {
    static final long EMPTY64 = -4513414715797952619L;

    private SchemaNormalization() {
    }

    public static String toParsingForm(Schema s) {
        try {
            HashMap<String, String> env = new HashMap<String, String>();
            return SchemaNormalization.build(env, s, new StringBuilder()).toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] fingerprint(String fpName, byte[] data) throws NoSuchAlgorithmException {
        if (fpName.equals("CRC-64-AVRO")) {
            long fp = SchemaNormalization.fingerprint64(data);
            byte[] result = new byte[8];
            for (int i = 0; i < 8; ++i) {
                result[i] = (byte)fp;
                fp >>= 8;
            }
            return result;
        }
        MessageDigest md = MessageDigest.getInstance(fpName);
        return md.digest(data);
    }

    public static long fingerprint64(byte[] data) {
        long result = -4513414715797952619L;
        for (byte b : data) {
            result = result >>> 8 ^ FP64.FP_TABLE[(int)(result ^ (long)b) & 0xFF];
        }
        return result;
    }

    public static byte[] parsingFingerprint(String fpName, Schema s) throws NoSuchAlgorithmException {
        return SchemaNormalization.fingerprint(fpName, SchemaNormalization.toParsingForm(s).getBytes(StandardCharsets.UTF_8));
    }

    public static long parsingFingerprint64(Schema s) {
        return SchemaNormalization.fingerprint64(SchemaNormalization.toParsingForm(s).getBytes(StandardCharsets.UTF_8));
    }

    private static Appendable build(Map<String, String> env, Schema s, Appendable o) throws IOException {
        boolean firstTime = true;
        Schema.Type st = s.getType();
        switch (st) {
            default: {
                return o.append('\"').append(st.getName()).append('\"');
            }
            case UNION: {
                o.append('[');
                for (Schema b : s.getTypes()) {
                    if (!firstTime) {
                        o.append(',');
                    } else {
                        firstTime = false;
                    }
                    SchemaNormalization.build(env, b, o);
                }
                return o.append(']');
            }
            case ARRAY: 
            case MAP: {
                o.append("{\"type\":\"").append(st.getName()).append("\"");
                if (st == Schema.Type.ARRAY) {
                    SchemaNormalization.build(env, s.getElementType(), o.append(",\"items\":"));
                } else {
                    SchemaNormalization.build(env, s.getValueType(), o.append(",\"values\":"));
                }
                return o.append("}");
            }
            case ENUM: 
            case FIXED: 
            case RECORD: 
        }
        String name = s.getFullName();
        if (env.get(name) != null) {
            return o.append(env.get(name));
        }
        String qname = "\"" + name + "\"";
        env.put(name, qname);
        o.append("{\"name\":").append(qname);
        o.append(",\"type\":\"").append(st.getName()).append("\"");
        if (st == Schema.Type.ENUM) {
            o.append(",\"symbols\":[");
            for (String enumSymbol : s.getEnumSymbols()) {
                if (!firstTime) {
                    o.append(',');
                } else {
                    firstTime = false;
                }
                o.append('\"').append(enumSymbol).append('\"');
            }
            o.append("]");
        } else if (st == Schema.Type.FIXED) {
            o.append(",\"size\":").append(Integer.toString(s.getFixedSize()));
        } else {
            o.append(",\"fields\":[");
            for (Schema.Field f : s.getFields()) {
                if (!firstTime) {
                    o.append(',');
                } else {
                    firstTime = false;
                }
                o.append("{\"name\":\"").append(f.name()).append("\"");
                SchemaNormalization.build(env, f.schema(), o.append(",\"type\":")).append("}");
            }
            o.append("]");
        }
        return o.append("}");
    }

    private static class FP64 {
        private static final long[] FP_TABLE = new long[256];

        private FP64() {
        }

        static {
            for (int i = 0; i < 256; ++i) {
                long fp = i;
                for (int j = 0; j < 8; ++j) {
                    long mask = -(fp & 1L);
                    fp = fp >>> 1 ^ 0xC15D213AA4D7A795L & mask;
                }
                FP64.FP_TABLE[i] = fp;
            }
        }
    }
}

