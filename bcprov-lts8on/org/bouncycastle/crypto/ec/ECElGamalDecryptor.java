/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.ec;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ec.ECDecryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECElGamalDecryptor
implements ECDecryptor {
    private ECPrivateKeyParameters key;

    @Override
    public void init(CipherParameters param) {
        if (!(param instanceof ECPrivateKeyParameters)) {
            throw new IllegalArgumentException("ECPrivateKeyParameters are required for decryption.");
        }
        this.key = (ECPrivateKeyParameters)param;
    }

    @Override
    public ECPoint decrypt(ECPair pair) {
        if (this.key == null) {
            throw new IllegalStateException("ECElGamalDecryptor not initialised");
        }
        ECCurve curve = this.key.getParameters().getCurve();
        ECPoint tmp = ECAlgorithms.cleanPoint(curve, pair.getX()).multiply(this.key.getD());
        return ECAlgorithms.cleanPoint(curve, pair.getY()).subtract(tmp).normalize();
    }
}

