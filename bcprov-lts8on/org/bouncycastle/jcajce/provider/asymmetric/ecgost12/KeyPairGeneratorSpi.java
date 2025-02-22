/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECGOST3410Parameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost12.BCECGOST3410_2012PublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class KeyPairGeneratorSpi
extends KeyPairGenerator {
    Object ecParams = null;
    ECKeyPairGenerator engine = new ECKeyPairGenerator();
    String algorithm = "ECGOST3410-2012";
    ECKeyGenerationParameters param;
    int strength = 239;
    SecureRandom random = null;
    boolean initialised = false;

    public KeyPairGeneratorSpi() {
        super("ECGOST3410-2012");
    }

    @Override
    public void initialize(int strength, SecureRandom random) {
        this.strength = strength;
        this.random = random;
        if (this.ecParams != null) {
            try {
                this.initialize((ECGenParameterSpec)this.ecParams, random);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw new InvalidParameterException("key size not configurable.");
            }
        } else {
            throw new InvalidParameterException("unknown key size.");
        }
    }

    @Override
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (params instanceof GOST3410ParameterSpec) {
            GOST3410ParameterSpec gostParams = (GOST3410ParameterSpec)params;
            this.init(gostParams, random);
        } else if (params instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
            org.bouncycastle.jce.spec.ECParameterSpec p = (org.bouncycastle.jce.spec.ECParameterSpec)params;
            this.ecParams = params;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), random);
            this.engine.init(this.param);
            this.initialised = true;
        } else if (params instanceof ECParameterSpec) {
            ECParameterSpec p = (ECParameterSpec)params;
            this.ecParams = params;
            ECCurve curve = EC5Util.convertCurve(p.getCurve());
            ECPoint g = EC5Util.convertPoint(curve, p.getGenerator());
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(curve, g, p.getOrder(), BigInteger.valueOf(p.getCofactor())), random);
            this.engine.init(this.param);
            this.initialised = true;
        } else if (params instanceof ECGenParameterSpec || params instanceof ECNamedCurveGenParameterSpec) {
            String curveName = params instanceof ECGenParameterSpec ? ((ECGenParameterSpec)params).getName() : ((ECNamedCurveGenParameterSpec)params).getName();
            this.init(new GOST3410ParameterSpec(curveName), random);
        } else if (params == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
            org.bouncycastle.jce.spec.ECParameterSpec p = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.ecParams = params;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), random);
            this.engine.init(this.param);
            this.initialised = true;
        } else {
            if (params == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
                throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }
            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + params.getClass().getName());
        }
    }

    private void init(GOST3410ParameterSpec gostParams, SecureRandom random) throws InvalidAlgorithmParameterException {
        X9ECParameters ecP = ECGOST3410NamedCurves.getByOIDX9(gostParams.getPublicKeyParamSet());
        if (ecP == null) {
            throw new InvalidAlgorithmParameterException("unknown curve: " + gostParams.getPublicKeyParamSet());
        }
        this.ecParams = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gostParams.getPublicKeyParamSet()), ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
        this.param = new ECKeyGenerationParameters(new ECGOST3410Parameters(new ECNamedDomainParameters(gostParams.getPublicKeyParamSet(), ecP), gostParams.getPublicKeyParamSet(), gostParams.getDigestParamSet(), gostParams.getEncryptionParamSet()), random);
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            throw new IllegalStateException("EC Key Pair Generator not initialised");
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        ECPublicKeyParameters pub = (ECPublicKeyParameters)pair.getPublic();
        ECPrivateKeyParameters priv = (ECPrivateKeyParameters)pair.getPrivate();
        if (this.ecParams instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
            org.bouncycastle.jce.spec.ECParameterSpec p = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecParams;
            BCECGOST3410_2012PublicKey pubKey = new BCECGOST3410_2012PublicKey(this.algorithm, pub, p);
            return new KeyPair(pubKey, new BCECGOST3410_2012PrivateKey(this.algorithm, priv, pubKey, p));
        }
        if (this.ecParams == null) {
            return new KeyPair(new BCECGOST3410_2012PublicKey(this.algorithm, pub), new BCECGOST3410_2012PrivateKey(this.algorithm, priv));
        }
        ECParameterSpec p = (ECParameterSpec)this.ecParams;
        BCECGOST3410_2012PublicKey pubKey = new BCECGOST3410_2012PublicKey(this.algorithm, pub, p);
        return new KeyPair(pubKey, new BCECGOST3410_2012PrivateKey(this.algorithm, priv, pubKey, p));
    }
}

