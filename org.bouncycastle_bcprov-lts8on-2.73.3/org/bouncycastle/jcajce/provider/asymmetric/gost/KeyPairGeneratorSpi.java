/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.GOST3410KeyPairGenerator;
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.gost.BCGOST3410PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.gost.BCGOST3410PublicKey;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class KeyPairGeneratorSpi
extends KeyPairGenerator {
    GOST3410KeyGenerationParameters param;
    GOST3410KeyPairGenerator engine = new GOST3410KeyPairGenerator();
    GOST3410ParameterSpec gost3410Params;
    int strength = 1024;
    SecureRandom random = null;
    boolean initialised = false;

    public KeyPairGeneratorSpi() {
        super("GOST3410");
    }

    @Override
    public void initialize(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
    }

    private void init(GOST3410ParameterSpec gParams, SecureRandom random) {
        GOST3410PublicKeyParameterSetSpec spec = gParams.getPublicKeyParameters();
        this.param = new GOST3410KeyGenerationParameters(random, new GOST3410Parameters(spec.getP(), spec.getQ(), spec.getA()));
        this.engine.init(this.param);
        this.initialised = true;
        this.gost3410Params = gParams;
    }

    @Override
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof GOST3410ParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a GOST3410ParameterSpec");
        }
        this.init((GOST3410ParameterSpec)params, random);
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.init(new GOST3410ParameterSpec(CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.getId()), CryptoServicesRegistrar.getSecureRandom());
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        GOST3410PublicKeyParameters pub = (GOST3410PublicKeyParameters)pair.getPublic();
        GOST3410PrivateKeyParameters priv = (GOST3410PrivateKeyParameters)pair.getPrivate();
        return new KeyPair(new BCGOST3410PublicKey(pub, this.gost3410Params), new BCGOST3410PrivateKey(priv, this.gost3410Params));
    }
}

