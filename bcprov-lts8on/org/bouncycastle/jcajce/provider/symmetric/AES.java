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
import java.util.HashMap;
import java.util.Map;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESWrapEngine;
import org.bouncycastle.crypto.engines.AESWrapPadEngine;
import org.bouncycastle.crypto.engines.RFC3211WrapEngine;
import org.bouncycastle.crypto.engines.RFC5649WrapEngine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CCMModeCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.internal.asn1.cms.CCMParameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.SymmetricAlgorithmProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameterGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseBlockCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseKeyGenerator;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseMac;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseSecretKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.IvAlgorithmParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.PBESecretKeyFactory;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

public final class AES {
    private static final Map<String, String> generalAesAttributes = new HashMap<String, String>();

    private AES() {
    }

    static {
        generalAesAttributes.put("SupportedKeyClasses", "javax.crypto.SecretKey");
        generalAesAttributes.put("SupportedKeyFormats", "RAW");
    }

    public static class AESCCMMAC
    extends BaseMac {
        public AESCCMMAC() {
            super(new CCMMac());
        }

        private static class CCMMac
        implements Mac {
            private final CCMModeCipher ccm = CCMBlockCipher.newInstance(AESEngine.newInstance());
            private int macLength = 8;

            private CCMMac() {
            }

            @Override
            public void init(CipherParameters params) throws IllegalArgumentException {
                this.ccm.init(true, params);
                this.macLength = this.ccm.getMac().length;
            }

            @Override
            public String getAlgorithmName() {
                return this.ccm.getAlgorithmName() + "Mac";
            }

            @Override
            public int getMacSize() {
                return this.macLength;
            }

            @Override
            public void update(byte in) throws IllegalStateException {
                this.ccm.processAADByte(in);
            }

            @Override
            public void update(byte[] in, int inOff, int len) throws DataLengthException, IllegalStateException {
                this.ccm.processAADBytes(in, inOff, len);
            }

            @Override
            public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
                try {
                    return this.ccm.doFinal(out, 0);
                }
                catch (InvalidCipherTextException e) {
                    throw new IllegalStateException("exception on doFinal(): " + e.toString());
                }
            }

            @Override
            public void reset() {
                this.ccm.reset();
            }
        }
    }

    public static class AESCMAC
    extends BaseMac {
        public AESCMAC() {
            super(new CMac(AESEngine.newInstance()));
        }
    }

    public static class AESGMAC
    extends BaseMac {
        public AESGMAC() {
            super(new GMac(GCMBlockCipher.newInstance(AESEngine.newInstance())));
        }
    }

    public static class AlgParamGen
    extends BaseAlgorithmParameterGenerator {
        @Override
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            AlgorithmParameters params;
            byte[] iv = new byte[16];
            if (this.random == null) {
                this.random = CryptoServicesRegistrar.getSecureRandom();
            }
            this.random.nextBytes(iv);
            try {
                params = this.createParametersInstance("AES");
                params.init(new IvParameterSpec(iv));
            }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return params;
        }
    }

    public static class AlgParamGenCCM
    extends BaseAlgorithmParameterGenerator {
        @Override
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            AlgorithmParameters params;
            byte[] iv = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(iv);
            try {
                params = this.createParametersInstance("CCM");
                params.init(new CCMParameters(iv, 12).getEncoded());
            }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return params;
        }
    }

    public static class AlgParamGenGCM
    extends BaseAlgorithmParameterGenerator {
        @Override
        protected void engineInit(AlgorithmParameterSpec genParamSpec, SecureRandom random) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("No supported AlgorithmParameterSpec for AES parameter generation.");
        }

        @Override
        protected AlgorithmParameters engineGenerateParameters() {
            AlgorithmParameters params;
            byte[] nonce = new byte[12];
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            this.random.nextBytes(nonce);
            try {
                params = this.createParametersInstance("GCM");
                params.init(new GCMParameters(nonce, 16).getEncoded());
            }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return params;
        }
    }

    public static class AlgParams
    extends IvAlgorithmParameters {
        @Override
        protected String engineToString() {
            return "AES IV";
        }
    }

    public static class AlgParamsCCM
    extends BaseAlgorithmParameters {
        private CCMParameters ccmParams;

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(paramSpec)) {
                this.ccmParams = CCMParameters.getInstance(GcmSpecUtil.extractGcmParameters(paramSpec));
            } else if (paramSpec instanceof AEADParameterSpec) {
                this.ccmParams = new CCMParameters(((AEADParameterSpec)paramSpec).getNonce(), ((AEADParameterSpec)paramSpec).getMacSizeInBits() / 8);
            } else {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            this.ccmParams = CCMParameters.getInstance(params);
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!this.isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            this.ccmParams = CCMParameters.getInstance(params);
        }

        @Override
        protected byte[] engineGetEncoded() throws IOException {
            return this.ccmParams.getEncoded();
        }

        @Override
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (!this.isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            return this.ccmParams.getEncoded();
        }

        @Override
        protected String engineToString() {
            return "CCM";
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec)) {
                if (GcmSpecUtil.gcmSpecExtractable()) {
                    return GcmSpecUtil.extractGcmSpec(this.ccmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (paramSpec == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.ccmParams.getNonce(), this.ccmParams.getIcvLen() * 8);
            }
            if (paramSpec == IvParameterSpec.class) {
                return new IvParameterSpec(this.ccmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    public static class AlgParamsGCM
    extends BaseAlgorithmParameters {
        private GCMParameters gcmParams;

        @Override
        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
            if (GcmSpecUtil.isGcmSpec(paramSpec)) {
                this.gcmParams = GcmSpecUtil.extractGcmParameters(paramSpec);
            } else if (paramSpec instanceof AEADParameterSpec) {
                this.gcmParams = new GCMParameters(((AEADParameterSpec)paramSpec).getNonce(), ((AEADParameterSpec)paramSpec).getMacSizeInBits() / 8);
            } else {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + paramSpec.getClass().getName());
            }
        }

        @Override
        protected void engineInit(byte[] params) throws IOException {
            this.gcmParams = GCMParameters.getInstance(params);
        }

        @Override
        protected void engineInit(byte[] params, String format) throws IOException {
            if (!this.isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            this.gcmParams = GCMParameters.getInstance(params);
        }

        @Override
        protected byte[] engineGetEncoded() throws IOException {
            return this.gcmParams.getEncoded();
        }

        @Override
        protected byte[] engineGetEncoded(String format) throws IOException {
            if (!this.isASN1FormatString(format)) {
                throw new IOException("unknown format specified");
            }
            return this.gcmParams.getEncoded();
        }

        @Override
        protected String engineToString() {
            return "GCM";
        }

        @Override
        protected AlgorithmParameterSpec localEngineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            if (paramSpec == AlgorithmParameterSpec.class || GcmSpecUtil.isGcmSpec(paramSpec)) {
                if (GcmSpecUtil.gcmSpecExtractable()) {
                    return GcmSpecUtil.extractGcmSpec(this.gcmParams.toASN1Primitive());
                }
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (paramSpec == AEADParameterSpec.class) {
                return new AEADParameterSpec(this.gcmParams.getNonce(), this.gcmParams.getIcvLen() * 8);
            }
            if (paramSpec == IvParameterSpec.class) {
                return new IvParameterSpec(this.gcmParams.getNonce());
            }
            throw new InvalidParameterSpecException("AlgorithmParameterSpec not recognized: " + paramSpec.getName());
        }
    }

    public static class CBC
    extends BaseBlockCipher {
        public CBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 128);
        }
    }

    public static class CCM
    extends BaseBlockCipher {
        public CCM() {
            super(CCMBlockCipher.newInstance(AESEngine.newInstance()), false, 12);
        }
    }

    public static class CFB
    extends BaseBlockCipher {
        public CFB() {
            super(new DefaultBufferedBlockCipher(CFBBlockCipher.newInstance(AESEngine.newInstance(), 128)), 128);
        }
    }

    public static class ECB
    extends BaseBlockCipher {
        public ECB() {
            super(new BlockCipherProvider(){

                @Override
                public BlockCipher get() {
                    return AESEngine.newInstance();
                }
            });
        }
    }

    public static class GCM
    extends BaseBlockCipher {
        public GCM() {
            super(GCMBlockCipher.newInstance(AESEngine.newInstance()));
        }
    }

    public static class KeyFactory
    extends BaseSecretKeyFactory {
        public KeyFactory() {
            super("AES", null);
        }
    }

    public static class KeyGen
    extends BaseKeyGenerator {
        public KeyGen() {
            this(192);
        }

        public KeyGen(int keySize) {
            super("AES", keySize, new CipherKeyGenerator());
        }
    }

    public static class KeyGen128
    extends KeyGen {
        public KeyGen128() {
            super(128);
        }
    }

    public static class KeyGen192
    extends KeyGen {
        public KeyGen192() {
            super(192);
        }
    }

    public static class KeyGen256
    extends KeyGen {
        public KeyGen256() {
            super(256);
        }
    }

    public static class Mappings
    extends SymmetricAlgorithmProvider {
        private static final String PREFIX = AES.class.getName();
        private static final String wrongAES128 = "2.16.840.1.101.3.4.2";
        private static final String wrongAES192 = "2.16.840.1.101.3.4.22";
        private static final String wrongAES256 = "2.16.840.1.101.3.4.42";

        @Override
        public void configure(ConfigurableProvider provider) {
            provider.addAlgorithm("AlgorithmParameters.AES", PREFIX + "$AlgParams");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.2", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.22", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.2.16.840.1.101.3.4.42", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
            provider.addAlgorithm("AlgorithmParameters.GCM", PREFIX + "$AlgParamsGCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            provider.addAlgorithm("AlgorithmParameters.CCM", PREFIX + "$AlgParamsCCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            provider.addAlgorithm("AlgorithmParameterGenerator.AES", PREFIX + "$AlgParamGen");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.2", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.22", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.2.16.840.1.101.3.4.42", "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CBC, "AES");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CBC, "AES");
            provider.addAttributes("Cipher.AES", generalAesAttributes);
            provider.addAlgorithm("Cipher.AES", PREFIX + "$ECB");
            provider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.2", "AES");
            provider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.22", "AES");
            provider.addAlgorithm("Alg.Alias.Cipher.2.16.840.1.101.3.4.42", "AES");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$ECB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$CBC");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$OFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$CFB");
            provider.addAlgorithm("Cipher", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$CFB");
            provider.addAttributes("Cipher.AESWRAP", generalAesAttributes);
            provider.addAlgorithm("Cipher.AESWRAP", PREFIX + "$Wrap");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap, "AESWRAP");
            provider.addAlgorithm("Alg.Alias.Cipher.AESKW", "AESWRAP");
            provider.addAttributes("Cipher.AESWRAPPAD", generalAesAttributes);
            provider.addAlgorithm("Cipher.AESWRAPPAD", PREFIX + "$WrapPad");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_wrap_pad, "AESWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_wrap_pad, "AESWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_wrap_pad, "AESWRAPPAD");
            provider.addAlgorithm("Alg.Alias.Cipher.AESKWP", "AESWRAPPAD");
            provider.addAlgorithm("Cipher.AESRFC3211WRAP", PREFIX + "$RFC3211Wrap");
            provider.addAlgorithm("Cipher.AESRFC5649WRAP", PREFIX + "$RFC5649Wrap");
            provider.addAlgorithm("AlgorithmParameterGenerator.CCM", PREFIX + "$AlgParamGenCCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            provider.addAttributes("Cipher.CCM", generalAesAttributes);
            provider.addAlgorithm("Cipher.CCM", PREFIX + "$CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_CCM, "CCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_CCM, "CCM");
            provider.addAlgorithm("AlgorithmParameterGenerator.GCM", PREFIX + "$AlgParamGenGCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator." + NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            provider.addAttributes("Cipher.GCM", generalAesAttributes);
            provider.addAlgorithm("Cipher.GCM", PREFIX + "$GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes128_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes192_GCM, "GCM");
            provider.addAlgorithm("Alg.Alias.Cipher", NISTObjectIdentifiers.id_aes256_GCM, "GCM");
            provider.addAlgorithm("KeyGenerator.AES", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.2", PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.22", PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator.2.16.840.1.101.3.4.42", PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_ECB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CBC, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_OFB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CFB, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_ECB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CBC, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_OFB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CFB, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_ECB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CBC, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_OFB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CFB, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator.AESWRAP", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_GCM, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_GCM, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_GCM, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_CCM, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_CCM, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_CCM, PREFIX + "$KeyGen256");
            provider.addAlgorithm("KeyGenerator.AESWRAPPAD", PREFIX + "$KeyGen");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes128_wrap_pad, PREFIX + "$KeyGen128");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes192_wrap_pad, PREFIX + "$KeyGen192");
            provider.addAlgorithm("KeyGenerator", NISTObjectIdentifiers.id_aes256_wrap_pad, PREFIX + "$KeyGen256");
            provider.addAlgorithm("Mac.AESCMAC", PREFIX + "$AESCMAC");
            provider.addAlgorithm("Mac.AESCCMMAC", PREFIX + "$AESCCMMAC");
            provider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes128_CCM.getId(), "AESCCMMAC");
            provider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes192_CCM.getId(), "AESCCMMAC");
            provider.addAlgorithm("Alg.Alias.Mac." + NISTObjectIdentifiers.id_aes256_CCM.getId(), "AESCCMMAC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Cipher.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC128");
            provider.addAlgorithm("Cipher.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC192");
            provider.addAlgorithm("Cipher.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA1AESCBC256");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC128");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC192");
            provider.addAlgorithm("Cipher.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256AESCBC256");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHAAND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND128BITAES-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND192BITAES-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-1AND256BITAES-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.Cipher.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Cipher.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
            provider.addAlgorithm("Cipher.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
            provider.addAlgorithm("Cipher.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithAESCBC");
            provider.addAlgorithm("SecretKeyFactory.AES", PREFIX + "$KeyFactory");
            provider.addAlgorithm("SecretKeyFactory", NISTObjectIdentifiers.aes, PREFIX + "$KeyFactory");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND128BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And128BitAESCBCOpenSSL");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND192BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And192BitAESCBCOpenSSL");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHMD5AND256BITAES-CBC-OPENSSL", PREFIX + "$PBEWithMD5And256BitAESCBCOpenSSL");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND128BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd128BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND192BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd192BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHAAND256BITAES-CBC-BC", PREFIX + "$PBEWithSHAAnd256BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND128BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And128BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND192BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And192BitAESBC");
            provider.addAlgorithm("SecretKeyFactory.PBEWITHSHA256AND256BITAES-CBC-BC", PREFIX + "$PBEWithSHA256And256BitAESBC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND128BITAES-CBC-BC", "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND192BITAES-CBC-BC", "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-1AND256BITAES-CBC-BC", "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-CBC-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-CBC-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-CBC-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND128BITAES-BC", "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND192BITAES-BC", "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory.PBEWITHSHA-256AND256BITAES-BC", "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc, "PBEWITHSHAAND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc, "PBEWITHSHAAND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc, "PBEWITHSHAAND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc, "PBEWITHSHA256AND128BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc, "PBEWITHSHA256AND192BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.SecretKeyFactory", BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc, "PBEWITHSHA256AND256BITAES-CBC-BC");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHAAND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA256AND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA1AND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-1AND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND128BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND192BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters.PBEWITHSHA-256AND256BITAES-CBC-BC", "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha1_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes128_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes192_cbc.getId(), "PKCS12PBE");
            provider.addAlgorithm("Alg.Alias.AlgorithmParameters." + BCObjectIdentifiers.bc_pbe_sha256_pkcs12_aes256_cbc.getId(), "PKCS12PBE");
            this.addGMacAlgorithm(provider, "AES", PREFIX + "$AESGMAC", PREFIX + "$KeyGen128");
            this.addPoly1305Algorithm(provider, "AES", PREFIX + "$Poly1305", PREFIX + "$Poly1305KeyGen");
        }
    }

    public static class OFB
    extends BaseBlockCipher {
        public OFB() {
            super(new DefaultBufferedBlockCipher(new OFBBlockCipher(AESEngine.newInstance(), 128)), 128);
        }
    }

    public static class PBEWithAESCBC
    extends BaseBlockCipher {
        public PBEWithAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()));
        }
    }

    public static class PBEWithMD5And128BitAESCBCOpenSSL
    extends PBESecretKeyFactory {
        public PBEWithMD5And128BitAESCBCOpenSSL() {
            super("PBEWithMD5And128BitAES-CBC-OpenSSL", null, true, 3, 0, 128, 128);
        }
    }

    public static class PBEWithMD5And192BitAESCBCOpenSSL
    extends PBESecretKeyFactory {
        public PBEWithMD5And192BitAESCBCOpenSSL() {
            super("PBEWithMD5And192BitAES-CBC-OpenSSL", null, true, 3, 0, 192, 128);
        }
    }

    public static class PBEWithMD5And256BitAESCBCOpenSSL
    extends PBESecretKeyFactory {
        public PBEWithMD5And256BitAESCBCOpenSSL() {
            super("PBEWithMD5And256BitAES-CBC-OpenSSL", null, true, 3, 0, 256, 128);
        }
    }

    public static class PBEWithSHA1AESCBC128
    extends BaseBlockCipher {
        public PBEWithSHA1AESCBC128() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 128, 16);
        }
    }

    public static class PBEWithSHA1AESCBC192
    extends BaseBlockCipher {
        public PBEWithSHA1AESCBC192() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 192, 16);
        }
    }

    public static class PBEWithSHA1AESCBC256
    extends BaseBlockCipher {
        public PBEWithSHA1AESCBC256() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 1, 256, 16);
        }
    }

    public static class PBEWithSHA256AESCBC128
    extends BaseBlockCipher {
        public PBEWithSHA256AESCBC128() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 128, 16);
        }
    }

    public static class PBEWithSHA256AESCBC192
    extends BaseBlockCipher {
        public PBEWithSHA256AESCBC192() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 192, 16);
        }
    }

    public static class PBEWithSHA256AESCBC256
    extends BaseBlockCipher {
        public PBEWithSHA256AESCBC256() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 2, 4, 256, 16);
        }
    }

    public static class PBEWithSHA256And128BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHA256And128BitAESBC() {
            super("PBEWithSHA256And128BitAES-CBC-BC", null, true, 2, 4, 128, 128);
        }
    }

    public static class PBEWithSHA256And192BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHA256And192BitAESBC() {
            super("PBEWithSHA256And192BitAES-CBC-BC", null, true, 2, 4, 192, 128);
        }
    }

    public static class PBEWithSHA256And256BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHA256And256BitAESBC() {
            super("PBEWithSHA256And256BitAES-CBC-BC", null, true, 2, 4, 256, 128);
        }
    }

    public static class PBEWithSHAAnd128BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHAAnd128BitAESBC() {
            super("PBEWithSHA1And128BitAES-CBC-BC", null, true, 2, 1, 128, 128);
        }
    }

    public static class PBEWithSHAAnd192BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHAAnd192BitAESBC() {
            super("PBEWithSHA1And192BitAES-CBC-BC", null, true, 2, 1, 192, 128);
        }
    }

    public static class PBEWithSHAAnd256BitAESBC
    extends PBESecretKeyFactory {
        public PBEWithSHAAnd256BitAESBC() {
            super("PBEWithSHA1And256BitAES-CBC-BC", null, true, 2, 1, 256, 128);
        }
    }

    public static class Poly1305
    extends BaseMac {
        public Poly1305() {
            super(new org.bouncycastle.crypto.macs.Poly1305(AESEngine.newInstance()));
        }
    }

    public static class Poly1305KeyGen
    extends BaseKeyGenerator {
        public Poly1305KeyGen() {
            super("Poly1305-AES", 256, new Poly1305KeyGenerator());
        }
    }

    public static class RFC3211Wrap
    extends BaseWrapCipher {
        public RFC3211Wrap() {
            super(new RFC3211WrapEngine(AESEngine.newInstance()), 16);
        }
    }

    public static class RFC5649Wrap
    extends BaseWrapCipher {
        public RFC5649Wrap() {
            super(new RFC5649WrapEngine(AESEngine.newInstance()));
        }
    }

    public static class Wrap
    extends BaseWrapCipher {
        public Wrap() {
            super(new AESWrapEngine());
        }
    }

    public static class WrapPad
    extends BaseWrapCipher {
        public WrapPad() {
            super(new AESWrapPadEngine());
        }
    }
}

