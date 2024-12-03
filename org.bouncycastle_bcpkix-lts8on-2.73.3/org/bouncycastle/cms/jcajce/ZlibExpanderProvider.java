/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.io.StreamOverflowException
 */
package org.bouncycastle.cms.jcajce;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;
import org.bouncycastle.util.io.StreamOverflowException;

public class ZlibExpanderProvider
implements InputExpanderProvider {
    private final long limit;

    public ZlibExpanderProvider() {
        this.limit = -1L;
    }

    public ZlibExpanderProvider(long limit) {
        this.limit = limit;
    }

    @Override
    public InputExpander get(final AlgorithmIdentifier algorithm) {
        return new InputExpander(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
            }

            @Override
            public InputStream getInputStream(InputStream comIn) {
                FilterInputStream s = new InflaterInputStream(comIn);
                if (ZlibExpanderProvider.this.limit >= 0L) {
                    s = new LimitedInputStream(s, ZlibExpanderProvider.this.limit);
                }
                return s;
            }
        };
    }

    private static class LimitedInputStream
    extends FilterInputStream {
        private long remaining;

        public LimitedInputStream(InputStream input, long limit) {
            super(input);
            this.remaining = limit;
        }

        @Override
        public int read() throws IOException {
            int b;
            if (this.remaining >= 0L && ((b = this.in.read()) < 0 || --this.remaining >= 0L)) {
                return b;
            }
            throw new StreamOverflowException("expanded byte limit exceeded");
        }

        @Override
        public int read(byte[] buf, int off, int len) throws IOException {
            if (len < 1) {
                return super.read(buf, off, len);
            }
            if (this.remaining < 1L) {
                this.read();
                return -1;
            }
            int actualLen = this.remaining > (long)len ? len : (int)this.remaining;
            int numRead = this.in.read(buf, off, actualLen);
            if (numRead > 0) {
                this.remaining -= (long)numRead;
            }
            return numRead;
        }
    }
}

