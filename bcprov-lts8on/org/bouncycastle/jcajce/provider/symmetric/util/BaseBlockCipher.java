/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.DefaultBufferedMultiBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.fpe.FPEEngine;
import org.bouncycastle.crypto.fpe.FPEFF1Engine;
import org.bouncycastle.crypto.fpe.FPEFF3_1Engine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.OpenPGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.PGPCFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO10126d2Padding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedMultiBlockCipher;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.jcajce.PBKDF1Key;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.BlockCipherProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.symmetric.util.SpecUtil;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.FPEParameterSpec;
import org.bouncycastle.jcajce.spec.GOST28147ParameterSpec;
import org.bouncycastle.jcajce.spec.RepeatedSecretKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class BaseBlockCipher
extends BaseWrapCipher
implements PBE {
    private static final int BUF_SIZE = 512;
    private static final Class[] availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, GcmSpecUtil.gcmSpecClass, GOST28147ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
    private BlockCipher baseEngine;
    private BlockCipherProvider engineProvider;
    private GenericBlockCipher cipher;
    private ParametersWithIV ivParam;
    private AEADParameters aeadParams;
    private int keySizeInBits;
    private int scheme = -1;
    private int digest;
    private int ivLength = 0;
    private boolean padded;
    private boolean fixedIv = true;
    private PBEParameterSpec pbeSpec = null;
    private String pbeAlgorithm = null;
    private String modeName = null;

    protected BaseBlockCipher(BlockCipher engine) {
        this.baseEngine = engine;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipher engine, int scheme, int digest, int keySizeInBits, int ivLength) {
        this.baseEngine = engine;
        this.scheme = scheme;
        this.digest = digest;
        this.keySizeInBits = keySizeInBits;
        this.ivLength = ivLength;
        this.cipher = new BufferedGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipherProvider provider) {
        this.baseEngine = provider.get();
        this.engineProvider = provider;
        this.cipher = new BufferedGenericBlockCipher(provider.get());
    }

    protected BaseBlockCipher(AEADBlockCipher engine) {
        this.baseEngine = engine.getUnderlyingCipher();
        this.ivLength = engine.getAlgorithmName().indexOf("GCM") >= 0 ? 12 : this.baseEngine.getBlockSize();
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(AEADCipher engine, boolean fixedIv, int ivLength) {
        this.baseEngine = null;
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(AEADBlockCipher engine, boolean fixedIv, int ivLength) {
        this.baseEngine = engine.getUnderlyingCipher();
        this.fixedIv = fixedIv;
        this.ivLength = ivLength;
        this.cipher = new AEADGenericBlockCipher(engine);
    }

    protected BaseBlockCipher(BlockCipher engine, int ivLength) {
        this(engine, true, ivLength);
    }

    protected BaseBlockCipher(BlockCipher engine, boolean fixedIv, int ivLength) {
        this.baseEngine = engine;
        this.fixedIv = fixedIv;
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.ivLength = ivLength / 8;
    }

    protected BaseBlockCipher(BufferedBlockCipher engine, int ivLength) {
        this(engine, true, ivLength);
    }

    protected BaseBlockCipher(BufferedBlockCipher engine, boolean fixedIv, int ivLength) {
        this.baseEngine = engine.getUnderlyingCipher();
        this.cipher = new BufferedGenericBlockCipher(engine);
        this.fixedIv = fixedIv;
        this.ivLength = ivLength / 8;
    }

    @Override
    protected int engineGetBlockSize() {
        if (this.baseEngine == null) {
            return -1;
        }
        return this.baseEngine.getBlockSize();
    }

    @Override
    protected byte[] engineGetIV() {
        if (this.aeadParams != null) {
            return this.aeadParams.getNonce();
        }
        return this.ivParam != null ? this.ivParam.getIV() : null;
    }

    @Override
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        return this.cipher.getOutputSize(inputLen);
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    this.engineParams = this.createParametersInstance(this.pbeAlgorithm);
                    this.engineParams.init(this.pbeSpec);
                }
                catch (Exception e) {
                    return null;
                }
            }
            if (this.aeadParams != null) {
                if (this.baseEngine == null) {
                    try {
                        this.engineParams = this.createParametersInstance(PKCSObjectIdentifiers.id_alg_AEADChaCha20Poly1305.getId());
                        this.engineParams.init(new DEROctetString(this.aeadParams.getNonce()).getEncoded());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.toString());
                    }
                } else {
                    try {
                        this.engineParams = this.createParametersInstance("GCM");
                        this.engineParams.init(new GCMParameters(this.aeadParams.getNonce(), this.aeadParams.getMacSize() / 8).getEncoded());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e.toString());
                    }
                }
            } else if (this.ivParam != null) {
                String name = this.cipher.getUnderlyingCipher().getAlgorithmName();
                if (name.indexOf(47) >= 0) {
                    name = name.substring(0, name.indexOf(47));
                }
                try {
                    this.engineParams = this.createParametersInstance(name);
                    this.engineParams.init(new IvParameterSpec(this.ivParam.getIV()));
                }
                catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
            }
        }
        return this.engineParams;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        if (this.baseEngine == null) {
            throw new NoSuchAlgorithmException("no mode supported for this algorithm");
        }
        this.modeName = Strings.toUpperCase(mode);
        if (this.modeName.equals("ECB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(this.baseEngine);
            return;
        } else if (this.modeName.equals("CBC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(CBCBlockCipher.newInstance(this.baseEngine));
            return;
        } else if (this.modeName.startsWith("OFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, wordSize));
                return;
            } else {
                this.cipher = new BufferedGenericBlockCipher(new OFBBlockCipher(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
            return;
        } else if (this.modeName.startsWith("CFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.modeName.length() != 3) {
                int wordSize = Integer.parseInt(this.modeName.substring(3));
                this.cipher = new BufferedGenericBlockCipher(CFBBlockCipher.newInstance(this.baseEngine, wordSize));
                return;
            } else {
                this.cipher = new BufferedGenericBlockCipher(CFBBlockCipher.newInstance(this.baseEngine, 8 * this.baseEngine.getBlockSize()));
            }
            return;
        } else if (this.modeName.startsWith("PGPCFB")) {
            boolean inlineIV = this.modeName.equals("PGPCFBWITHIV");
            if (!inlineIV && this.modeName.length() != 6) {
                throw new NoSuchAlgorithmException("no mode support for " + this.modeName);
            }
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new PGPCFBBlockCipher(this.baseEngine, inlineIV));
            return;
        } else if (this.modeName.equals("OPENPGPCFB")) {
            this.ivLength = 0;
            this.cipher = new BufferedGenericBlockCipher(new OpenPGPCFBBlockCipher(this.baseEngine));
            return;
        } else if (this.modeName.equals("FF1")) {
            this.ivLength = 0;
            this.cipher = new BufferedFPEBlockCipher(new FPEFF1Engine(this.baseEngine));
            return;
        } else if (this.modeName.equals("FF3-1")) {
            this.ivLength = 0;
            this.cipher = new BufferedFPEBlockCipher(new FPEFF3_1Engine(this.baseEngine));
            return;
        } else if (this.modeName.equals("SIC")) {
            this.ivLength = this.baseEngine.getBlockSize();
            if (this.ivLength < 16) {
                throw new IllegalArgumentException("Warning: SIC-Mode can become a twotime-pad if the blocksize of the cipher is too small. Use a cipher with a block size of at least 128 bits (e.g. AES)");
            }
            this.fixedIv = false;
            CTRModeCipher c = SICBlockCipher.newInstance(this.baseEngine);
            this.cipher = c instanceof MultiBlockCipher ? new BufferedGenericBlockCipher(new DefaultBufferedMultiBlockCipher(c)) : new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(c));
            return;
        } else if (this.modeName.equals("CTR")) {
            CTRModeCipher c;
            this.ivLength = this.baseEngine.getBlockSize();
            this.fixedIv = false;
            this.cipher = this.baseEngine instanceof DSTU7624Engine ? new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new KCTRBlockCipher(this.baseEngine))) : ((c = SICBlockCipher.newInstance(this.baseEngine)) instanceof MultiBlockCipher ? new BufferedGenericBlockCipher(new DefaultBufferedMultiBlockCipher(c)) : new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(c)));
            return;
        } else if (this.modeName.equals("GOFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new GOFBBlockCipher(this.baseEngine)));
            return;
        } else if (this.modeName.equals("GCFB")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(new GCFBBlockCipher(this.baseEngine)));
            return;
        } else if (this.modeName.equals("CTS")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(CBCBlockCipher.newInstance(this.baseEngine)));
            return;
        } else if (this.modeName.equals("CCM")) {
            this.ivLength = 12;
            this.cipher = this.baseEngine instanceof DSTU7624Engine ? new AEADGenericBlockCipher(new KCCMBlockCipher(this.baseEngine)) : new AEADGenericBlockCipher(CCMBlockCipher.newInstance(this.baseEngine));
            return;
        } else if (this.modeName.equals("OCB")) {
            if (this.engineProvider == null) throw new NoSuchAlgorithmException("can't support mode " + mode);
            this.ivLength = 15;
            this.cipher = new AEADGenericBlockCipher(new OCBBlockCipher(this.baseEngine, this.engineProvider.get()));
            return;
        } else if (this.modeName.equals("EAX")) {
            this.ivLength = this.baseEngine.getBlockSize();
            this.cipher = new AEADGenericBlockCipher(new EAXBlockCipher(this.baseEngine));
            return;
        } else if (this.modeName.equals("GCM-SIV")) {
            this.ivLength = 12;
            this.cipher = new AEADGenericBlockCipher(new GCMSIVBlockCipher(this.baseEngine));
            return;
        } else {
            if (!this.modeName.equals("GCM")) throw new NoSuchAlgorithmException("can't support mode " + mode);
            if (this.baseEngine instanceof DSTU7624Engine) {
                this.ivLength = this.baseEngine.getBlockSize();
                this.cipher = new AEADGenericBlockCipher(new KGCMBlockCipher(this.baseEngine));
                return;
            } else {
                this.ivLength = 12;
                this.cipher = new AEADGenericBlockCipher(GCMBlockCipher.newInstance(this.baseEngine));
            }
        }
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        if (this.baseEngine == null) {
            throw new NoSuchPaddingException("no padding supported for this algorithm");
        }
        String paddingName = Strings.toUpperCase(padding);
        if (paddingName.equals("NOPADDING")) {
            if (this.cipher.wrapOnNoPadding()) {
                this.cipher = this.cipher.getUnderlyingCipher() instanceof MultiBlockCipher ? new BufferedGenericBlockCipher(new DefaultBufferedMultiBlockCipher((MultiBlockCipher)this.cipher.getUnderlyingCipher())) : new BufferedGenericBlockCipher(new DefaultBufferedBlockCipher(this.cipher.getUnderlyingCipher()));
            }
        } else if (paddingName.equals("WITHCTS") || paddingName.equals("CTSPADDING") || paddingName.equals("CS3PADDING")) {
            this.cipher = new BufferedGenericBlockCipher(new CTSBlockCipher(this.cipher.getUnderlyingCipher()));
        } else {
            this.padded = true;
            if (this.isAEADModeName(this.modeName)) {
                throw new NoSuchPaddingException("Only NoPadding can be used with AEAD modes.");
            }
            if (paddingName.equals("PKCS5PADDING") || paddingName.equals("PKCS7PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher());
            } else if (paddingName.equals("ZEROBYTEPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ZeroBytePadding());
            } else if (paddingName.equals("ISO10126PADDING") || paddingName.equals("ISO10126-2PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO10126d2Padding());
            } else if (paddingName.equals("X9.23PADDING") || paddingName.equals("X923PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new X923Padding());
            } else if (paddingName.equals("ISO7816-4PADDING") || paddingName.equals("ISO9797-1PADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new ISO7816d4Padding());
            } else if (paddingName.equals("TBCPADDING")) {
                this.cipher = new BufferedGenericBlockCipher(this.cipher.getUnderlyingCipher(), new TBCPadding());
            } else {
                throw new NoSuchPaddingException("Padding " + padding + " unknown.");
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        SecretKey k;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        this.aeadParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + (key != null ? key.getAlgorithm() : null) + " not suitable for symmetric enryption.");
        }
        if (params == null && this.baseEngine != null && this.baseEngine.getAlgorithmName().startsWith("RC5-64")) {
            throw new InvalidAlgorithmParameterException("RC5 requires an RC5ParametersSpec to be passed in.");
        }
        if (this.scheme == 2 || key instanceof PKCS12Key) {
            try {
                k = (SecretKey)key;
            }
            catch (Exception e) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
            if (params instanceof PBEParameterSpec) {
                this.pbeSpec = (PBEParameterSpec)params;
            }
            if (k instanceof PBEKey && this.pbeSpec == null) {
                PBEKey pbeKey = (PBEKey)k;
                if (pbeKey.getSalt() == null) {
                    throw new InvalidAlgorithmParameterException("PBEKey requires parameters to specify salt");
                }
                this.pbeSpec = new PBEParameterSpec(pbeKey.getSalt(), pbeKey.getIterationCount());
            }
            if (this.pbeSpec == null && !(k instanceof PBEKey)) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            if (key instanceof BCPBEKey) {
                CipherParameters pbeKeyParam = ((BCPBEKey)key).getParam();
                if (pbeKeyParam instanceof ParametersWithIV) {
                    param = pbeKeyParam;
                } else {
                    if (pbeKeyParam != null) throw new InvalidKeyException("Algorithm requires a PBE key suitable for PKCS12");
                    param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
                }
            } else {
                param = PBE.Util.makePBEParameters(k.getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
            }
            if (param instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (key instanceof PBKDF1Key) {
            k = (PBKDF1Key)key;
            if (params instanceof PBEParameterSpec) {
                this.pbeSpec = (PBEParameterSpec)params;
            }
            if (k instanceof PBKDF1KeyWithParameters && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(((PBKDF1KeyWithParameters)k).getSalt(), ((PBKDF1KeyWithParameters)k).getIterationCount());
            }
            if ((param = PBE.Util.makePBEParameters(((PBKDF1Key)k).getEncoded(), 0, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName())) instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (key instanceof BCPBEKey) {
            k = (BCPBEKey)key;
            this.pbeAlgorithm = ((BCPBEKey)k).getOID() != null ? ((BCPBEKey)k).getOID().getId() : ((BCPBEKey)k).getAlgorithm();
            if (((BCPBEKey)k).getParam() != null) {
                param = this.adjustParameters(params, ((BCPBEKey)k).getParam());
            } else {
                if (!(params instanceof PBEParameterSpec)) throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                this.pbeSpec = (PBEParameterSpec)params;
                param = PBE.Util.makePBEParameters((BCPBEKey)k, params, this.cipher.getUnderlyingCipher().getAlgorithmName());
            }
            if (param instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (key instanceof PBEKey) {
            k = (PBEKey)key;
            this.pbeSpec = (PBEParameterSpec)params;
            if (k instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(k.getSalt(), k.getIterationCount());
            }
            if ((param = PBE.Util.makePBEParameters(k.getEncoded(), this.scheme, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName())) instanceof ParametersWithIV) {
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (!(key instanceof RepeatedSecretKeySpec)) {
            if (this.scheme == 0 || this.scheme == 4 || this.scheme == 1 || this.scheme == 5) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            param = new KeyParameter(key.getEncoded());
        } else {
            param = null;
        }
        if (params instanceof AEADParameterSpec) {
            if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                throw new InvalidAlgorithmParameterException("AEADParameterSpec can only be used with AEAD modes.");
            }
            AEADParameterSpec aeadSpec = (AEADParameterSpec)params;
            KeyParameter keyParam = param instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)param).getParameters() : (KeyParameter)param;
            this.aeadParams = new AEADParameters(keyParam, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
            param = this.aeadParams;
        } else if (params instanceof IvParameterSpec) {
            if (this.ivLength != 0) {
                IvParameterSpec p = (IvParameterSpec)params;
                if (p.getIV().length != this.ivLength && !(this.cipher instanceof AEADGenericBlockCipher) && this.fixedIv) {
                    throw new InvalidAlgorithmParameterException("IV must be " + this.ivLength + " bytes long.");
                }
                param = param instanceof ParametersWithIV ? new ParametersWithIV(((ParametersWithIV)param).getParameters(), p.getIV()) : new ParametersWithIV(param, p.getIV());
                this.ivParam = (ParametersWithIV)param;
            } else if (this.modeName != null && this.modeName.equals("ECB")) {
                throw new InvalidAlgorithmParameterException("ECB mode does not use an IV");
            }
        } else if (params instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec gost28147Param = (GOST28147ParameterSpec)params;
            param = new ParametersWithSBox(new KeyParameter(key.getEncoded()), ((GOST28147ParameterSpec)params).getSBox());
            if (gost28147Param.getIV() != null && this.ivLength != 0) {
                param = param instanceof ParametersWithIV ? new ParametersWithIV(((ParametersWithIV)param).getParameters(), gost28147Param.getIV()) : new ParametersWithIV(param, gost28147Param.getIV());
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (params instanceof RC2ParameterSpec) {
            RC2ParameterSpec rc2Param = (RC2ParameterSpec)params;
            param = new RC2Parameters(key.getEncoded(), ((RC2ParameterSpec)params).getEffectiveKeyBits());
            if (rc2Param.getIV() != null && this.ivLength != 0) {
                param = param instanceof ParametersWithIV ? new ParametersWithIV(((ParametersWithIV)param).getParameters(), rc2Param.getIV()) : new ParametersWithIV(param, rc2Param.getIV());
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (params instanceof RC5ParameterSpec) {
            RC5ParameterSpec rc5Param = (RC5ParameterSpec)params;
            param = new RC5Parameters(key.getEncoded(), ((RC5ParameterSpec)params).getRounds());
            if (!this.baseEngine.getAlgorithmName().startsWith("RC5")) throw new InvalidAlgorithmParameterException("RC5 parameters passed to a cipher that is not RC5.");
            if (this.baseEngine.getAlgorithmName().equals("RC5-32")) {
                if (rc5Param.getWordSize() != 32) {
                    throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 32 not " + rc5Param.getWordSize() + ".");
                }
            } else if (this.baseEngine.getAlgorithmName().equals("RC5-64") && rc5Param.getWordSize() != 64) {
                throw new InvalidAlgorithmParameterException("RC5 already set up for a word size of 64 not " + rc5Param.getWordSize() + ".");
            }
            if (rc5Param.getIV() != null && this.ivLength != 0) {
                param = param instanceof ParametersWithIV ? new ParametersWithIV(((ParametersWithIV)param).getParameters(), rc5Param.getIV()) : new ParametersWithIV(param, rc5Param.getIV());
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (params instanceof FPEParameterSpec) {
            FPEParameterSpec spec = (FPEParameterSpec)params;
            param = new FPEParameters((KeyParameter)param, spec.getRadixConverter(), spec.getTweak(), spec.isUsingInverseFunction());
        } else if (GcmSpecUtil.isGcmSpec(params)) {
            if (!this.isAEADModeName(this.modeName) && !(this.cipher instanceof AEADGenericBlockCipher)) {
                throw new InvalidAlgorithmParameterException("GCMParameterSpec can only be used with AEAD modes.");
            }
            KeyParameter keyParam = param instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)param).getParameters() : (KeyParameter)param;
            this.aeadParams = GcmSpecUtil.extractAeadParameters(keyParam, params);
            param = this.aeadParams;
        } else if (params != null && !(params instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
        }
        if (this.ivLength != 0 && !(param instanceof ParametersWithIV) && !(param instanceof AEADParameters)) {
            SecureRandom ivRandom = random;
            if (ivRandom == null) {
                ivRandom = CryptoServicesRegistrar.getSecureRandom();
            }
            if (opmode == 1 || opmode == 3) {
                byte[] iv = new byte[this.ivLength];
                ivRandom.nextBytes(iv);
                param = new ParametersWithIV(param, iv);
                this.ivParam = (ParametersWithIV)param;
            } else if (this.cipher.getUnderlyingCipher().getAlgorithmName().indexOf("PGPCFB") < 0) {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
        }
        if (random != null && this.padded) {
            param = new ParametersWithRandom(param, random);
        }
        try {
            switch (opmode) {
                case 1: 
                case 3: {
                    this.cipher.init(true, param);
                    break;
                }
                case 2: 
                case 4: {
                    this.cipher.init(false, param);
                    break;
                }
                default: {
                    throw new InvalidParameterException("unknown opmode " + opmode + " passed");
                }
            }
            if (!(this.cipher instanceof AEADGenericBlockCipher) || this.aeadParams != null) return;
            AEADCipher aeadCipher = ((AEADGenericBlockCipher)this.cipher).cipher;
            this.aeadParams = new AEADParameters((KeyParameter)this.ivParam.getParameters(), aeadCipher.getMac().length * 8, this.ivParam.getIV());
            return;
        }
        catch (IllegalArgumentException e) {
            throw new InvalidAlgorithmParameterException(e.getMessage(), e);
        }
        catch (Exception e) {
            throw new BaseWrapCipher.InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    private CipherParameters adjustParameters(AlgorithmParameterSpec params, CipherParameters param) {
        if (param instanceof ParametersWithIV) {
            CipherParameters key = ((ParametersWithIV)param).getParameters();
            if (params instanceof IvParameterSpec) {
                IvParameterSpec iv = (IvParameterSpec)params;
                this.ivParam = new ParametersWithIV(key, iv.getIV());
                param = this.ivParam;
            } else if (params instanceof GOST28147ParameterSpec) {
                GOST28147ParameterSpec gost28147Param = (GOST28147ParameterSpec)params;
                param = new ParametersWithSBox(param, gost28147Param.getSBox());
                if (gost28147Param.getIV() != null && this.ivLength != 0) {
                    this.ivParam = new ParametersWithIV(key, gost28147Param.getIV());
                    param = this.ivParam;
                }
            }
        } else if (params instanceof IvParameterSpec) {
            IvParameterSpec iv = (IvParameterSpec)params;
            this.ivParam = new ParametersWithIV(param, iv.getIV());
            param = this.ivParam;
        } else if (params instanceof GOST28147ParameterSpec) {
            GOST28147ParameterSpec gost28147Param = (GOST28147ParameterSpec)params;
            param = new ParametersWithSBox(param, gost28147Param.getSBox());
            if (gost28147Param.getIV() != null && this.ivLength != 0) {
                param = new ParametersWithIV(param, gost28147Param.getIV());
            }
        }
        return param;
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null && (paramSpec = SpecUtil.extractSpec(params, availableSpecs)) == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
        }
        this.engineInit(opmode, key, paramSpec, random);
        this.engineParams = params;
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            this.engineInit(opmode, key, (AlgorithmParameterSpec)null, random);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override
    protected void engineUpdateAAD(byte[] input, int offset, int length) {
        this.cipher.updateAAD(input, offset, length);
    }

    @Override
    protected void engineUpdateAAD(ByteBuffer src) {
        int remaining = src.remaining();
        if (remaining >= 1) {
            if (src.hasArray()) {
                this.engineUpdateAAD(src.array(), src.arrayOffset() + src.position(), remaining);
                src.position(src.limit());
            } else if (remaining <= 512) {
                byte[] data = new byte[remaining];
                src.get(data);
                this.engineUpdateAAD(data, 0, data.length);
                Arrays.fill(data, (byte)0);
            } else {
                int length;
                byte[] data = new byte[512];
                do {
                    length = Math.min(data.length, remaining);
                    src.get(data, 0, length);
                    this.engineUpdateAAD(data, 0, length);
                } while ((remaining -= length) > 0);
                Arrays.fill(data, (byte)0);
            }
        }
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        int length = this.cipher.getUpdateOutputSize(inputLen);
        if (length > 0) {
            byte[] out = new byte[length];
            int len = this.cipher.processBytes(input, inputOffset, inputLen, out, 0);
            if (len == 0) {
                return null;
            }
            if (len != out.length) {
                byte[] tmp = new byte[len];
                System.arraycopy(out, 0, tmp, 0, len);
                return tmp;
            }
            return out;
        }
        this.cipher.processBytes(input, inputOffset, inputLen, null, 0);
        return null;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + this.cipher.getUpdateOutputSize(inputLen) > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            return this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
        }
        catch (DataLengthException e) {
            throw new IllegalStateException(e.toString());
        }
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        int len = 0;
        byte[] tmp = new byte[this.engineGetOutputSize(inputLen)];
        if (inputLen != 0) {
            len = this.cipher.processBytes(input, inputOffset, inputLen, tmp, 0);
        }
        try {
            len += this.cipher.doFinal(tmp, len);
        }
        catch (DataLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
        if (len == tmp.length) {
            return tmp;
        }
        if (len > tmp.length) {
            throw new IllegalBlockSizeException("internal buffer overflow");
        }
        byte[] out = new byte[len];
        System.arraycopy(tmp, 0, out, 0, len);
        return out;
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        int len = 0;
        if (outputOffset + this.engineGetOutputSize(inputLen) > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            if (inputLen != 0) {
                len = this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
            }
            return len + this.cipher.doFinal(output, outputOffset + len);
        }
        catch (OutputLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
        catch (DataLengthException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    private boolean isAEADModeName(String modeName) {
        return "CCM".equals(modeName) || "EAX".equals(modeName) || "GCM".equals(modeName) || "GCM-SIV".equals(modeName) || "OCB".equals(modeName);
    }

    private static class AEADGenericBlockCipher
    implements GenericBlockCipher {
        private static final Constructor aeadBadTagConstructor;
        private AEADCipher cipher;

        private static Constructor findExceptionConstructor(Class clazz) {
            try {
                return clazz.getConstructor(String.class);
            }
            catch (Exception e) {
                return null;
            }
        }

        AEADGenericBlockCipher(AEADCipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override
        public String getAlgorithmName() {
            if (this.cipher instanceof AEADBlockCipher) {
                return ((AEADBlockCipher)this.cipher).getUnderlyingCipher().getAlgorithmName();
            }
            return this.cipher.getAlgorithmName();
        }

        @Override
        public boolean wrapOnNoPadding() {
            return false;
        }

        @Override
        public BlockCipher getUnderlyingCipher() {
            if (this.cipher instanceof AEADBlockCipher) {
                return ((AEADBlockCipher)this.cipher).getUnderlyingCipher();
            }
            return null;
        }

        @Override
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override
        public void updateAAD(byte[] input, int offset, int length) {
            this.cipher.processAADBytes(input, offset, length);
        }

        @Override
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            }
            catch (InvalidCipherTextException e) {
                if (aeadBadTagConstructor != null) {
                    BadPaddingException aeadBadTag = null;
                    try {
                        aeadBadTag = (BadPaddingException)aeadBadTagConstructor.newInstance(e.getMessage());
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (aeadBadTag != null) {
                        throw aeadBadTag;
                    }
                }
                throw new BadPaddingException(e.getMessage());
            }
        }

        static {
            Class aeadBadTagClass = ClassUtil.loadClass(BaseBlockCipher.class, "javax.crypto.AEADBadTagException");
            aeadBadTagConstructor = aeadBadTagClass != null ? AEADGenericBlockCipher.findExceptionConstructor(aeadBadTagClass) : null;
        }
    }

    private static class BufferedFPEBlockCipher
    implements GenericBlockCipher {
        private FPEEngine cipher;
        private BaseWrapCipher.ErasableOutputStream eOut = new BaseWrapCipher.ErasableOutputStream();

        BufferedFPEBlockCipher(FPEEngine cipher) {
            this.cipher = cipher;
        }

        @Override
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override
        public boolean wrapOnNoPadding() {
            return false;
        }

        @Override
        public String getAlgorithmName() {
            return this.cipher.getAlgorithmName();
        }

        @Override
        public BlockCipher getUnderlyingCipher() {
            throw new IllegalStateException("not applicable for FPE");
        }

        @Override
        public int getOutputSize(int len) {
            return this.eOut.size() + len;
        }

        @Override
        public int getUpdateOutputSize(int len) {
            return 0;
        }

        @Override
        public void updateAAD(byte[] input, int offset, int length) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }

        @Override
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            this.eOut.write(in);
            return 0;
        }

        @Override
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            this.eOut.write(in, inOff, len);
            return 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                int n = this.cipher.processBlock(this.eOut.getBuf(), 0, this.eOut.size(), out, outOff);
                return n;
            }
            finally {
                this.eOut.erase();
            }
        }
    }

    private static class BufferedGenericBlockCipher
    implements GenericBlockCipher {
        private BufferedBlockCipher cipher;

        BufferedGenericBlockCipher(BufferedBlockCipher cipher) {
            this.cipher = cipher;
        }

        BufferedGenericBlockCipher(BlockCipher cipher) {
            this(cipher, new PKCS7Padding());
        }

        BufferedGenericBlockCipher(BlockCipher cipher, BlockCipherPadding padding) {
            this.cipher = cipher instanceof MultiBlockCipher ? new PaddedBufferedMultiBlockCipher((MultiBlockCipher)cipher, padding) : new PaddedBufferedBlockCipher(cipher, padding);
        }

        @Override
        public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
            this.cipher.init(forEncryption, params);
        }

        @Override
        public boolean wrapOnNoPadding() {
            return !(this.cipher instanceof CTSBlockCipher);
        }

        @Override
        public String getAlgorithmName() {
            return this.cipher.getUnderlyingCipher().getAlgorithmName();
        }

        @Override
        public BlockCipher getUnderlyingCipher() {
            return this.cipher.getUnderlyingCipher();
        }

        @Override
        public int getOutputSize(int len) {
            return this.cipher.getOutputSize(len);
        }

        @Override
        public int getUpdateOutputSize(int len) {
            return this.cipher.getUpdateOutputSize(len);
        }

        @Override
        public void updateAAD(byte[] input, int offset, int length) {
            throw new UnsupportedOperationException("AAD is not supported in the current mode.");
        }

        @Override
        public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processByte(in, out, outOff);
        }

        @Override
        public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
            return this.cipher.processBytes(in, inOff, len, out, outOff);
        }

        @Override
        public int doFinal(byte[] out, int outOff) throws IllegalStateException, BadPaddingException {
            try {
                return this.cipher.doFinal(out, outOff);
            }
            catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            }
        }
    }

    private static interface GenericBlockCipher {
        public void init(boolean var1, CipherParameters var2) throws IllegalArgumentException;

        public boolean wrapOnNoPadding();

        public String getAlgorithmName();

        public BlockCipher getUnderlyingCipher();

        public int getOutputSize(int var1);

        public int getUpdateOutputSize(int var1);

        public void updateAAD(byte[] var1, int var2, int var3);

        public int processByte(byte var1, byte[] var2, int var3) throws DataLengthException;

        public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException;

        public int doFinal(byte[] var1, int var2) throws IllegalStateException, BadPaddingException;
    }
}

