/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

public class KeyFactorySpi
extends BaseKeyFactorySpi
implements AsymmetricKeyInfoConverter {
    String algorithm;
    ProviderConfiguration configuration;

    KeyFactorySpi(String algorithm, ProviderConfiguration configuration) {
        this.algorithm = algorithm;
        this.configuration = configuration;
    }

    @Override
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof ECPublicKey) {
            return new BCECPublicKey((ECPublicKey)key, this.configuration);
        }
        if (key instanceof java.security.interfaces.ECPrivateKey) {
            return new BCECPrivateKey((java.security.interfaces.ECPrivateKey)key, this.configuration);
        }
        throw new InvalidKeyException("key type unknown");
    }

    @Override
    protected KeySpec engineGetKeySpec(Key key, Class spec) throws InvalidKeySpecException {
        if ((spec.isAssignableFrom(KeySpec.class) || spec.isAssignableFrom(java.security.spec.ECPublicKeySpec.class)) && key instanceof ECPublicKey) {
            ECPublicKey k = (ECPublicKey)key;
            if (k.getParams() != null) {
                return new java.security.spec.ECPublicKeySpec(k.getW(), k.getParams());
            }
            ECParameterSpec implicitSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new java.security.spec.ECPublicKeySpec(k.getW(), EC5Util.convertSpec(EC5Util.convertCurve(implicitSpec.getCurve(), implicitSpec.getSeed()), implicitSpec));
        }
        if ((spec.isAssignableFrom(KeySpec.class) || spec.isAssignableFrom(ECPrivateKeySpec.class)) && key instanceof java.security.interfaces.ECPrivateKey) {
            java.security.interfaces.ECPrivateKey k = (java.security.interfaces.ECPrivateKey)key;
            if (k.getParams() != null) {
                return new ECPrivateKeySpec(k.getS(), k.getParams());
            }
            ECParameterSpec implicitSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPrivateKeySpec(k.getS(), EC5Util.convertSpec(EC5Util.convertCurve(implicitSpec.getCurve(), implicitSpec.getSeed()), implicitSpec));
        }
        if (spec.isAssignableFrom(ECPublicKeySpec.class) && key instanceof ECPublicKey) {
            ECPublicKey k = (ECPublicKey)key;
            if (k.getParams() != null) {
                return new ECPublicKeySpec(EC5Util.convertPoint(k.getParams(), k.getW()), EC5Util.convertSpec(k.getParams()));
            }
            ECParameterSpec implicitSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new ECPublicKeySpec(EC5Util.convertPoint(k.getParams(), k.getW()), implicitSpec);
        }
        if (spec.isAssignableFrom(org.bouncycastle.jce.spec.ECPrivateKeySpec.class) && key instanceof java.security.interfaces.ECPrivateKey) {
            java.security.interfaces.ECPrivateKey k = (java.security.interfaces.ECPrivateKey)key;
            if (k.getParams() != null) {
                return new org.bouncycastle.jce.spec.ECPrivateKeySpec(k.getS(), EC5Util.convertSpec(k.getParams()));
            }
            ECParameterSpec implicitSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            return new org.bouncycastle.jce.spec.ECPrivateKeySpec(k.getS(), implicitSpec);
        }
        if (spec.isAssignableFrom(OpenSSHPublicKeySpec.class) && key instanceof ECPublicKey) {
            if (key instanceof BCECPublicKey) {
                BCECPublicKey bcPk = (BCECPublicKey)key;
                ECParameterSpec sc = bcPk.getParameters();
                try {
                    return new OpenSSHPublicKeySpec(OpenSSHPublicKeyUtil.encodePublicKey(new ECPublicKeyParameters(bcPk.getQ(), new ECDomainParameters(sc.getCurve(), sc.getG(), sc.getN(), sc.getH(), sc.getSeed()))));
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("unable to produce encoding: " + e.getMessage());
                }
            }
            throw new IllegalArgumentException("invalid key type: " + key.getClass().getName());
        }
        if (spec.isAssignableFrom(OpenSSHPrivateKeySpec.class) && key instanceof java.security.interfaces.ECPrivateKey) {
            if (key instanceof BCECPrivateKey) {
                try {
                    return new OpenSSHPrivateKeySpec(PrivateKeyInfo.getInstance(key.getEncoded()).parsePrivateKey().toASN1Primitive().getEncoded());
                }
                catch (IOException e) {
                    throw new IllegalArgumentException("cannot encoded key: " + e.getMessage());
                }
            }
            throw new IllegalArgumentException("invalid key type: " + key.getClass().getName());
        }
        return super.engineGetKeySpec(key, spec);
    }

    @Override
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof org.bouncycastle.jce.spec.ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (org.bouncycastle.jce.spec.ECPrivateKeySpec)keySpec, this.configuration);
        }
        if (keySpec instanceof ECPrivateKeySpec) {
            return new BCECPrivateKey(this.algorithm, (ECPrivateKeySpec)keySpec, this.configuration);
        }
        if (keySpec instanceof OpenSSHPrivateKeySpec) {
            ECPrivateKey ecKey = ECPrivateKey.getInstance(((OpenSSHPrivateKeySpec)keySpec).getEncoded());
            try {
                return new BCECPrivateKey(this.algorithm, new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ecKey.getParametersObject()), ecKey), this.configuration);
            }
            catch (IOException e) {
                throw new InvalidKeySpecException("bad encoding: " + e.getMessage());
            }
        }
        return super.engineGeneratePrivate(keySpec);
    }

    @Override
    protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (ECPublicKeySpec)keySpec, this.configuration);
            }
            if (keySpec instanceof java.security.spec.ECPublicKeySpec) {
                return new BCECPublicKey(this.algorithm, (java.security.spec.ECPublicKeySpec)keySpec, this.configuration);
            }
            if (keySpec instanceof OpenSSHPublicKeySpec) {
                AsymmetricKeyParameter params = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)keySpec).getEncoded());
                if (params instanceof ECPublicKeyParameters) {
                    ECDomainParameters parameters = ((ECPublicKeyParameters)params).getParameters();
                    return this.engineGeneratePublic(new ECPublicKeySpec(((ECPublicKeyParameters)params).getQ(), new ECParameterSpec(parameters.getCurve(), parameters.getG(), parameters.getN(), parameters.getH(), parameters.getSeed())));
                }
                throw new IllegalArgumentException("openssh key is not ec public key");
            }
        }
        catch (Exception e) {
            throw new InvalidKeySpecException("invalid KeySpec: " + e.getMessage(), e);
        }
        return super.engineGeneratePublic(keySpec);
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (algOid.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPrivateKey(this.algorithm, keyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getAlgorithm().getAlgorithm();
        if (algOid.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            return new BCECPublicKey(this.algorithm, keyInfo, this.configuration);
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognised");
    }

    public static class EC
    extends KeyFactorySpi {
        public EC() {
            super("EC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECDH
    extends KeyFactorySpi {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECDHC
    extends KeyFactorySpi {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECDSA
    extends KeyFactorySpi {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECGOST3410
    extends KeyFactorySpi {
        public ECGOST3410() {
            super("ECGOST3410", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECGOST3410_2012
    extends KeyFactorySpi {
        public ECGOST3410_2012() {
            super("ECGOST3410-2012", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECMQV
    extends KeyFactorySpi {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}

