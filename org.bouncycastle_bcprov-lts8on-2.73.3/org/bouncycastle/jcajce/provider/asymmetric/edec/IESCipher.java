/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.agreement.XDHBasicAgreement;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.generators.X25519KeyPairGenerator;
import org.bouncycastle.crypto.generators.X448KeyPairGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.parsers.XIESPublicKeyParser;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.interfaces.XDHKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.EdECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class IESCipher
extends BaseCipherSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private int ivLength;
    private IESEngine engine;
    private int state = -1;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AlgorithmParameters engineParam = null;
    private IESParameterSpec engineSpec = null;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode = false;
    private AsymmetricKeyParameter otherKeyParameter = null;

    public IESCipher(IESEngine engine) {
        this.engine = engine;
        this.ivLength = 0;
    }

    public IESCipher(IESEngine engine, int ivLength) {
        this.engine = engine;
        this.ivLength = ivLength;
    }

    @Override
    public int engineGetBlockSize() {
        if (this.engine.getCipher() != null) {
            return this.engine.getCipher().getBlockSize();
        }
        return 0;
    }

    @Override
    public int engineGetKeySize(Key key) {
        if (key instanceof XDHKey) {
            String algorithm = ((XDHKey)key).getAlgorithm();
            if ("X25519".equalsIgnoreCase(algorithm)) {
                return 256;
            }
            if ("X448".equalsIgnoreCase(algorithm)) {
                return 448;
            }
            throw new IllegalArgumentException("unknown XDH key algorithm " + algorithm);
        }
        throw new IllegalArgumentException("not an XDH key");
    }

    @Override
    public byte[] engineGetIV() {
        if (this.engineSpec != null) {
            return this.engineSpec.getNonce();
        }
        return null;
    }

    @Override
    public AlgorithmParameters engineGetParameters() {
        if (this.engineParam == null && this.engineSpec != null) {
            try {
                this.engineParam = this.helper.createAlgorithmParameters("IES");
                this.engineParam.init(this.engineSpec);
            }
            catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParam;
    }

    @Override
    public void engineSetMode(String mode) throws NoSuchAlgorithmException {
        String modeName = Strings.toUpperCase(mode);
        if (modeName.equals("NONE")) {
            this.dhaesMode = false;
        } else if (modeName.equals("DHAES")) {
            this.dhaesMode = true;
        } else {
            throw new IllegalArgumentException("can't support mode " + mode);
        }
    }

    @Override
    public int engineGetOutputSize(int inputLen) {
        int len3;
        int len2;
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        int len1 = this.engine.getMac().getMacSize();
        if (this.otherKeyParameter == null) {
            ECCurve c = ((ECKeyParameters)this.key).getParameters().getCurve();
            int feSize = (c.getFieldSize() + 7) / 8;
            len2 = 2 * feSize;
        } else {
            len2 = 0;
        }
        int inLen = this.buffer.size() + inputLen;
        if (this.engine.getCipher() == null) {
            len3 = inLen;
        } else if (this.state == 1 || this.state == 3) {
            len3 = this.engine.getCipher().getOutputSize(inLen);
        } else if (this.state == 2 || this.state == 4) {
            len3 = this.engine.getCipher().getOutputSize(inLen - len1 - len2);
        } else {
            throw new IllegalStateException("cipher not initialised");
        }
        if (this.state == 1 || this.state == 3) {
            return len1 + len2 + len3;
        }
        if (this.state == 2 || this.state == 4) {
            return len3;
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public void engineSetPadding(String padding) throws NoSuchPaddingException {
        String paddingName = Strings.toUpperCase(padding);
        if (!(paddingName.equals("NOPADDING") || paddingName.equals("PKCS5PADDING") || paddingName.equals("PKCS7PADDING"))) {
            throw new NoSuchPaddingException("padding not available with IESCipher");
        }
    }

    @Override
    public void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        IESParameterSpec paramSpec = null;
        if (params != null) {
            try {
                paramSpec = params.getParameterSpec(IESParameterSpec.class);
            }
            catch (Exception e) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + e.toString());
            }
        }
        this.engineParam = params;
        this.engineInit(opmode, key, paramSpec, random);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void engineInit(int opmode, Key key, AlgorithmParameterSpec engineSpec, SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
        this.otherKeyParameter = null;
        if (!(engineSpec instanceof IESParameterSpec)) {
            throw new InvalidAlgorithmParameterException("must be passed IES parameters");
        }
        this.engineSpec = (IESParameterSpec)engineSpec;
        byte[] nonce = this.engineSpec.getNonce();
        if (this.ivLength != 0 && (nonce == null || nonce.length != this.ivLength)) {
            throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
        }
        if (opmode == 1 || opmode == 3) {
            if (!(key instanceof PublicKey)) throw new InvalidKeyException("must be passed recipient's public XDH key for encryption");
            this.key = EdECUtil.generatePublicKeyParameter((PublicKey)key);
        } else {
            if (opmode != 2 && opmode != 4) throw new InvalidKeyException("must be passed XDH key");
            if (!(key instanceof PrivateKey)) throw new InvalidKeyException("must be passed recipient's private XDH key for decryption");
            this.key = EdECUtil.generatePrivateKeyParameter((PrivateKey)key);
        }
        this.random = random;
        this.state = opmode;
        this.buffer.reset();
    }

    @Override
    public void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            this.engineInit(opmode, key, (AlgorithmParameterSpec)null, random);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException("cannot handle supplied parameter spec: " + e.getMessage());
        }
    }

    @Override
    public byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        this.buffer.write(input, inputOffset, inputLen);
        return null;
    }

    @Override
    public int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        this.buffer.write(input, inputOffset, inputLen);
        return 0;
    }

    @Override
    public byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        int fieldSize;
        if (inputLen != 0) {
            this.buffer.write(input, inputOffset, inputLen);
        }
        byte[] in = this.buffer.toByteArray();
        this.buffer.reset();
        CipherParameters params = new IESWithCipherParameters(this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize());
        if (this.engineSpec.getNonce() != null) {
            params = new ParametersWithIV(params, this.engineSpec.getNonce());
        }
        if (this.otherKeyParameter != null) {
            try {
                if (this.state == 1 || this.state == 3) {
                    this.engine.init(true, this.otherKeyParameter, this.key, params);
                } else {
                    this.engine.init(false, this.key, this.otherKeyParameter, params);
                }
                return this.engine.processBlock(in, 0, in.length);
            }
            catch (Exception e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        final boolean isX25519 = this.key instanceof X25519PublicKeyParameters || this.key instanceof X25519PrivateKeyParameters;
        int n = fieldSize = isX25519 ? 256 : 448;
        if (this.state == 1 || this.state == 3) {
            AsymmetricCipherKeyPairGenerator kpGen = isX25519 ? new X25519KeyPairGenerator() : new X448KeyPairGenerator();
            kpGen.init(new KeyGenerationParameters(this.random, fieldSize));
            EphemeralKeyPairGenerator epKpGen = new EphemeralKeyPairGenerator(kpGen, new KeyEncoder(){

                @Override
                public byte[] getEncoded(AsymmetricKeyParameter keyParameter) {
                    return isX25519 ? ((X25519PublicKeyParameters)keyParameter).getEncoded() : ((X448PublicKeyParameters)keyParameter).getEncoded();
                }
            });
            try {
                this.engine.init(this.key, params, epKpGen);
                return this.engine.processBlock(in, 0, in.length);
            }
            catch (Exception e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        if (this.state == 2 || this.state == 4) {
            try {
                this.engine.init(this.key, params, new XIESPublicKeyParser(isX25519));
                return this.engine.processBlock(in, 0, in.length);
            }
            catch (InvalidCipherTextException e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public int engineDoFinal(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] buf = this.engineDoFinal(input, inputOffset, inputLength);
        System.arraycopy(buf, 0, output, outputOffset, buf.length);
        return buf.length;
    }

    public static class XIES
    extends IESCipher {
        public XIES() {
            this(DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public XIES(Digest kdfDigest, Digest macDigest) {
            super(new IESEngine(new XDHBasicAgreement(), new KDF2BytesGenerator(kdfDigest), new HMac(macDigest)));
        }
    }

    public static class XIESwithAESCBC
    extends XIESwithCipher {
        public XIESwithAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16);
        }
    }

    public static class XIESwithCipher
    extends IESCipher {
        public XIESwithCipher(BlockCipher cipher, int ivLength) {
            this(cipher, ivLength, DigestFactory.createSHA1(), DigestFactory.createSHA1());
        }

        public XIESwithCipher(BlockCipher cipher, int ivLength, Digest kdfDigest, Digest macDigest) {
            super(new IESEngine(new XDHBasicAgreement(), new KDF2BytesGenerator(kdfDigest), new HMac(macDigest), new PaddedBufferedBlockCipher(cipher)), ivLength);
        }
    }

    public static class XIESwithDESedeCBC
    extends XIESwithCipher {
        public XIESwithDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8);
        }
    }

    public static class XIESwithSHA256
    extends XIES {
        public XIESwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class XIESwithSHA256andAESCBC
    extends XIESwithCipher {
        public XIESwithSHA256andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class XIESwithSHA256andDESedeCBC
    extends XIESwithCipher {
        public XIESwithSHA256andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA256(), DigestFactory.createSHA256());
        }
    }

    public static class XIESwithSHA384
    extends XIES {
        public XIESwithSHA384() {
            super(DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class XIESwithSHA384andAESCBC
    extends XIESwithCipher {
        public XIESwithSHA384andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class XIESwithSHA384andDESedeCBC
    extends XIESwithCipher {
        public XIESwithSHA384andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA384(), DigestFactory.createSHA384());
        }
    }

    public static class XIESwithSHA512
    extends XIES {
        public XIESwithSHA512() {
            super(DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    public static class XIESwithSHA512andAESCBC
    extends XIESwithCipher {
        public XIESwithSHA512andAESCBC() {
            super(CBCBlockCipher.newInstance(AESEngine.newInstance()), 16, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }

    public static class XIESwithSHA512andDESedeCBC
    extends XIESwithCipher {
        public XIESwithSHA512andDESedeCBC() {
            super(CBCBlockCipher.newInstance(new DESedeEngine()), 8, DigestFactory.createSHA512(), DigestFactory.createSHA512());
        }
    }
}

