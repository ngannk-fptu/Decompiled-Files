/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  zipkin2.codec.Encoding
 */
package zipkin2.reporter;

import java.util.List;
import zipkin2.codec.Encoding;

public enum BytesMessageEncoder {
    JSON{

        @Override
        public byte[] encode(List<byte[]> values) {
            int sizeOfArray = 2;
            int length = values.size();
            int i = 0;
            while (i < length) {
                sizeOfArray += values.get(i++).length;
                if (i >= length) continue;
                ++sizeOfArray;
            }
            byte[] buf = new byte[sizeOfArray];
            int pos = 0;
            buf[pos++] = 91;
            int i2 = 0;
            while (i2 < length) {
                byte[] v = values.get(i2++);
                System.arraycopy(v, 0, buf, pos, v.length);
                pos += v.length;
                if (i2 >= length) continue;
                buf[pos++] = 44;
            }
            buf[pos] = 93;
            return buf;
        }
    }
    ,
    THRIFT{

        @Override
        public byte[] encode(List<byte[]> values) {
            int sizeOfArray = 5;
            int length = values.size();
            for (int i = 0; i < length; ++i) {
                sizeOfArray += values.get(i).length;
            }
            byte[] buf = new byte[sizeOfArray];
            int pos = 0;
            buf[pos++] = 12;
            buf[pos++] = (byte)(length >>> 24 & 0xFF);
            buf[pos++] = (byte)(length >>> 16 & 0xFF);
            buf[pos++] = (byte)(length >>> 8 & 0xFF);
            buf[pos++] = (byte)(length & 0xFF);
            int i = 0;
            while (i < length) {
                byte[] v = values.get(i++);
                System.arraycopy(v, 0, buf, pos, v.length);
                pos += v.length;
            }
            return buf;
        }
    }
    ,
    PROTO3{

        @Override
        public byte[] encode(List<byte[]> values) {
            int sizeOfArray = 0;
            int length = values.size();
            int i = 0;
            while (i < length) {
                sizeOfArray += values.get(i++).length;
            }
            byte[] buf = new byte[sizeOfArray];
            int pos = 0;
            int i2 = 0;
            while (i2 < length) {
                byte[] v = values.get(i2++);
                System.arraycopy(v, 0, buf, pos, v.length);
                pos += v.length;
            }
            return buf;
        }
    };


    public abstract byte[] encode(List<byte[]> var1);

    public static BytesMessageEncoder forEncoding(Encoding encoding) {
        if (encoding == null) {
            throw new NullPointerException("encoding == null");
        }
        switch (encoding) {
            case JSON: {
                return JSON;
            }
            case PROTO3: {
                return PROTO3;
            }
            case THRIFT: {
                return THRIFT;
            }
        }
        throw new UnsupportedOperationException(encoding.name());
    }
}

