/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.util.Hashtable;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.BCDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.BCDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.PrimeCertaintyCalculator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Properties;

public class KeyPairGeneratorSpi
extends KeyPairGenerator {
    private static Hashtable params = new Hashtable();
    private static Object lock = new Object();
    DSAKeyGenerationParameters param;
    DSAKeyPairGenerator engine = new DSAKeyPairGenerator();
    int strength = 2048;
    SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
    boolean initialised = false;

    public KeyPairGeneratorSpi() {
        super("DSA");
    }

    @Override
    public void initialize(int strength, SecureRandom random) {
        if (strength < 512 || strength > 4096 || strength < 1024 && strength % 64 != 0 || strength >= 1024 && strength % 1024 != 0) {
            throw new InvalidParameterException("strength must be from 512 - 4096 and a multiple of 1024 above 1024");
        }
        DSAParameterSpec spec = BouncyCastleProvider.CONFIGURATION.getDSADefaultParameters(strength);
        if (spec != null) {
            this.param = new DSAKeyGenerationParameters(random, new DSAParameters(spec.getP(), spec.getQ(), spec.getG()));
            this.engine.init(this.param);
            this.initialised = true;
        } else {
            this.strength = strength;
            this.random = random;
            this.initialised = false;
        }
    }

    @Override
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof DSAParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a DSAParameterSpec");
        }
        DSAParameterSpec dsaParams = (DSAParameterSpec)params;
        this.param = new DSAKeyGenerationParameters(random, new DSAParameters(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG()));
        this.engine.init(this.param);
        this.initialised = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            Integer paramStrength = Integers.valueOf(this.strength);
            if (params.containsKey(paramStrength)) {
                this.param = (DSAKeyGenerationParameters)params.get(paramStrength);
            } else {
                Object object = lock;
                synchronized (object) {
                    if (params.containsKey(paramStrength)) {
                        this.param = (DSAKeyGenerationParameters)params.get(paramStrength);
                    } else {
                        DSAParametersGenerator pGen;
                        int certainty = PrimeCertaintyCalculator.getDefaultCertainty(this.strength);
                        if (this.strength == 1024) {
                            pGen = new DSAParametersGenerator();
                            if (Properties.isOverrideSet("org.bouncycastle.dsa.FIPS186-2for1024bits")) {
                                pGen.init(this.strength, certainty, this.random);
                            } else {
                                DSAParameterGenerationParameters dsaParams = new DSAParameterGenerationParameters(1024, 160, certainty, this.random);
                                pGen.init(dsaParams);
                            }
                        } else if (this.strength > 1024) {
                            DSAParameterGenerationParameters dsaParams = new DSAParameterGenerationParameters(this.strength, 256, certainty, this.random);
                            pGen = new DSAParametersGenerator(SHA256Digest.newInstance());
                            pGen.init(dsaParams);
                        } else {
                            pGen = new DSAParametersGenerator();
                            pGen.init(this.strength, certainty, this.random);
                        }
                        this.param = new DSAKeyGenerationParameters(this.random, pGen.generateParameters());
                        params.put(paramStrength, this.param);
                    }
                }
            }
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        DSAPublicKeyParameters pub = (DSAPublicKeyParameters)pair.getPublic();
        DSAPrivateKeyParameters priv = (DSAPrivateKeyParameters)pair.getPrivate();
        return new KeyPair(new BCDSAPublicKey(pub), new BCDSAPrivateKey(priv));
    }
}

