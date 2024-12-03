/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dh;

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
import javax.crypto.interfaces.DHKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHKeyParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.IESWithCipherParameters;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.parsers.DHIESPublicKeyParser;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.asymmetric.util.DHUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.IESKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class IESCipher
extends BaseCipherSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final int ivLength;
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
        if (key instanceof DHKey) {
            return ((DHKey)((Object)key)).getParams().getP().bitLength();
        }
        throw new IllegalArgumentException("not a DH key");
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
        if (this.key == null) {
            throw new IllegalStateException("cipher not initialised");
        }
        int len1 = this.engine.getMac().getMacSize();
        int len2 = this.otherKeyParameter == null ? 1 + 2 * (((DHKeyParameters)this.key).getParameters().getP().bitLength() + 7) / 8 : 0;
        if (this.engine.getCipher() == null) {
            len3 = inputLen;
        } else if (this.state == 1 || this.state == 3) {
            len3 = this.engine.getCipher().getOutputSize(inputLen);
        } else if (this.state == 2 || this.state == 4) {
            len3 = this.engine.getCipher().getOutputSize(inputLen - len1 - len2);
        } else {
            throw new IllegalStateException("cipher not initialised");
        }
        if (this.state == 1 || this.state == 3) {
            return this.buffer.size() + len1 + len2 + len3;
        }
        if (this.state == 2 || this.state == 4) {
            return this.buffer.size() - len1 - len2 + len3;
        }
        throw new IllegalStateException("IESCipher not initialised");
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
        if (!(engineSpec instanceof IESParameterSpec)) {
            throw new InvalidAlgorithmParameterException("must be passed IES parameters");
        }
        this.engineSpec = (IESParameterSpec)engineSpec;
        byte[] nonce = this.engineSpec.getNonce();
        if (this.ivLength != 0 && (nonce == null || nonce.length != this.ivLength)) {
            throw new InvalidAlgorithmParameterException("NONCE in IES Parameters needs to be " + this.ivLength + " bytes long");
        }
        if (opmode == 1 || opmode == 3) {
            if (key instanceof DHPublicKey) {
                this.key = DHUtil.generatePublicKeyParameter((PublicKey)key);
            } else {
                if (!(key instanceof IESKey)) throw new InvalidKeyException("must be passed recipient's public DH key for encryption");
                IESKey ieKey = (IESKey)key;
                this.key = DHUtil.generatePublicKeyParameter(ieKey.getPublic());
                this.otherKeyParameter = DHUtil.generatePrivateKeyParameter(ieKey.getPrivate());
            }
        } else {
            if (opmode != 2 && opmode != 4) throw new InvalidKeyException("must be passed EC key");
            if (key instanceof DHPrivateKey) {
                this.key = DHUtil.generatePrivateKeyParameter((PrivateKey)key);
            } else {
                if (!(key instanceof IESKey)) throw new InvalidKeyException("must be passed recipient's private DH key for decryption");
                IESKey ieKey = (IESKey)key;
                this.otherKeyParameter = DHUtil.generatePublicKeyParameter(ieKey.getPublic());
                this.key = DHUtil.generatePrivateKeyParameter(ieKey.getPrivate());
            }
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
        if (inputLen != 0) {
            this.buffer.write(input, inputOffset, inputLen);
        }
        byte[] in = this.buffer.toByteArray();
        this.buffer.reset();
        CipherParameters params = new IESWithCipherParameters(this.engineSpec.getDerivationV(), this.engineSpec.getEncodingV(), this.engineSpec.getMacKeySize(), this.engineSpec.getCipherKeySize());
        if (this.engineSpec.getNonce() != null) {
            params = new ParametersWithIV(params, this.engineSpec.getNonce());
        }
        DHParameters dhParams = ((DHKeyParameters)this.key).getParameters();
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
        if (this.state == 1 || this.state == 3) {
            DHKeyPairGenerator gen = new DHKeyPairGenerator();
            gen.init(new DHKeyGenerationParameters(this.random, dhParams));
            EphemeralKeyPairGenerator kGen = new EphemeralKeyPairGenerator(gen, new KeyEncoder(){

                @Override
                public byte[] getEncoded(AsymmetricKeyParameter keyParameter) {
                    byte[] Vloc = new byte[(((DHKeyParameters)keyParameter).getParameters().getP().bitLength() + 7) / 8];
                    byte[] Vtmp = BigIntegers.asUnsignedByteArray(((DHPublicKeyParameters)keyParameter).getY());
                    if (Vtmp.length > Vloc.length) {
                        throw new IllegalArgumentException("Senders's public key longer than expected.");
                    }
                    System.arraycopy(Vtmp, 0, Vloc, Vloc.length - Vtmp.length, Vtmp.length);
                    return Vloc;
                }
            });
            try {
                this.engine.init(this.key, params, kGen);
                return this.engine.processBlock(in, 0, in.length);
            }
            catch (Exception e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        if (this.state == 2 || this.state == 4) {
            try {
                this.engine.init(this.key, params, new DHIESPublicKeyParser(((DHKeyParameters)this.key).getParameters()));
                return this.engine.processBlock(in, 0, in.length);
            }
            catch (InvalidCipherTextException e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        throw new IllegalStateException("IESCipher not initialised");
    }

    @Override
    public int engineDoFinal(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] buf = this.engineDoFinal(input, inputOffset, inputLength);
        System.arraycopy(buf, 0, output, outputOffset, buf.length);
        return buf.length;
    }

    public static class IES
    extends IESCipher {
        public IES() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1())));
        }
    }

    public static class IESwithAESCBC
    extends IESCipher {
        public IESwithAESCBC() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1()), new PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(AESEngine.newInstance()))), 16);
        }
    }

    public static class IESwithDESedeCBC
    extends IESCipher {
        public IESwithDESedeCBC() {
            super(new IESEngine(new DHBasicAgreement(), new KDF2BytesGenerator(DigestFactory.createSHA1()), new HMac(DigestFactory.createSHA1()), new PaddedBufferedBlockCipher(CBCBlockCipher.newInstance(new DESedeEngine()))), 8);
        }
    }
}

