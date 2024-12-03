/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument;

public interface Clock {
    public static final Clock SYSTEM = new Clock(){

        @Override
        public long wallTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long monotonicTime() {
            return System.nanoTime();
        }
    };

    public long wallTime();

    public long monotonicTime();
}

