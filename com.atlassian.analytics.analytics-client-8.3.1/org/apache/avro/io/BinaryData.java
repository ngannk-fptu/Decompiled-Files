/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.io;

import java.io.IOException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.Decoder;
import org.apache.avro.util.internal.ThreadLocalWithInitial;

public class BinaryData {
    private static final ThreadLocal<Decoders> DECODERS = ThreadLocalWithInitial.of(Decoders::new);
    private static final ThreadLocal<HashData> HASH_DATA = ThreadLocalWithInitial.of(HashData::new);

    private BinaryData() {
    }

    public static int compare(byte[] b1, int s1, byte[] b2, int s2, Schema schema) {
        return BinaryData.compare(b1, s1, b1.length - s1, b2, s2, b2.length - s2, schema);
    }

    public static int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2, Schema schema) {
        Decoders decoders = DECODERS.get();
        decoders.set(b1, s1, l1, b2, s2, l2);
        try {
            int n = BinaryData.compare(decoders, schema);
            return n;
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
        finally {
            decoders.clear();
        }
    }

    private static int compare(Decoders d, Schema schema) throws IOException {
        BinaryDecoder d1 = d.d1;
        BinaryDecoder d2 = d.d2;
        switch (schema.getType()) {
            case RECORD: {
                for (Schema.Field field : schema.getFields()) {
                    if (field.order() == Schema.Field.Order.IGNORE) {
                        GenericDatumReader.skip(field.schema(), d1);
                        GenericDatumReader.skip(field.schema(), d2);
                        continue;
                    }
                    int c = BinaryData.compare(d, field.schema());
                    if (c == 0) continue;
                    return field.order() != Schema.Field.Order.DESCENDING ? c : -c;
                }
                return 0;
            }
            case ENUM: 
            case INT: {
                return Integer.compare(((Decoder)d1).readInt(), ((Decoder)d2).readInt());
            }
            case LONG: {
                return Long.compare(((Decoder)d1).readLong(), ((Decoder)d2).readLong());
            }
            case FLOAT: {
                return Float.compare(((Decoder)d1).readFloat(), ((Decoder)d2).readFloat());
            }
            case DOUBLE: {
                return Double.compare(((Decoder)d1).readDouble(), ((Decoder)d2).readDouble());
            }
            case BOOLEAN: {
                return Boolean.compare(((Decoder)d1).readBoolean(), ((Decoder)d2).readBoolean());
            }
            case ARRAY: {
                long i = 0L;
                long r1 = 0L;
                long r2 = 0L;
                long l1 = 0L;
                long l2 = 0L;
                block15: while (true) {
                    if (r1 == 0L) {
                        r1 = ((Decoder)d1).readLong();
                        if (r1 < 0L) {
                            r1 = -r1;
                            ((Decoder)d1).readLong();
                        }
                        l1 += r1;
                    }
                    if (r2 == 0L) {
                        r2 = ((Decoder)d2).readLong();
                        if (r2 < 0L) {
                            r2 = -r2;
                            ((Decoder)d2).readLong();
                        }
                        l2 += r2;
                    }
                    if (r1 == 0L || r2 == 0L) {
                        return Long.compare(l1, l2);
                    }
                    long l = Math.min(l1, l2);
                    while (true) {
                        if (i >= l) continue block15;
                        int c = BinaryData.compare(d, schema.getElementType());
                        if (c != 0) {
                            return c;
                        }
                        ++i;
                        --r1;
                        --r2;
                    }
                    break;
                }
            }
            case MAP: {
                throw new AvroRuntimeException("Can't compare maps!");
            }
            case UNION: {
                int i1 = ((Decoder)d1).readInt();
                int i2 = ((Decoder)d2).readInt();
                int c = Integer.compare(i1, i2);
                return c == 0 ? BinaryData.compare(d, schema.getTypes().get(i1)) : c;
            }
            case FIXED: {
                int size = schema.getFixedSize();
                int c = BinaryData.compareBytes(d.d1.getBuf(), d.d1.getPos(), size, d.d2.getBuf(), d.d2.getPos(), size);
                d.d1.skipFixed(size);
                d.d2.skipFixed(size);
                return c;
            }
            case STRING: 
            case BYTES: {
                int l1 = ((Decoder)d1).readInt();
                int l2 = ((Decoder)d2).readInt();
                int c = BinaryData.compareBytes(d.d1.getBuf(), d.d1.getPos(), l1, d.d2.getBuf(), d.d2.getPos(), l2);
                d.d1.skipFixed(l1);
                d.d2.skipFixed(l2);
                return c;
            }
            case NULL: {
                return 0;
            }
        }
        throw new AvroRuntimeException("Unexpected schema to compare!");
    }

    public static int compareBytes(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
        int end1 = s1 + l1;
        int end2 = s2 + l2;
        int i = s1;
        for (int j = s2; i < end1 && j < end2; ++i, ++j) {
            int a = b1[i] & 0xFF;
            int b = b2[j] & 0xFF;
            if (a == b) continue;
            return a - b;
        }
        return l1 - l2;
    }

    public static int hashCode(byte[] bytes, int start, int length, Schema schema) {
        HashData data = HASH_DATA.get();
        data.set(bytes, start, length);
        try {
            return BinaryData.hashCode(data, schema);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    private static int hashCode(HashData data, Schema schema) throws IOException {
        BinaryDecoder decoder = data.decoder;
        switch (schema.getType()) {
            case RECORD: {
                int hashCode = 1;
                for (Schema.Field field : schema.getFields()) {
                    if (field.order() == Schema.Field.Order.IGNORE) {
                        GenericDatumReader.skip(field.schema(), decoder);
                        continue;
                    }
                    hashCode = hashCode * 31 + BinaryData.hashCode(data, field.schema());
                }
                return hashCode;
            }
            case ENUM: 
            case INT: {
                return ((Decoder)decoder).readInt();
            }
            case BOOLEAN: {
                return Boolean.hashCode(((Decoder)decoder).readBoolean());
            }
            case FLOAT: {
                return Float.hashCode(((Decoder)decoder).readFloat());
            }
            case LONG: {
                return Long.hashCode(((Decoder)decoder).readLong());
            }
            case DOUBLE: {
                return Double.hashCode(((Decoder)decoder).readDouble());
            }
            case ARRAY: {
                Schema elementType = schema.getElementType();
                int hashCode = 1;
                long l = ((Decoder)decoder).readArrayStart();
                while (l != 0L) {
                    for (long i = 0L; i < l; ++i) {
                        hashCode = hashCode * 31 + BinaryData.hashCode(data, elementType);
                    }
                    l = ((Decoder)decoder).arrayNext();
                }
                return hashCode;
            }
            case MAP: {
                throw new AvroRuntimeException("Can't hashCode maps!");
            }
            case UNION: {
                return BinaryData.hashCode(data, schema.getTypes().get(((Decoder)decoder).readInt()));
            }
            case FIXED: {
                return BinaryData.hashBytes(1, data, schema.getFixedSize(), false);
            }
            case STRING: {
                return BinaryData.hashBytes(0, data, ((Decoder)decoder).readInt(), false);
            }
            case BYTES: {
                return BinaryData.hashBytes(1, data, ((Decoder)decoder).readInt(), true);
            }
            case NULL: {
                return 0;
            }
        }
        throw new AvroRuntimeException("Unexpected schema to hashCode!");
    }

    private static int hashBytes(int init, HashData data, int len, boolean rev) throws IOException {
        int hashCode = init;
        byte[] bytes = data.decoder.getBuf();
        int start = data.decoder.getPos();
        int end = start + len;
        if (rev) {
            for (int i = end - 1; i >= start; --i) {
                hashCode = hashCode * 31 + bytes[i];
            }
        } else {
            for (int i = start; i < end; ++i) {
                hashCode = hashCode * 31 + bytes[i];
            }
        }
        data.decoder.skipFixed(len);
        return hashCode;
    }

    public static int skipLong(byte[] bytes, int start) {
        while ((bytes[start++] & 0x80) != 0) {
        }
        return start;
    }

    public static int encodeBoolean(boolean b, byte[] buf, int pos) {
        buf[pos] = b ? (byte)1 : 0;
        return 1;
    }

    public static int encodeInt(int n, byte[] buf, int pos) {
        n = n << 1 ^ n >> 31;
        int start = pos;
        if ((n & 0xFFFFFF80) != 0) {
            buf[pos++] = (byte)((n | 0x80) & 0xFF);
            if ((n >>>= 7) > 127) {
                buf[pos++] = (byte)((n | 0x80) & 0xFF);
                if ((n >>>= 7) > 127) {
                    buf[pos++] = (byte)((n | 0x80) & 0xFF);
                    if ((n >>>= 7) > 127) {
                        buf[pos++] = (byte)((n | 0x80) & 0xFF);
                        n >>>= 7;
                    }
                }
            }
        }
        buf[pos++] = (byte)n;
        return pos - start;
    }

    public static int encodeLong(long n, byte[] buf, int pos) {
        n = n << 1 ^ n >> 63;
        int start = pos;
        if ((n & 0xFFFFFFFFFFFFFF80L) != 0L) {
            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
            if ((n >>>= 7) > 127L) {
                buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                if ((n >>>= 7) > 127L) {
                    buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                    if ((n >>>= 7) > 127L) {
                        buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                        if ((n >>>= 7) > 127L) {
                            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                            if ((n >>>= 7) > 127L) {
                                buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                if ((n >>>= 7) > 127L) {
                                    buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                    if ((n >>>= 7) > 127L) {
                                        buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                        if ((n >>>= 7) > 127L) {
                                            buf[pos++] = (byte)((n | 0x80L) & 0xFFL);
                                            n >>>= 7;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        buf[pos++] = (byte)n;
        return pos - start;
    }

    public static int encodeFloat(float f, byte[] buf, int pos) {
        int bits = Float.floatToRawIntBits(f);
        buf[pos + 3] = (byte)(bits >>> 24);
        buf[pos + 2] = (byte)(bits >>> 16);
        buf[pos + 1] = (byte)(bits >>> 8);
        buf[pos] = (byte)bits;
        return 4;
    }

    public static int encodeDouble(double d, byte[] buf, int pos) {
        long bits = Double.doubleToRawLongBits(d);
        int first = (int)(bits & 0xFFFFFFFFFFFFFFFFL);
        int second = (int)(bits >>> 32 & 0xFFFFFFFFFFFFFFFFL);
        buf[pos] = (byte)first;
        buf[pos + 4] = (byte)second;
        buf[pos + 5] = (byte)(second >>> 8);
        buf[pos + 1] = (byte)(first >>> 8);
        buf[pos + 2] = (byte)(first >>> 16);
        buf[pos + 6] = (byte)(second >>> 16);
        buf[pos + 7] = (byte)(second >>> 24);
        buf[pos + 3] = (byte)(first >>> 24);
        return 8;
    }

    private static class HashData {
        private final BinaryDecoder decoder = new BinaryDecoder(new byte[0], 0, 0);

        public void set(byte[] bytes, int start, int len) {
            this.decoder.setBuf(bytes, start, len);
        }
    }

    private static class Decoders {
        private final BinaryDecoder d1 = new BinaryDecoder(new byte[0], 0, 0);
        private final BinaryDecoder d2 = new BinaryDecoder(new byte[0], 0, 0);

        public void set(byte[] data1, int off1, int len1, byte[] data2, int off2, int len2) {
            this.d1.setBuf(data1, off1, len1);
            this.d2.setBuf(data2, off2, len2);
        }

        public void clear() {
            this.d1.clearBuf();
            this.d2.clearBuf();
        }
    }
}

