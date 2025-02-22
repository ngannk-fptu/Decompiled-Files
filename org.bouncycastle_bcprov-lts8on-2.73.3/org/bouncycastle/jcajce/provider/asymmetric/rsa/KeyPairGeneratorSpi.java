/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;

public class KeyPairGeneratorSpi
extends KeyPairGenerator {
    private static final AlgorithmIdentifier PKCS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    private static final AlgorithmIdentifier PSS_ALGID = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS);
    static final BigInteger defaultPublicExponent = BigInteger.valueOf(65537L);
    RSAKeyGenerationParameters param;
    RSAKeyPairGenerator engine;
    AlgorithmIdentifier algId;

    public KeyPairGeneratorSpi(String algorithmName, AlgorithmIdentifier algId) {
        super(algorithmName);
        this.algId = algId;
        this.engine = new RSAKeyPairGenerator();
        this.param = new RSAKeyGenerationParameters(defaultPublicExponent, CryptoServicesRegistrar.getSecureRandom(), 2048, PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.engine.init(this.param);
    }

    public KeyPairGeneratorSpi() {
        this("RSA", PKCS_ALGID);
    }

    @Override
    public void initialize(int strength, SecureRandom random) {
        this.param = new RSAKeyGenerationParameters(defaultPublicExponent, random, strength, PrimeCertaintyCalculator.getDefaultCertainty(strength));
        this.engine.init(this.param);
    }

    @Override
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof RSAKeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a RSAKeyGenParameterSpec");
        }
        RSAKeyGenParameterSpec rsaParams = (RSAKeyGenParameterSpec)params;
        this.param = new RSAKeyGenerationParameters(rsaParams.getPublicExponent(), random, rsaParams.getKeysize(), PrimeCertaintyCalculator.getDefaultCertainty(2048));
        this.engine.init(this.param);
    }

    @Override
    public KeyPair generateKeyPair() {
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        RSAKeyParameters pub = (RSAKeyParameters)pair.getPublic();
        RSAPrivateCrtKeyParameters priv = (RSAPrivateCrtKeyParameters)pair.getPrivate();
        return new KeyPair(new BCRSAPublicKey(this.algId, pub), new BCRSAPrivateCrtKey(this.algId, priv));
    }

    public static class PSS
    extends KeyPairGeneratorSpi {
        public PSS() {
            super("RSASSA-PSS", PSS_ALGID);
        }
    }
}

