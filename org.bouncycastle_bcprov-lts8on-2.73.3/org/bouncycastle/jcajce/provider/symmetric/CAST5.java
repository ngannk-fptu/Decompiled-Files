/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.misc.CAST5CBCParameters;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

public final class CAST5 {
    private CAST5() {
    }

    public static class AlgParamGen
    extends BaseAlgorithmParameterGenerator {
        @Override
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for CAST5 parameter generation.");
        }

        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            AlgorithmParameters params;
            byte[] iv = new byte[8];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(iv);
            try {
                params = this.createParametersInstance("CAST5");
                params.init(new IvParameterSpec(iv));
            }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return params;
        }
    }

    public static class AlgParams
    extends BaseAlgorithmParameters {
        private byte[] iv;
        private int keyLength = 128;

        @Override
        protected byte[] engineGetEncoded() {
            byte[] tmp = new byte[this.iv.length];
            System.arraycopy(this.iv, 0, tmp, 0, this.iv.length);
            return tmp;
        }

        @Override
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (this.isASN1FormatString(format)) {
                return new CAST5CBCParameters(this.engineGetEncoded(), this.keyLength).getEncoded();
            }
            if (format.equals("RAW")) {
                return this.engineGetEncoded();
            }
            return null;
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == IvParameterSpec.class || paramSpec == AlgorithmParameterSpec.class) {
                return new IvParameterSpec(this.iv);
            }
            throw new InvalidParameterSpecException("unknown parameter spec passed to CAST5 parameters object.");
        }

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (!(paramSpec instanceof IvParameterSpec)) {
                throw new InvalidParameterSpecException("IvParameterSpec required to initialise a CAST5 parameters algorithm parameters object");
            }
            this.iv = ((IvParameterSpec)paramSpec).getIV();
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            this.iv = new byte[params.length];
            System.arraycopy(params, 0, this.iv, 0, this.iv.length);
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (this.isASN1FormatString(format)) {
                ASN1InputStream aIn = new ASN1InputStream(params);
                CAST5CBCParameters p = CAST5CBCParameters.getInstance(aIn.readObject());
                this.keyLength = p.getKeyLength();
                this.iv = p.getIV();
                return;
            }
            if (format.equals("RAW")) {
                this.engineInit(params);
                return;
            }
            throw new IOException("Unknown parameters format in IV parameters object");
        }

        @Override
        protected String engineToString() {
            return "CAST5 Parameters";
        }
    }

    public static class CBC
    extends BaseBlockCipher {
        public CBC() {
            super(new CBCBlockCipher(new CAST5Engine()), 64);
        }
    }

    public static class ECB
    extends BaseBlockCipher {
        public ECB() {
            super(new CAST5Engine());
        }
    }

    public static class KeyGen
    extends BaseKeyGenerator {
        public KeyGen() {
            super("CAST5", 128, new CipherKeyGenerator());
        }
    }

    public static class Mappings
    extends AlgorithmProvider {
        private static final String PREFIX = CAST5.class.getName();

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.CAST5", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.1.2.840.113533.7.66.10", "CAST5");
            provider.addAlgorithm("AlgorithmParameterGenerator.CAST5", PREFIX + "$AlgParamGen");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.1.2.840.113533.7.66.10", "CAST5");
            provider.addAlgorithm("Cipher.CAST5", PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", MiscObjectIdentifiers.cast5CBC, PREFIX + "$CBC");
            provider.addAlgorithm("KeyGenerator.CAST5", PREFIX + "$KeyGen");
            provider.addAlgorithm("Alg.Alias.KeyGenerator", MiscObjectIdentifiers.cast5CBC, "CAST5");
        }
    }
}

