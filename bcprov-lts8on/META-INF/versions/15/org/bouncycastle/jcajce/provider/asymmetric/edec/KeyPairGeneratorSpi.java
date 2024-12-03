/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.NamedParameterSpec;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.Ed448KeyPairGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed448KeyGenerationParameters;
import org.bouncycastle.crypto.params.X25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.X448KeyGenerationParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BC11XDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BC11XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BC15EdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BC15EdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class KeyPairGeneratorSpi
extends java.security.KeyPairGeneratorSpi {
    private static final int EdDSA = -1;
    private static final int XDH = -2;
    private static final int Ed25519 = 1;
    private static final int Ed448 = 2;
    private static final int X25519 = 3;
    private static final int X448 = 4;
    private final int algorithmDeclared;
    private int algorithmInitialized;
    private SecureRandom secureRandom;
    private AsymmetricCipherKeyPairGenerator generator;

    KeyPairGeneratorSpi(int algorithmDeclared) {
        this.algorithmDeclared = algorithmDeclared;
        if (KeyPairGeneratorSpi.getAlgorithmFamily(algorithmDeclared) != algorithmDeclared) {
            this.algorithmInitialized = algorithmDeclared;
        }
    }

    @Override
    public void initialize(int strength, SecureRandom secureRandom) {
        int algorithm;
        this.algorithmInitialized = algorithm = this.getAlgorithmForStrength(strength);
        this.secureRandom = secureRandom;
        this.generator = null;
    }

    @Override
    public void initialize(AlgorithmParameterSpec paramSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String name = KeyPairGeneratorSpi.getNameFromParams(paramSpec);
        if (null == name) {
            throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + paramSpec);
        }
        int algorithm = KeyPairGeneratorSpi.getAlgorithmForName(name);
        if (this.algorithmDeclared != algorithm && this.algorithmDeclared != KeyPairGeneratorSpi.getAlgorithmFamily(algorithm)) {
            throw new InvalidAlgorithmParameterException("parameterSpec for wrong curve type");
        }
        this.algorithmInitialized = algorithm;
        this.secureRandom = secureRandom;
        this.generator = null;
    }

    @Override
    public KeyPair generateKeyPair() {
        if (this.algorithmInitialized == 0) {
            throw new IllegalStateException("generator not correctly initialized");
        }
        if (null == this.generator) {
            this.generator = this.setupGenerator();
        }
        AsymmetricCipherKeyPair kp = this.generator.generateKeyPair();
        switch (this.algorithmInitialized) {
            case 1: 
            case 2: {
                return new KeyPair(new BC15EdDSAPublicKey(kp.getPublic()), new BC15EdDSAPrivateKey(kp.getPrivate()));
            }
            case 3: 
            case 4: {
                return new KeyPair(new BC11XDHPublicKey(kp.getPublic()), new BC11XDHPrivateKey(kp.getPrivate()));
            }
        }
        throw new IllegalStateException("generator not correctly initialized");
    }

    private int getAlgorithmForStrength(int strength) {
        switch (strength) {
            case 255: 
            case 256: {
                switch (this.algorithmDeclared) {
                    case -1: 
                    case 1: {
                        return 1;
                    }
                    case -2: 
                    case 3: {
                        return 3;
                    }
                }
                throw new InvalidParameterException("key size not configurable");
            }
            case 448: {
                switch (this.algorithmDeclared) {
                    case -1: 
                    case 2: {
                        return 2;
                    }
                    case -2: 
                    case 4: {
                        return 4;
                    }
                }
                throw new InvalidParameterException("key size not configurable");
            }
        }
        throw new InvalidParameterException("unknown key size");
    }

    private AsymmetricCipherKeyPairGenerator setupGenerator() {
        if (null == this.secureRandom) {
            this.secureRandom = CryptoServicesRegistrar.getSecureRandom();
        }
        switch (this.algorithmInitialized) {
            case 1: {
                Ed25519KeyPairGenerator generator = new Ed25519KeyPairGenerator();
                generator.init(new Ed25519KeyGenerationParameters(this.secureRandom));
                return generator;
            }
            case 2: {
                Ed448KeyPairGenerator generator = new Ed448KeyPairGenerator();
                generator.init(new Ed448KeyGenerationParameters(this.secureRandom));
                return generator;
            }
            case 3: {
                X25519KeyPairGenerator generator = new X25519KeyPairGenerator();
                generator.init(new X25519KeyGenerationParameters(this.secureRandom));
                return generator;
            }
            case 4: {
                X448KeyPairGenerator generator = new X448KeyPairGenerator();
                generator.init(new X448KeyGenerationParameters(this.secureRandom));
                return generator;
            }
        }
        throw new IllegalStateException("generator not correctly initialized");
    }

    private static int getAlgorithmFamily(int algorithm) {
        switch (algorithm) {
            case 1: 
            case 2: {
                return -1;
            }
            case 3: 
            case 4: {
                return -2;
            }
        }
        return algorithm;
    }

    private static int getAlgorithmForName(String name) throws InvalidAlgorithmParameterException {
        if (name.equalsIgnoreCase("X25519") || name.equals(EdECObjectIdentifiers.id_X25519.getId())) {
            return 3;
        }
        if (name.equalsIgnoreCase("Ed25519") || name.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
            return 1;
        }
        if (name.equalsIgnoreCase("X448") || name.equals(EdECObjectIdentifiers.id_X448.getId())) {
            return 4;
        }
        if (name.equalsIgnoreCase("Ed448") || name.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
            return 2;
        }
        throw new InvalidAlgorithmParameterException("invalid parameterSpec name: " + name);
    }

    private static String getNameFromParams(AlgorithmParameterSpec paramSpec) throws InvalidAlgorithmParameterException {
        if (paramSpec instanceof ECGenParameterSpec) {
            return ((ECGenParameterSpec)paramSpec).getName();
        }
        if (paramSpec instanceof ECNamedCurveGenParameterSpec) {
            return ((ECNamedCurveGenParameterSpec)paramSpec).getName();
        }
        if (paramSpec instanceof NamedParameterSpec) {
            return ((NamedParameterSpec)paramSpec).getName();
        }
        if (paramSpec instanceof EdDSAParameterSpec) {
            return ((EdDSAParameterSpec)paramSpec).getCurveName();
        }
        if (paramSpec instanceof XDHParameterSpec) {
            return ((XDHParameterSpec)paramSpec).getCurveName();
        }
        return ECUtil.getNameFrom(paramSpec);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X25519
    extends KeyPairGeneratorSpi {
        public X25519() {
            super(3);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X448
    extends KeyPairGeneratorSpi {
        public X448() {
            super(4);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class XDH
    extends KeyPairGeneratorSpi {
        public XDH() {
            super(-2);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class Ed25519
    extends KeyPairGeneratorSpi {
        public Ed25519() {
            super(1);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class Ed448
    extends KeyPairGeneratorSpi {
        public Ed448() {
            super(2);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class EdDSA
    extends KeyPairGeneratorSpi {
        public EdDSA() {
            super(-1);
        }
    }
}

