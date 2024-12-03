/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

public class NullDigest
implements Digest {
    private OpenByteArrayOutputStream bOut = new OpenByteArrayOutputStream();

    @Override
    public String getAlgorithmName() {
        return "NULL";
    }

    @Override
    public int getDigestSize() {
        return this.bOut.size();
    }

    @Override
    public void update(byte in) {
        this.bOut.write(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.bOut.write(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        int size = this.bOut.size();
        this.bOut.copy(out, outOff);
        this.reset();
        return size;
    }

    @Override
    public void reset() {
        this.bOut.reset();
    }

    private static class OpenByteArrayOutputStream
    extends ByteArrayOutputStream {
        private OpenByteArrayOutputStream() {
        }

        @Override
        public void reset() {
            super.reset();
            Arrays.clear(this.buf);
        }

        void copy(byte[] out, int outOff) {
            System.arraycopy(this.buf, 0, out, outOff, this.size());
        }
    }
}

