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
    public KeyPairGeneratorSpi(String string) {
        super(string);
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

        public EC(String string, ProviderConfiguration providerConfiguration) {
            super(string);
            this.algorithm = string;
            this.configuration = providerConfiguration;
        }

        public void initialize(int n, SecureRandom secureRandom) {
            this.strength = n;
            this.random = secureRandom;
            ECGenParameterSpec eCGenParameterSpec = (ECGenParameterSpec)ecParameters.get(Integers.valueOf(n));
            if (eCGenParameterSpec == null) {
                throw new InvalidParameterException("unknown key size.");
            }
            try {
                this.initialize(eCGenParameterSpec, secureRandom);
            }
            catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                throw new InvalidParameterException("key size not configurable.");
            }
        }

        public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            if (algorithmParameterSpec == null) {
                org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = this.configuration.getEcImplicitlyCa();
                if (eCParameterSpec == null) {
                    throw new InvalidAlgorithmParameterException("null parameter passed but no implicitCA set");
                }
                this.ecParams = null;
                this.param = this.createKeyGenParamsBC(eCParameterSpec, secureRandom);
            } else if (algorithmParameterSpec instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
                this.ecParams = algorithmParameterSpec;
                this.param = this.createKeyGenParamsBC((org.bouncycastle.jce.spec.ECParameterSpec)algorithmParameterSpec, secureRandom);
            } else if (algorithmParameterSpec instanceof ECParameterSpec) {
                this.ecParams = algorithmParameterSpec;
                this.param = this.createKeyGenParamsJCE((ECParameterSpec)algorithmParameterSpec, secureRandom);
            } else if (algorithmParameterSpec instanceof ECGenParameterSpec) {
                this.initializeNamedCurve(((ECGenParameterSpec)algorithmParameterSpec).getName(), secureRandom);
            } else if (algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
                this.initializeNamedCurve(((ECNamedCurveGenParameterSpec)algorithmParameterSpec).getName(), secureRandom);
            } else {
                String string = ECUtil.getNameFrom(algorithmParameterSpec);
                if (string != null) {
                    this.initializeNamedCurve(string, secureRandom);
                } else {
                    throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + algorithmParameterSpec);
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }

        public KeyPair generateKeyPair() {
            if (!this.initialised) {
                this.initialize(this.strength, new SecureRandom());
            }
            AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.engine.generateKeyPair();
            ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)asymmetricCipherKeyPair.getPublic();
            ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)asymmetricCipherKeyPair.getPrivate();
            if (this.ecParams instanceof org.bouncycastle.jce.spec.ECParameterSpec) {
                org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec = (org.bouncycastle.jce.spec.ECParameterSpec)this.ecParams;
                BCECPublicKey bCECPublicKey = new BCECPublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec, this.configuration);
                return new KeyPair(bCECPublicKey, new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, bCECPublicKey, eCParameterSpec, this.configuration));
            }
            if (this.ecParams == null) {
                return new KeyPair(new BCECPublicKey(this.algorithm, eCPublicKeyParameters, this.configuration), new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, this.configuration));
            }
            ECParameterSpec eCParameterSpec = (ECParameterSpec)this.ecParams;
            BCECPublicKey bCECPublicKey = new BCECPublicKey(this.algorithm, eCPublicKeyParameters, eCParameterSpec, this.configuration);
            return new KeyPair(bCECPublicKey, new BCECPrivateKey(this.algorithm, eCPrivateKeyParameters, bCECPublicKey, eCParameterSpec, this.configuration));
        }

        protected ECKeyGenerationParameters createKeyGenParamsBC(org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec, SecureRandom secureRandom) {
            return new ECKeyGenerationParameters(new ECDomainParameters(eCParameterSpec.getCurve(), eCParameterSpec.getG(), eCParameterSpec.getN(), eCParameterSpec.getH()), secureRandom);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(ECParameterSpec eCParameterSpec, SecureRandom secureRandom) {
            Object object;
            Object object2;
            if (eCParameterSpec instanceof ECNamedCurveSpec && null != (object2 = ECUtils.getDomainParametersFromName((String)(object = ((ECNamedCurveSpec)eCParameterSpec).getName()), this.configuration))) {
                return this.createKeyGenParamsJCE((X9ECParameters)object2, secureRandom);
            }
            object = EC5Util.convertCurve(eCParameterSpec.getCurve());
            object2 = EC5Util.convertPoint((ECCurve)object, eCParameterSpec.getGenerator());
            BigInteger bigInteger = eCParameterSpec.getOrder();
            BigInteger bigInteger2 = BigInteger.valueOf(eCParameterSpec.getCofactor());
            ECDomainParameters eCDomainParameters = new ECDomainParameters((ECCurve)object, (ECPoint)object2, bigInteger, bigInteger2);
            return new ECKeyGenerationParameters(eCDomainParameters, secureRandom);
        }

        protected ECKeyGenerationParameters createKeyGenParamsJCE(X9ECParameters x9ECParameters, SecureRandom secureRandom) {
            ECDomainParameters eCDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH());
            return new ECKeyGenerationParameters(eCDomainParameters, secureRandom);
        }

        protected void initializeNamedCurve(String string, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            X9ECParameters x9ECParameters = ECUtils.getDomainParametersFromName(string, this.configuration);
            if (null == x9ECParameters) {
                throw new InvalidAlgorithmParameterException("unknown curve name: " + string);
            }
            byte[] byArray = null;
            this.ecParams = new ECNamedCurveSpec(string, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), byArray);
            this.param = this.createKeyGenParamsJCE(x9ECParameters, secureRandom);
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

