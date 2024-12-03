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
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.engines.IESEngine;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.EphemeralKeyPairGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.ec.ECUtils;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.interfaces.ECKey;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class IESKEMCipher
extends BaseCipherSpi {
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final ECDHCBasicAgreement agreement;
    private final KDF2BytesGenerator kdf;
    private final Mac hMac;
    private final int macKeyLength;
    private final int macLength;
    private int ivLength;
    private IESEngine engine;
    private int state = -1;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private AlgorithmParameters engineParam = null;
    private IESKEMParameterSpec engineSpec = null;
    private AsymmetricKeyParameter key;
    private SecureRandom random;
    private boolean dhaesMode = false;
    private AsymmetricKeyParameter otherKeyParameter = null;

    public IESKEMCipher(ECDHCBasicAgreement agreement, KDF2BytesGenerator kdf, Mac hMac, int macKeyLength, int macLength) {
        this.agreement = agreement;
        this.kdf = kdf;
        this.hMac = hMac;
        this.macKeyLength = macKeyLength;
        this.macLength = macLength;
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
        throw new NoSuchAlgorithmException("can't support mode " + mode);
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
        throw new NoSuchPaddingException("padding not available with IESCipher");
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
        this.engineSpec = (IESKEMParameterSpec)engineSpec;
        if (opmode == 1 || opmode == 3) {
            if (!(key instanceof PublicKey)) throw new InvalidKeyException("must be passed recipient's public EC key for encryption");
            this.key = ECUtils.generatePublicKeyParameter((PublicKey)key);
        } else {
            if (opmode != 2 && opmode != 4) throw new InvalidKeyException("must be passed EC key");
            if (!(key instanceof PrivateKey)) throw new InvalidKeyException("must be passed recipient's private EC key for decryption");
            this.key = ECUtils.generatePrivateKeyParameter((PrivateKey)key);
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
        ECDomainParameters ecParams = ((ECKeyParameters)this.key).getParameters();
        if (this.state == 1 || this.state == 3) {
            ECKeyPairGenerator gen = new ECKeyPairGenerator();
            gen.init(new ECKeyGenerationParameters(ecParams, this.random));
            final boolean usePointCompression = this.engineSpec.hasUsePointCompression();
            EphemeralKeyPairGenerator kGen = new EphemeralKeyPairGenerator(gen, new KeyEncoder(){

                @Override
                public byte[] getEncoded(AsymmetricKeyParameter keyParameter) {
                    return ((ECPublicKeyParameters)keyParameter).getQ().getEncoded(usePointCompression);
                }
            });
            EphemeralKeyPair kp = kGen.generate();
            this.agreement.init(kp.getKeyPair().getPrivate());
            byte[] secret = converter.integerToBytes(this.agreement.calculateAgreement(this.key), converter.getByteLength(ecParams.getCurve()));
            byte[] out = new byte[inputLen + this.macKeyLength];
            this.kdf.init(new KDFParameters(secret, this.engineSpec.getRecipientInfo()));
            this.kdf.generateBytes(out, 0, out.length);
            byte[] enc = new byte[inputLen + this.macLength];
            for (int i = 0; i != inputLen; ++i) {
                enc[i] = (byte)(input[inputOffset + i] ^ out[i]);
            }
            KeyParameter macKey = new KeyParameter(out, inputLen, out.length - inputLen);
            this.hMac.init(macKey);
            this.hMac.update(enc, 0, inputLen);
            byte[] mac = new byte[this.hMac.getMacSize()];
            this.hMac.doFinal(mac, 0);
            Arrays.clear(macKey.getKey());
            Arrays.clear(out);
            System.arraycopy(mac, 0, enc, inputLen, this.macLength);
            return Arrays.concatenate(kp.getEncodedPublicKey(), enc);
        }
        if (this.state == 2 || this.state == 4) {
            ECPrivateKeyParameters k = (ECPrivateKeyParameters)this.key;
            ECCurve curve = k.getParameters().getCurve();
            int pEncLength = (curve.getFieldSize() + 7) / 8;
            pEncLength = input[inputOffset] == 4 ? 1 + 2 * pEncLength : 1 + pEncLength;
            int keyLength = inputLen - (pEncLength + this.macLength);
            ECPoint q = curve.decodePoint(Arrays.copyOfRange(input, inputOffset, inputOffset + pEncLength));
            this.agreement.init(this.key);
            byte[] secret = converter.integerToBytes(this.agreement.calculateAgreement(new ECPublicKeyParameters(q, k.getParameters())), converter.getByteLength(ecParams.getCurve()));
            byte[] out = new byte[keyLength + this.macKeyLength];
            this.kdf.init(new KDFParameters(secret, this.engineSpec.getRecipientInfo()));
            this.kdf.generateBytes(out, 0, out.length);
            byte[] dec = new byte[keyLength];
            for (int i = 0; i != dec.length; ++i) {
                dec[i] = (byte)(input[inputOffset + pEncLength + i] ^ out[i]);
            }
            KeyParameter macKey = new KeyParameter(out, keyLength, out.length - keyLength);
            this.hMac.init(macKey);
            this.hMac.update(input, inputOffset + pEncLength, dec.length);
            byte[] mac = new byte[this.hMac.getMacSize()];
            this.hMac.doFinal(mac, 0);
            Arrays.clear(macKey.getKey());
            Arrays.clear(out);
            if (!Arrays.constantTimeAreEqual(this.macLength, mac, 0, input, inputOffset + (inputLen - this.macLength))) {
                throw new BadPaddingException("mac field");
            }
            return dec;
        }
        throw new IllegalStateException("cipher not initialised");
    }

    @Override
    public int engineDoFinal(byte[] input, int inputOffset, int inputLength, byte[] output, int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        byte[] buf = this.engineDoFinal(input, inputOffset, inputLength);
        System.arraycopy(buf, 0, output, outputOffset, buf.length);
        return buf.length;
    }

    public static class KEM
    extends IESKEMCipher {
        public KEM(Digest kdfDigest, Digest macDigest, int macKeyLength, int macLength) {
            super(new ECDHCBasicAgreement(), new KDF2BytesGenerator(kdfDigest), new HMac(macDigest), macKeyLength, macLength);
        }
    }

    public static class KEMwithSHA256
    extends KEM {
        public KEMwithSHA256() {
            super(DigestFactory.createSHA256(), DigestFactory.createSHA256(), 32, 16);
        }
    }
}

