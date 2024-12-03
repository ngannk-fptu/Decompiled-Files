/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.ec;

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
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class GMCipherSpi
extends CipherSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private SM2Engine engine;
    private int state = -1;
    private ErasableOutputStream buffer = new ErasableOutputStream();
    private AsymmetricKeyParameter key;
    private SecureRandom random;

    public GMCipherSpi(SM2Engine engine) {
        this.engine = engine;
    }

    @Override
    public int engineGetBlockSize() {
        return 0;
    }

    @Override
    public int engineGetKeySize(Key key) {
        if (key instanceof ECKey) {
            return ((ECKey)((Object)key)).getParameters().getCurve().getFieldSize();
        }
        throw new IllegalArgumentException("not an EC key");
    }

    @Override
    public byte[] engineGetIV() {
        return null;
    }

    @Override
    public AlgorithmParameters engineGetParameters() {
        return null;
    }

    @Override
    public void engineSetMode(String mode) throws NoSuchAlgorithmException {
        String modeName = Strings.toUpperCase(mode);
        if (!modeName.equals("NONE")) {
            throw new IllegalArgumentException("can't support mode " + mode);
        }
    }

    @Override
    public int engineGetOutputSize(int inputLen) {
        if (this.state == 1 || this.state == 3) {
            return this.engine.getOutputSize(inputLen);
        }
        if (this.state == 2 || this.state == 4) {
            return this.engine.getOutputSize(inputLen);
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public void engineSetPadding(String padding) throws NoSuchPaddingException {
        String paddingName = Strings.toUpperCase(padding);
        if (!paddingName.equals("NOPADDING")) {
            throw new NoSuchPaddingException("padding not available with IESCipher");
        }
    }

    @Override
    public void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null) {
            throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + params.getClass().getName());
        }
        this.engineInit(opmode, key, paramSpec, random);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void engineInit(int opmode, Key key, AlgorithmParameterSpec engineSpec, SecureRandom random) throws InvalidAlgorithmParameterException, InvalidKeyException {
        if (opmode == 1 || opmode == 3) {
            if (!(key instanceof PublicKey)) throw new InvalidKeyException("must be passed public EC key for encryption");
            this.key = ECUtils.generatePublicKeyParameter((PublicKey)key);
        } else {
            if (opmode != 2 && opmode != 4) throw new InvalidKeyException("must be passed EC key");
            if (!(key instanceof PrivateKey)) throw new InvalidKeyException("must be passed private EC key for decryption");
            this.key = ECUtil.generatePrivateKeyParameter((PrivateKey)key);
        }
        this.random = random != null ? random : CryptoServicesRegistrar.getSecureRandom();
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
        block11: {
            if (inputLen != 0) {
                this.buffer.write(input, inputOffset, inputLen);
            }
            if (this.state == 1 || this.state == 3) {
                try {
                    this.engine.init(true, new ParametersWithRandom(this.key, this.random));
                    byte[] byArray = this.engine.processBlock(this.buffer.getBuf(), 0, this.buffer.size());
                    return byArray;
                }
                catch (Exception e) {
                    throw new BadBlockException("unable to process block", e);
                }
            }
            if (this.state != 2 && this.state != 4) break block11;
            try {
                this.engine.init(false, this.key);
                byte[] e = this.engine.processBlock(this.buffer.getBuf(), 0, this.buffer.size());
                return e;
            }
            catch (Exception e) {
                throw new BadBlockException("unable to process block", e);
            }
        }
        throw new IllegalStateException("cipher not initialised");
        finally {
            this.buffer.erase();
        }
    }

    @Override
    public int engineDoFinal(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] buf = this.engineDoFinal(input, inputOffset, inputLength);
        System.arraycopy(buf, 0, output, outputOffset, buf.length);
        return buf.length;
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

    public static class SM2
    extends GMCipherSpi {
        public SM2() {
            super(new SM2Engine());
        }
    }

    public static class SM2withBlake2b
    extends GMCipherSpi {
        public SM2withBlake2b() {
            super(new SM2Engine(new Blake2bDigest(512)));
        }
    }

    public static class SM2withBlake2s
    extends GMCipherSpi {
        public SM2withBlake2s() {
            super(new SM2Engine(new Blake2sDigest(256)));
        }
    }

    public static class SM2withMD5
    extends GMCipherSpi {
        public SM2withMD5() {
            super(new SM2Engine(new MD5Digest()));
        }
    }

    public static class SM2withRMD
    extends GMCipherSpi {
        public SM2withRMD() {
            super(new SM2Engine(new RIPEMD160Digest()));
        }
    }

    public static class SM2withSha1
    extends GMCipherSpi {
        public SM2withSha1() {
            super(new SM2Engine(new SHA1Digest()));
        }
    }

    public static class SM2withSha224
    extends GMCipherSpi {
        public SM2withSha224() {
            super(new SM2Engine(new SHA224Digest()));
        }
    }

    public static class SM2withSha256
    extends GMCipherSpi {
        public SM2withSha256() {
            super(new SM2Engine(SHA256Digest.newInstance()));
        }
    }

    public static class SM2withSha384
    extends GMCipherSpi {
        public SM2withSha384() {
            super(new SM2Engine(new SHA384Digest()));
        }
    }

    public static class SM2withSha512
    extends GMCipherSpi {
        public SM2withSha512() {
            super(new SM2Engine(new SHA512Digest()));
        }
    }

    public static class SM2withWhirlpool
    extends GMCipherSpi {
        public SM2withWhirlpool() {
            super(new SM2Engine(new WhirlpoolDigest()));
        }
    }
}

