/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.BDS;
import org.bouncycastle.pqc.crypto.xmss.OTSHashAddress;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSNode;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

public final class XMSSKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private XMSSParameters params;
    private SecureRandom prng;

    public void init(KeyGenerationParameters keyGenerationParameters) {
        XMSSKeyGenerationParameters xMSSKeyGenerationParameters = (XMSSKeyGenerationParameters)keyGenerationParameters;
        this.prng = xMSSKeyGenerationParameters.getRandom();
        this.params = xMSSKeyGenerationParameters.getParameters();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        XMSSPrivateKeyParameters xMSSPrivateKeyParameters = this.generatePrivateKey(this.params, this.prng);
        XMSSNode xMSSNode = xMSSPrivateKeyParameters.getBDSState().getRoot();
        xMSSPrivateKeyParameters = new XMSSPrivateKeyParameters.Builder(this.params).withSecretKeySeed(xMSSPrivateKeyParameters.getSecretKeySeed()).withSecretKeyPRF(xMSSPrivateKeyParameters.getSecretKeyPRF()).withPublicSeed(xMSSPrivateKeyParameters.getPublicSeed()).withRoot(xMSSNode.getValue()).withBDSState(xMSSPrivateKeyParameters.getBDSState()).build();
        XMSSPublicKeyParameters xMSSPublicKeyParameters = new XMSSPublicKeyParameters.Builder(this.params).withRoot(xMSSNode.getValue()).withPublicSeed(xMSSPrivateKeyParameters.getPublicSeed()).build();
        return new AsymmetricCipherKeyPair(xMSSPublicKeyParameters, xMSSPrivateKeyParameters);
    }

    private XMSSPrivateKeyParameters generatePrivateKey(XMSSParameters xMSSParameters, SecureRandom secureRandom) {
        int n = xMSSParameters.getTreeDigestSize();
        byte[] byArray = new byte[n];
        secureRandom.nextBytes(byArray);
        byte[] byArray2 = new byte[n];
        secureRandom.nextBytes(byArray2);
        byte[] byArray3 = new byte[n];
        secureRandom.nextBytes(byArray3);
        XMSSPrivateKeyParameters xMSSPrivateKeyParameters = new XMSSPrivateKeyParameters.Builder(xMSSParameters).withSecretKeySeed(byArray).withSecretKeyPRF(byArray2).withPublicSeed(byArray3).withBDSState(new BDS(xMSSParameters, byArray3, byArray, (OTSHashAddress)new OTSHashAddress.Builder().build())).build();
        return xMSSPrivateKeyParameters;
    }
}

