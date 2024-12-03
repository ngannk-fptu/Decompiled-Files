/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j;

public interface BucketListener {
    public static final BucketListener NOPE = new BucketListener(){

        @Override
        public void onConsumed(long tokens) {
        }

        @Override
        public void onRejected(long tokens) {
        }

        @Override
        public void onDelayed(long nanos) {
        }

        @Override
        public void onParked(long nanos) {
        }

        @Override
        public void onInterrupted(InterruptedException e) {
        }
    };

    public void onConsumed(long var1);

    public void onRejected(long var1);

    public void onParked(long var1);

    public void onInterrupted(InterruptedException var1);

    public void onDelayed(long var1);
}

