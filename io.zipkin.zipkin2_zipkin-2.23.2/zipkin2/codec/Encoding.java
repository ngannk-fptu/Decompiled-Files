/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.codec;

import java.util.List;

public enum Encoding {
    JSON{

        @Override
        public int listSizeInBytes(int encodedSizeInBytes) {
            return 2 + encodedSizeInBytes;
        }

        @Override
        public int listSizeInBytes(List<byte[]> values) {
            int sizeInBytes = 2;
            int i = 0;
            int length = values.size();
            while (i < length) {
                sizeInBytes += values.get(i++).length;
                if (i >= length) continue;
                ++sizeInBytes;
            }
            return sizeInBytes;
        }
    }
    ,
    THRIFT{

        @Override
        public int listSizeInBytes(int encodedSizeInBytes) {
            return 5 + encodedSizeInBytes;
        }

        @Override
        public int listSizeInBytes(List<byte[]> values) {
            int sizeInBytes = 5;
            int length = values.size();
            for (int i = 0; i < length; ++i) {
                sizeInBytes += values.get(i).length;
            }
            return sizeInBytes;
        }
    }
    ,
    PROTO3{

        @Override
        public int listSizeInBytes(int encodedSizeInBytes) {
            return encodedSizeInBytes;
        }

        @Override
        public int listSizeInBytes(List<byte[]> values) {
            int sizeInBytes = 0;
            int i = 0;
            int length = values.size();
            while (i < length) {
                sizeInBytes += values.get(i++).length;
            }
            return sizeInBytes;
        }
    };


    public abstract int listSizeInBytes(int var1);

    public abstract int listSizeInBytes(List<byte[]> var1);
}

