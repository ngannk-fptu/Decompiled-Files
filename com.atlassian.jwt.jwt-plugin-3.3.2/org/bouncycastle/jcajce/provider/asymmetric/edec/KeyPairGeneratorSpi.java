/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.provider.asymmetric.edec.BC11XDHPrivateKey
 *  org.bouncycastle.jcajce.provider.asymmetric.edec.BC11XDHPublicKey
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;

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

    KeyPairGeneratorSpi(int n) {
        this.algorithmDeclared = n;
        if (KeyPairGeneratorSpi.getAlgorithmFamily(n) != n) {
            this.algorithmInitialized = n;
        }
    }

    @Override
    public void initialize(int n, SecureRandom secureRandom) {
        int n2;
        this.algorithmInitialized = n2 = this.getAlgorithmForStrength(n);
        this.secureRandom = secureRandom;
        this.generator = null;
    }

    @Override
    public void initialize(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        String string = KeyPairGeneratorSpi.getNameFromParams(algorithmParameterSpec);
        if (null == string) {
            throw new InvalidAlgorithmParameterException("invalid parameterSpec: " + algorithmParameterSpec);
        }
        int n = KeyPairGeneratorSpi.getAlgorithmForName(string);
        if (this.algorithmDeclared != n && this.algorithmDeclared != KeyPairGeneratorSpi.getAlgorithmFamily(n)) {
            throw new InvalidAlgorithmParameterException("parameterSpec for wrong curve type");
        }
        this.algorithmInitialized = n;
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
        AsymmetricCipherKeyPair asymmetricCipherKeyPair = this.generator.generateKeyPair();
        switch (this.algorithmInitialized) {
            case 1: 
            case 2: {
                return new KeyPair(new BCEdDSAPublicKey(asymmetricCipherKeyPair.getPublic()), new BCEdDSAPrivateKey(asymmetricCipherKeyPair.getPrivate()));
            }
            case 3: 
            case 4: {
                return new KeyPair((PublicKey)new BC11XDHPublicKey(asymmetricCipherKeyPair.getPublic()), (PrivateKey)new BC11XDHPrivateKey(asymmetricCipherKeyPair.getPrivate()));
            }
        }
        throw new IllegalStateException("generator not correctly initialized");
    }

    private int getAlgorithmForStrength(int n) {
        switch (n) {
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
                Ed25519KeyPairGenerator ed25519KeyPairGenerator = new Ed25519KeyPairGenerator();
                ed25519KeyPairGenerator.init(new Ed25519KeyGenerationParameters(this.secureRandom));
                return ed25519KeyPairGenerator;
            }
            case 2: {
                Ed448KeyPairGenerator ed448KeyPairGenerator = new Ed448KeyPairGenerator();
                ed448KeyPairGenerator.init(new Ed448KeyGenerationParameters(this.secureRandom));
                return ed448KeyPairGenerator;
            }
            case 3: {
                X25519KeyPairGenerator x25519KeyPairGenerator = new X25519KeyPairGenerator();
                x25519KeyPairGenerator.init(new X25519KeyGenerationParameters(this.secureRandom));
                return x25519KeyPairGenerator;
            }
            case 4: {
                X448KeyPairGenerator x448KeyPairGenerator = new X448KeyPairGenerator();
                x448KeyPairGenerator.init(new X448KeyGenerationParameters(this.secureRandom));
                return x448KeyPairGenerator;
            }
        }
        throw new IllegalStateException("generator not correctly initialized");
    }

    private static int getAlgorithmFamily(int n) {
        switch (n) {
            case 1: 
            case 2: {
                return -1;
            }
            case 3: 
            case 4: {
                return -2;
            }
        }
        return n;
    }

    private static int getAlgorithmForName(String string) throws InvalidAlgorithmParameterException {
        if (string.equalsIgnoreCase("X25519") || string.equals(EdECObjectIdentifiers.id_X25519.getId())) {
            return 3;
        }
        if (string.equalsIgnoreCase("Ed25519") || string.equals(EdECObjectIdentifiers.id_Ed25519.getId())) {
            return 1;
        }
        if (string.equalsIgnoreCase("X448") || string.equals(EdECObjectIdentifiers.id_X448.getId())) {
            return 4;
        }
        if (string.equalsIgnoreCase("Ed448") || string.equals(EdECObjectIdentifiers.id_Ed448.getId())) {
            return 2;
        }
        throw new InvalidAlgorithmParameterException("invalid parameterSpec name: " + string);
    }

    private static String getNameFromParams(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            return ((ECGenParameterSpec)algorithmParameterSpec).getName();
        }
        if (algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec) {
            return ((ECNamedCurveGenParameterSpec)algorithmParameterSpec).getName();
        }
        if (algorithmParameterSpec instanceof NamedParameterSpec) {
            return ((NamedParameterSpec)algorithmParameterSpec).getName();
        }
        if (algorithmParameterSpec instanceof EdDSAParameterSpec) {
            return ((EdDSAParameterSpec)algorithmParameterSpec).getCurveName();
        }
        if (algorithmParameterSpec instanceof XDHParameterSpec) {
            return ((XDHParameterSpec)algorithmParameterSpec).getCurveName();
        }
        return ECUtil.getNameFrom(algorithmParameterSpec);
    }

    public static final class X25519
    extends KeyPairGeneratorSpi {
        public X25519() {
            super(3);
        }
    }

    public static final class X448
    extends KeyPairGeneratorSpi {
        public X448() {
            super(4);
        }
    }

    public static final class XDH
    extends KeyPairGeneratorSpi {
        public XDH() {
            super(-2);
        }
    }

    public static final class Ed25519
    extends KeyPairGeneratorSpi {
        public Ed25519() {
            super(1);
        }
    }

    public static final class Ed448
    extends KeyPairGeneratorSpi {
        public Ed448() {
            super(2);
        }
    }

    public static final class EdDSA
    extends KeyPairGeneratorSpi {
        public EdDSA() {
            super(-1);
        }
    }
}

