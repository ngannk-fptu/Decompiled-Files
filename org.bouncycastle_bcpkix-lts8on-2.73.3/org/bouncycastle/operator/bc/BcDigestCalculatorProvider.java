/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.Digest
 *  org.bouncycastle.crypto.ExtendedDigest
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.operator.bc.BcDigestProvider;

public class BcDigestCalculatorProvider
implements DigestCalculatorProvider {
    private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    @Override
    public DigestCalculator get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
        ExtendedDigest dig = this.digestProvider.get(algorithm);
        final DigestOutputStream stream = new DigestOutputStream((Digest)dig);
        return new DigestCalculator(){

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
            }

            @Override
            public OutputStream getOutputStream() {
                return stream;
            }

            @Override
            public byte[] getDigest() {
                return stream.getDigest();
            }
        };
    }

    private static class DigestOutputStream
    extends OutputStream {
        private Digest dig;

        DigestOutputStream(Digest dig) {
            this.dig = dig;
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            this.dig.update(bytes, off, len);
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            this.dig.update(bytes, 0, bytes.length);
        }

        @Override
        public void write(int b) throws IOException {
            this.dig.update((byte)b);
        }

        byte[] getDigest() {
            byte[] d = new byte[this.dig.getDigestSize()];
            this.dig.doFinal(d, 0);
            return d;
        }
    }
}

