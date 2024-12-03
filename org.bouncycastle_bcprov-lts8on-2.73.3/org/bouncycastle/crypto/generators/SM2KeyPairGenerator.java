/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.util.BigIntegers;

public class SM2KeyPairGenerator
extends ECKeyPairGenerator {
    public SM2KeyPairGenerator() {
        super("SM2KeyGen");
    }

    @Override
    protected boolean isOutOfRangeD(BigInteger d, BigInteger n) {
        return d.compareTo(ONE) < 0 || d.compareTo(n.subtract(BigIntegers.ONE)) >= 0;
    }
}

