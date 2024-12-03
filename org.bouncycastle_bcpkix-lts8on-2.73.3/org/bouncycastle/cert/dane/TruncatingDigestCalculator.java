/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cert.dane;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

public class TruncatingDigestCalculator
implements DigestCalculator {
    private final DigestCalculator baseCalculator;
    private final int length;

    public TruncatingDigestCalculator(DigestCalculator baseCalculator) {
        this(baseCalculator, 28);
    }

    public TruncatingDigestCalculator(DigestCalculator baseCalculator, int length) {
        this.baseCalculator = baseCalculator;
        this.length = length;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.baseCalculator.getAlgorithmIdentifier();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.baseCalculator.getOutputStream();
    }

    @Override
    public byte[] getDigest() {
        byte[] rv = new byte[this.length];
        byte[] dig = this.baseCalculator.getDigest();
        System.arraycopy(dig, 0, rv, 0, rv.length);
        return rv;
    }
}

