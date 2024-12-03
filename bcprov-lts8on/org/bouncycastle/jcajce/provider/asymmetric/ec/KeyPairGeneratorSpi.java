/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Integers;

public abstract class KeyPairGeneratorSpi
extends KeyPairGenerator {
    public KeyPairGeneratorSpi(String algorithmName) {
        super(algorithmName);
    }

    public static class EC
    extends KeyPairGeneratorSpi {
        ECKeyGenerationParameters param;
        ECKeyPairGenerator engine = new ECKeyPairGenerator();
        Object ecParams = null;
        int strength = 239;
        SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
        boolean initialised = false;
        String algorithm;
        ProviderConfiguration configuration;
        private static Hashtable ecParameters = new Hashtable();

        public EC() {
            super("EC");
            this.algorithm = "EC";
            this.configuration = BouncyCastleProvider.CONFIGURATION;
        }

        public EC(String algorithm, ProviderConfiguration configuration) {
            super(algorithm);
            this.algorithm = algorithm;
            this.configuration = configuration;
        }

        @Override
        public void initialize(int strength, SecureRandom random) {
            this.strength = strength;
            this.random = random;
            ECGenParameterSpec ecParams = (ECGenParameterSpec)ecParameters.get(Integers.valueOf(strength));
            if (ecParams == null) {
                throw new InvalidParameterException("unknown key size.");
            }
            try {
                this.initialize(ecParams, random);
            }
            catch (InvalidAlgorithmParameterException e) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }

        @Override
        public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
            if (params == null) {
                org.bouncycastle.jce.spec.ECParameterSpec implicitCA = this.configuration.getEcImplicitlyCa();
                if (implicitCA == null) {
                    throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
                }
                this.ecParams = null;
                this.param = this.createKeyGenParamsBC(implicitCA, random);
            } else if (params instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
                this.ecParams = params;
                this.param = this.createKeyGenParamsBC((org.bouncycastle.jce.spec.ECParameterSpec)params, random);
            } else if (params instanceof ECParameterSpec) {
                this.ecParams = params;
                this.param = this.createKeyGenParamsJCE((ECParameterSpec)params, random);
            } else if (params instanceof ECGenParameterSpec) {
                this.initializeNamedCurve(((ECGenParameterSpec)params).getName(), random);
            } else if (params instanceof ECNamedCurveGenParameterSpec) {
                this.initializeNamedCurve(((ECNamedCurveGenParameterSpec)params).getName(), random);
            } else {
                String name = ECUtil.getNameFrom(params);
                if (name != null) {
                    this.initializeNamedCurve(name, random);
                } else {
                    throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + params);
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }

        @Override
        public KeyPair generateKeyPair() {
            if (!this.initialised) {
                this.initialize(this.strength, new SecureRandom());
            }
            AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
            ECPublicKeyParameters pub = (ECPublicKeyParameters)pair.getPublic();
            ECPrivateKeyParameters priv = (ECPrivateKeyParameters)pair.getPrivate();
            if (this.ecParams instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
                org.bouncycastle.jce.spec.ECParameterSpec p = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecParams;
                BCECPublicKey pubKey = new BCECPublicKey(this.algorithm, pub, p, this.configuration);
                return new KeyPair(pubKey, new BCECPrivateKey(this.algorithm, priv, pubKey, p, this.configuration));
            }
            if (this.ecParams == null) {
                return new KeyPair(new BCECPublicKey(this.algorithm, pub, this.configuration), new BCECPrivateKey(this.algorithm, priv, this.configuration));
            }
            ECParameterSpec p = (ECParameterSpec)this.ecParams;
            BCECPublicKey pubKey = new BCECPublicKey(this.algorithm, pub, p, this.configuration);
            return new KeyPair(pubKey, new BCECPrivateKey(this.algorithm, priv, pubKey, p, this.configuration));
        }

        protected ECKeyGenerationParameters createKeyGenParamsBC(org.bouncycastle.jce.spec.ECParameterSpec p, SecureRandom r) {
            return new ECKeyGenerationParameters(new ECDomainParameters(p.getCurve(), p.getG(), p.getN(), p.getH()), r);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(ECParameterSpec p, SecureRandom r) {
            String curveName;
            X9ECParameters x9;
            if (p instanceof ECNamedCurveSpec && null != (x9 = ECUtils.getDomainParametersFromName(curveName = ((ECNamedCurveSpec)p).getName(), this.configuration))) {
                return this.createKeyGenParamsJCE(x9, r);
            }
            ECCurve curve = EC5Util.convertCurve(p.getCurve());
            ECPoint g = EC5Util.convertPoint(curve, p.getGenerator());
            BigInteger n = p.getOrder();
            BigInteger h = BigInteger.valueOf(p.getCofactor());
            ECDomainParameters dp = new ECDomainParameters(curve, g, n, h);
            return new ECKeyGenerationParameters(dp, r);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(X9ECParameters x9, SecureRandom r) {
            ECDomainParameters dp = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH());
            return new ECKeyGenerationParameters(dp, r);
        }

        protected void initializeNamedCurve(String curveName, SecureRandom random) throws InvalidAlgorithmParameterException {
            X9ECParameters x9 = ECUtils.getDomainParametersFromName(curveName, this.configuration);
            if (null == x9) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + curveName);
            }
            byte[] seed = null;
            this.ecParams = new ECNamedCurveSpec(curveName, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), seed);
            this.param = this.createKeyGenParamsJCE(x9, random);
        }

        static {
            ecParameters.put(Integers.valueOf(192), new ECGenParameterSpec("prime192v1"));
            ecParameters.put(Integers.valueOf(239), new ECGenParameterSpec("prime239v1"));
            ecParameters.put(Integers.valueOf(256), new ECGenParameterSpec("prime256v1"));
            ecParameters.put(Integers.valueOf(224), new ECGenParameterSpec("P-224"));
            ecParameters.put(Integers.valueOf(384), new ECGenParameterSpec("P-384"));
            ecParameters.put(Integers.valueOf(521), new ECGenParameterSpec("P-521"));
        }
    }

    public static class ECDH
    extends EC {
        public ECDH() {
            super("ECDH", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECDHC
    extends EC {
        public ECDHC() {
            super("ECDHC", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECDSA
    extends EC {
        public ECDSA() {
            super("ECDSA", BouncyCastleProvider.CONFIGURATION);
        }
    }

    public static class ECMQV
    extends EC {
        public ECMQV() {
            super("ECMQV", BouncyCastleProvider.CONFIGURATION);
        }
    }
}

