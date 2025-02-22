/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.util.Pack;

public class OldIESEngine
extends IESEngine {
    public OldIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac) {
        super(agree, kdf, mac);
    }

    public OldIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, BufferedBlockCipher cipher) {
        super(agree, kdf, mac, cipher);
    }

    @Override
    protected byte[] getLengthTag(byte[] p2) {
        byte[] L2 = new byte[4];
        if (p2 != null) {
            Pack.intToBigEndian(p2.length * 8, L2, 0);
        }
        return L2;
    }
}

