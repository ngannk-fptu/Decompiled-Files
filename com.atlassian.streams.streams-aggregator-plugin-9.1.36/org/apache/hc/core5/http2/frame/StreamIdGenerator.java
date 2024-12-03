/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.frame;

public interface StreamIdGenerator {
    public static final StreamIdGenerator ODD = new StreamIdGenerator(){

        @Override
        public int generate(int previousMax) {
            int i = previousMax + 1;
            if (i % 2 == 0) {
                ++i;
            }
            return i;
        }

        @Override
        public boolean isSameSide(int streamId) {
            return (streamId & 1) == 1;
        }
    };
    public static final StreamIdGenerator EVEN = new StreamIdGenerator(){

        @Override
        public int generate(int previousMax) {
            int i = previousMax + 1;
            if (i % 2 == 1) {
                ++i;
            }
            return i;
        }

        @Override
        public boolean isSameSide(int streamId) {
            return (streamId & 1) == 0;
        }
    };

    public int generate(int var1);

    public boolean isSameSide(int var1);
}

