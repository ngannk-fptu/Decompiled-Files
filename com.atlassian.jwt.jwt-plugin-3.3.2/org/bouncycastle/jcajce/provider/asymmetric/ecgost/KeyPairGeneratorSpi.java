/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
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
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.BCECGOST3410PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.BCECGOST3410PublicKey;
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
    String algorithm = "ECGOST3410";
    ECKeyGenerationParameters param;
    int strength = 239;
    SecureRandom random = null;
    boolean initialised = false;

    public KeyPairGeneratorSpi() {
        super("ECGOST3410");
    }

    public void initialize(int n, SecureRandom secureRandom) {
        this.strength = n;
        this.random = secureRandom;
        if (this.ecParams != null) {
            try {
                this.initialize((ECGenParameterSpec)this.ecParams, secureRandom);
            }
            catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                throw new InvalidParameterException("key size not configurable.");
            }
        } else {
            throw new InvalidParameterException("unknown key size.");
        }
    }

    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof GOST3410ParameterSpec) {
            GOST3410ParameterSpec gOST3410ParameterSpec = (GOST3410ParameterSpec)algorithmParameterSpec;
            this.init(gOST3410ParameterSpec, secureRandom);
        } else if (algorithmParameterSpec instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
            org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = (org.bouncycastle.jce.spec.ECParameterSpec)algorithmParameterSpec;
            this.ecParams = algorithmParameterSpec;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        } else if (algorithmParameterSpec instanceof ECParameterSpec) {
            ECParameterSpec eCParameterSpec = (ECParameterSpec)algorithmParameterSpec;
            this.ecParams = algorithmParameterSpec;
            ECCurve eCCurve = EC5Util.convertCurve(eCParameterSpec.getCurve());
            ECPoint eCPoint = EC5Util.convertPoint(eCCurve, eCParameterSpec.getGenerator());
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCCurve, eCPoint, eCParameterSpec.getOrder(), BigInteger.valueOf(eCParameterSpec.getCofactor())), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        } else if (algorithmParameterSpec instanceof ECGenParameterSpec || algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
            String string = algorithmParameterSpec instanceof ECGenParameterSpec ? ((ECGenParameterSpec)algorithmParameterSpec).getName() : ((ECNamedCurveGenParameterSpec)algorithmParameterSpec).getName();
            this.init(new GOST3410ParameterSpec(string), secureRandom);
        } else if (algorithmParameterSpec == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() != null) {
            org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            this.ecParams = algorithmParameterSpec;
            this.param = new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), secureRandom);
            this.engine.init(this.param);
            this.initialised = true;
        } else {
            if (algorithmParameterSpec == null && BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa() == null) {
                throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
            }
            throw new InvalidAlgorithmParameterException("parameter object not a ECParameterSpec: " + algorithmParameterSpec.getClass().getName());
        }
    }

    private void init(GOST3410ParameterSpec gOST3410ParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = gOST3410ParameterSpec.getPublicKeyParamSet();
        X9ECParameters x9ECParameters = ECGOST3410NamedCurves.getByOIDX9(aSN1ObjectIdentifier);
        if (x9ECParameters == null) {
            throw new InvalidAlgorithmParameterException("unknown curve: " + aSN1ObjectIdentifier);
        }
        this.ecParams = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(aSN1ObjectIdentifier), x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
        this.param = new ECKeyGenerationParameters(new ECGOST3410Parameters(new ECNamedDomainParameters(aSN1ObjectIdentifier, x9ECParameters), aSN1ObjectIdentifier, gOST3410ParameterSpec.getDigestParamSet(), gOST3410ParameterSpec.getEncryptionParamSet()), secureRandom);
        this.engine.init(this.param);
        this.initialised = true;
    }

    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            throw new IllegalStateException("EC Key Pair Generator not initialised");
        }
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
        ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
        if (this.ecParams instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
            org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecParams;
            BCECGOST3410PublicKey bCECGOST3410PublicKey = new BCECGOST3410PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec);
            return new KeyPair(bCECGOST3410PublicKey, new BCECGOST3410PrivateKey(this.algorithm, eCPrivateKeyParameters, bCECGOST3410PublicKey, eCParameterSpec));
        }
        if (this.ecParams == null) {
            return new KeyPair(new BCECGOST3410PublicKey(this.algorithm, eCPublicKeyParameters), new BCECGOST3410PrivateKey(this.algorithm, eCPrivateKeyParameters));
        }
        ECParameterSpec eCParameterSpec = (ECParameterSpec)this.ecParams;
        BCECGOST3410PublicKey bCECGOST3410PublicKey = new BCECGOST3410PublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec);
        return new KeyPair(bCECGOST3410PublicKey, new BCECGOST3410PrivateKey(this.algorithm, eCPrivateKeyParameters, bCECGOST3410PublicKey, eCParameterSpec));
    }
}

