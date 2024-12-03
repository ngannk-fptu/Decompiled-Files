/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.io.ByteArrayOutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.symmetric.util.SpecUtil;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

public abstract class BaseWrapCipher
extends CipherSpi
implements PBE {
    private Class[] availableSpecs = new Class[]{GOST28147WrapParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class};
    protected int pbeType = 2;
    protected int pbeHash = 1;
    protected int pbeKeySize;
    protected int pbeIvSize;
    protected AlgorithmParameters engineParams = null;
    protected Wrapper wrapEngine = null;
    private int ivSize;
    private byte[] iv;
    private ErasableOutputStream wrapStream = null;
    private boolean forWrapping;
    private final JcaJceHelper helper = new BCJcaJceHelper();

    protected BaseWrapCipher() {
    }

    protected BaseWrapCipher(Wrapper wrapEngine) {
        this(wrapEngine, 0);
    }

    protected BaseWrapCipher(Wrapper wrapEngine, int ivSize) {
        this.wrapEngine = wrapEngine;
        this.ivSize = ivSize;
    }

    @Override
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override
    protected byte[] engineGetIV() {
        return Arrays.clone(this.iv);
    }

    @Override
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        return -1;
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.iv != null) {
            String name = this.wrapEngine.getAlgorithmName();
            if (name.indexOf(47) >= 0) {
                name = name.substring(0, name.indexOf(47));
            }
            try {
                this.engineParams = this.createParametersInstance(name);
                this.engineParams.init(new IvParameterSpec(this.iv));
            }
            catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    protected final AlgorithmParameters createParametersInstance(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(algorithm);
    }

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("can't support mode " + mode);
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + padding + " unknown.");
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        void var5_17;
        if (key instanceof BCPBEKey) {
            BCPBEKey k = (BCPBEKey)key;
            if (params instanceof PBEParameterSpec) {
                CipherParameters cipherParameters = PBE.Util.makePBEParameters(k, params, this.wrapEngine.getAlgorithmName());
            } else {
                if (k.getParam() == null) throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                CipherParameters cipherParameters = k.getParam();
            }
        } else {
            KeyParameter keyParameter = new KeyParameter(key.getEncoded());
        }
        if (params instanceof IvParameterSpec) {
            void var5_11;
            IvParameterSpec ivSpec = (IvParameterSpec)params;
            this.iv = ivSpec.getIV();
            ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)var5_11, this.iv);
        }
        if (params instanceof GOST28147WrapParameterSpec) {
            void var5_15;
            GOST28147WrapParameterSpec spec = (GOST28147WrapParameterSpec)params;
            byte[] sBox = spec.getSBox();
            if (sBox != null) {
                void var5_13;
                ParametersWithSBox parametersWithSBox = new ParametersWithSBox((CipherParameters)var5_13, sBox);
            }
            ParametersWithUKM parametersWithUKM = new ParametersWithUKM((CipherParameters)var5_15, spec.getUKM());
        }
        if (var5_17 instanceof KeyParameter && this.ivSize != 0 && (opmode == 3 || opmode == 1)) {
            this.iv = new byte[this.ivSize];
            random.nextBytes(this.iv);
            ParametersWithIV parametersWithIV = new ParametersWithIV((CipherParameters)var5_17, this.iv);
        }
        if (random != null) {
            void var5_19;
            ParametersWithRandom parametersWithRandom = new ParametersWithRandom((CipherParameters)var5_19, random);
        }
        try {
            switch (opmode) {
                case 3: {
                    void var5_21;
                    this.wrapEngine.init(true, (CipherParameters)var5_21);
                    this.wrapStream = null;
                    this.forWrapping = true;
                    return;
                }
                case 4: {
                    void var5_21;
                    this.wrapEngine.init(false, (CipherParameters)var5_21);
                    this.wrapStream = null;
                    this.forWrapping = false;
                    return;
                }
                case 1: {
                    void var5_21;
                    this.wrapEngine.init(true, (CipherParameters)var5_21);
                    this.wrapStream = new ErasableOutputStream();
                    this.forWrapping = true;
                    return;
                }
                case 2: {
                    void var5_21;
                    this.wrapEngine.init(false, (CipherParameters)var5_21);
                    this.wrapStream = new ErasableOutputStream();
                    this.forWrapping = false;
                    return;
                }
                default: {
                    throw new InvalidParameterException("Unknown mode parameter passed to init.");
                }
            }
        }
        catch (Exception e) {
            throw new InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null && (paramSpec = SpecUtil.extractSpec(params, this.availableSpecs)) == null) {
            throw new InvalidAlgorithmParameterException("can't handle parameter " + params.toString());
        }
        this.engineParams = params;
        this.engineInit(opmode, key, paramSpec, random);
    }

    @Override
    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        try {
            this.engineInit(opmode, key, (AlgorithmParameterSpec)null, random);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new InvalidKeyOrParametersException(e.getMessage(), e);
        }
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        if (this.wrapStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        this.wrapStream.write(input, inputOffset, inputLen);
        return null;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (this.wrapStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        this.wrapStream.write(input, inputOffset, inputLen);
        return 0;
    }

    /*
     * Loose catch block
     */
    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        if (this.wrapStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        if (input != null) {
            this.wrapStream.write(input, inputOffset, inputLen);
        }
        if (this.forWrapping) {
            try {
                byte[] byArray = this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                return byArray;
            }
            catch (Exception e) {
                throw new IllegalBlockSizeException(e.getMessage());
            }
        }
        byte[] e = this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
        return e;
        catch (InvalidCipherTextException e2) {
            throw new BadPaddingException(e2.getMessage());
        }
        finally {
            this.wrapStream.erase();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        if (this.wrapStream == null) {
            throw new IllegalStateException("not supported in a wrapping mode");
        }
        this.wrapStream.write(input, inputOffset, inputLen);
        try {
            byte[] enc;
            if (this.forWrapping) {
                try {
                    enc = this.wrapEngine.wrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
                }
                catch (Exception e) {
                    throw new IllegalBlockSizeException(e.getMessage());
                }
            }
            try {
                enc = this.wrapEngine.unwrap(this.wrapStream.getBuf(), 0, this.wrapStream.size());
            }
            catch (InvalidCipherTextException e) {
                throw new BadPaddingException(e.getMessage());
            }
            if (outputOffset + enc.length > output.length) {
                throw new ShortBufferException("output buffer too short for input.");
            }
            System.arraycopy(enc, 0, output, outputOffset, enc.length);
            int n = enc.length;
            return n;
        }
        finally {
            this.wrapStream.erase();
        }
    }

    @Override
    protected byte[] engineWrap(Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            if (this.wrapEngine == null) {
                return this.engineDoFinal(encoded, 0, encoded.length);
            }
            return this.wrapEngine.wrap(encoded, 0, encoded.length);
        }
        catch (BadPaddingException e) {
            throw new IllegalBlockSizeException(e.getMessage());
        }
    }

    @Override
    protected Key engineUnwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, int wrappedKeyType) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] encoded;
        try {
            encoded = this.wrapEngine == null ? this.engineDoFinal(wrappedKey, 0, wrappedKey.length) : this.wrapEngine.unwrap(wrappedKey, 0, wrappedKey.length);
        }
        catch (InvalidCipherTextException e) {
            throw new InvalidKeyException(e.getMessage());
        }
        catch (BadPaddingException e) {
            throw new InvalidKeyException(e.getMessage());
        }
        catch (IllegalBlockSizeException e2) {
            throw new InvalidKeyException(e2.getMessage());
        }
        if (wrappedKeyType == 3) {
            return new SecretKeySpec(encoded, wrappedKeyAlgorithm);
        }
        if (wrappedKeyAlgorithm.equals("") && wrappedKeyType == 2) {
            try {
                PrivateKeyInfo in = PrivateKeyInfo.getInstance(encoded);
                PrivateKey privKey = BouncyCastleProvider.getPrivateKey(in);
                if (privKey != null) {
                    return privKey;
                }
                throw new InvalidKeyException("algorithm " + in.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
            }
            catch (Exception e) {
                throw new InvalidKeyException("Invalid key encoding.");
            }
        }
        try {
            KeyFactory kf = this.helper.createKeyFactory(wrappedKeyAlgorithm);
            if (wrappedKeyType == 1) {
                return kf.generatePublic(new X509EncodedKeySpec(encoded));
            }
            if (wrappedKeyType == 2) {
                return kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
            }
        }
        catch (NoSuchProviderException e) {
            throw new InvalidKeyException("Unknown key type " + e.getMessage());
        }
        catch (InvalidKeySpecException e2) {
            throw new InvalidKeyException("Unknown key type " + e2.getMessage());
        }
        throw new InvalidKeyException("Unknown key type " + wrappedKeyType);
    }

    protected static final class ErasableOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }

        public void erase() {
            Arrays.fill(this.buf, (byte)0);
            this.reset();
        }
    }

    protected static class InvalidKeyOrParametersException
    extends InvalidKeyException {
        private final Throwable cause;

        InvalidKeyOrParametersException(String msg, Throwable cause) {
            super(msg);
            this.cause = cause;
        }

        @Override
        public Throwable getCause() {
            return this.cause;
        }
    }
}

