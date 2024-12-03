/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

public class ThreadedSeedGenerator {
    public byte[] generateSeed(int numBytes, boolean fast) {
        SeedGenerator gen = new SeedGenerator();
        return gen.generateSeed(numBytes, fast);
    }

    private static class SeedGenerator
    implements Runnable {
        private volatile int counter = 0;
        private volatile boolean stop = false;

        private SeedGenerator() {
        }

        @Override
        public void run() {
            while (!this.stop) {
                ++this.counter;
            }
        }

        public byte[] generateSeed(int numbytes, boolean fast) {
            Thread t = new Thread(this);
            byte[] result = new byte[numbytes];
            this.counter = 0;
            this.stop = false;
            int last = 0;
            t.start();
            int end = fast ? numbytes : numbytes * 8;
            for (int i = 0; i < end; ++i) {
                while (this.counter == last) {
                    try {
                        Thread.sleep(1L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
                last = this.counter;
                if (fast) {
                    result[i] = (byte)(last & 0xFF);
                    continue;
                }
                int bytepos = i / 8;
                result[bytepos] = (byte)(result[bytepos] << 1 | last & 1);
            }
            this.stop = true;
            return result;
        }
    }
}

