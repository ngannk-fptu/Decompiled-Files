/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.SymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;

public final class RC6 {
    private RC6() {
    }

    public static class AlgParamGen
    extends BaseAlgorithmParameterGenerator {
        protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec, SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for RC6 parameter generation.");
        }

        protected AlgorithmParameters engineGenerateParameters() {
            AlgorithmParameters algorithmParameters;
            byte[] byArray = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(byArray);
            try {
                algorithmParameters = this.createParametersInstance("RC6");
                algorithmParameters.init(new IvParameterSpec(byArray));
            }
            catch (Exception exception) {
                throw new RuntimeException(exception.getMessage());
            }
            return algorithmParameters;
        }
    }

    public static class AlgParams
    extends IvAlgorithmParameters {
        protected String engineToString() {
            return "RC6 IV";
        }
    }

    public static class CBC
    extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new RC6Engine()), 128);
        }
    }

    public static class CFB
    extends BaseBlockCipher {
        public CFB() {
            super(new BufferedBlockCipher(new CFBBlockCipher(new RC6Engine(), 128)), 128);
        }
    }

    public static class ECB
    extends BaseBlockCipher {
        public ECB() {
            super(new BlockCipherProvider(){

                public BlockCipher get() {
                    return new RC6Engine();
                }
            });
        }
    }

    public static class GMAC
    extends BaseMac {
        public GMAC() {
            super(new GMac(new GCMBlockCipher(new RC6Engine())));
        }
    }

    public static class KeyGen
    extends BaseKeyGenerator {
        public KeyGen() {
            super("RC6", 256, new CipherKeyGenerator());
        }
    }

    public static class Mappings
    extends SymmetricAlgorithmProvider {
        private static final String PREFIX = RC6.class.getName();

        public void configure(ConfigurableProvider configurableProvider) {
            configurableProvider.addAlgorithm("Cipher.RC6", PREFIX + "$ECB");
            configurableProvider.addAlgorithm("KeyGenerator.RC6", PREFIX + "$KeyGen");
            configurableProvider.addAlgorithm("AlgorithmParameters.RC6", PREFIX + "$AlgParams");
            this.addGMacAlgorithm(configurableProvider, "RC6", PREFIX + "$GMAC", PREFIX + "$KeyGen");
            this.addPoly1305Algorithm(configurableProvider, "RC6", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }
    }

    public static class OFB
    extends BaseBlockCipher {
        public OFB() {
            super(new BufferedBlockCipher(new OFBBlockCipher(new RC6Engine(), 128)), 128);
        }
    }

    public static class Poly1305
    extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(new RC6Engine()));
        }
    }

    public static class Poly1305KeyGen
    extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-RC6", 256, new Poly1305KeyGenerator());
        }
    }
}

