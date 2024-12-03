/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.ISO9796d1Encoding;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.RSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseCipherSpi;
import org.bouncycastle.jcajce.provider.util.BadBlockException;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Strings;

public class CipherSpi
extends BaseCipherSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private AsymmetricBlockCipher cipher;
    private AlgorithmParameterSpec paramSpec;
    private AlgorithmParameters engineParams;
    private boolean publicKeyOnly = false;
    private boolean privateKeyOnly = false;
    private BaseCipherSpi.ErasableOutputStream bOut = new BaseCipherSpi.ErasableOutputStream();

    public CipherSpi(AsymmetricBlockCipher engine) {
        this.cipher = engine;
    }

    public CipherSpi(OAEPParameterSpec pSpec) {
        try {
            this.initFromSpec(pSpec);
        }
        catch (NoSuchPaddingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public CipherSpi(boolean publicKeyOnly, boolean privateKeyOnly, AsymmetricBlockCipher engine) {
        this.publicKeyOnly = publicKeyOnly;
        this.privateKeyOnly = privateKeyOnly;
        this.cipher = engine;
    }

    private void initFromSpec(OAEPParameterSpec pSpec) throws NoSuchPaddingException {
        MGF1ParameterSpec mgfParams = (MGF1ParameterSpec)pSpec.getMGFParameters();
        Digest digest = DigestFactory.getDigest(mgfParams.getDigestAlgorithm());
        if (digest == null) {
            throw new NoSuchPaddingException("no match on OAEP constructor for digest algorithm: " + mgfParams.getDigestAlgorithm());
        }
        this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, ((PSource.PSpecified)pSpec.getPSource()).getValue());
        this.paramSpec = pSpec;
    }

    @Override
    protected int engineGetBlockSize() {
        try {
            return this.cipher.getInputBlockSize();
        }
        catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override
    protected int engineGetKeySize(Key key) {
        if (key instanceof RSAPrivateKey) {
            RSAPrivateKey k = (RSAPrivateKey)key;
            return k.getModulus().bitLength();
        }
        if (key instanceof RSAPublicKey) {
            RSAPublicKey k = (RSAPublicKey)key;
            return k.getModulus().bitLength();
        }
        throw new IllegalArgumentException("not an RSA key!");
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        try {
            return this.cipher.getOutputBlockSize();
        }
        catch (NullPointerException e) {
            throw new IllegalStateException("RSA Cipher not initialised");
        }
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null && this.paramSpec != null) {
            try {
                this.engineParams = this.helper.createAlgorithmParameters("OAEP");
                this.engineParams.init(this.paramSpec);
            }
            catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        return this.engineParams;
    }

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        String md = Strings.toUpperCase(mode);
        if (md.equals("NONE") || md.equals("ECB")) {
            return;
        }
        if (md.equals("1")) {
            this.privateKeyOnly = true;
            this.publicKeyOnly = false;
            return;
        }
        if (md.equals("2")) {
            this.privateKeyOnly = false;
            this.publicKeyOnly = true;
            return;
        }
        throw new NoSuchAlgorithmException("can't support mode " + mode);
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        String pad = Strings.toUpperCase(padding);
        if (pad.equals("NOPADDING")) {
            this.cipher = new RSABlindedEngine();
        } else if (pad.equals("PKCS1PADDING")) {
            this.cipher = new PKCS1Encoding(new RSABlindedEngine());
        } else if (pad.equals("ISO9796-1PADDING")) {
            this.cipher = new ISO9796d1Encoding(new RSABlindedEngine());
        } else if (pad.equals("OAEPWITHMD5ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("MD5", "MGF1", new MGF1ParameterSpec("MD5"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPPADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        } else if (pad.equals("OAEPWITHSHA1ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-1ANDMGF1PADDING")) {
            this.initFromSpec(OAEPParameterSpec.DEFAULT);
        } else if (pad.equals("OAEPWITHSHA224ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-224ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-224", "MGF1", new MGF1ParameterSpec("SHA-224"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA256ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-256ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA384ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-384ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA512ANDMGF1PADDING") || pad.equals("OAEPWITHSHA-512ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA3-224ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-224", "MGF1", new MGF1ParameterSpec("SHA3-224"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA3-256ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-256", "MGF1", new MGF1ParameterSpec("SHA3-256"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA3-384ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-384", "MGF1", new MGF1ParameterSpec("SHA3-384"), PSource.PSpecified.DEFAULT));
        } else if (pad.equals("OAEPWITHSHA3-512ANDMGF1PADDING")) {
            this.initFromSpec(new OAEPParameterSpec("SHA3-512", "MGF1", new MGF1ParameterSpec("SHA3-512"), PSource.PSpecified.DEFAULT));
        } else {
            throw new NoSuchPaddingException(padding + " unavailable with RSA.");
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        if (params == null || params instanceof OAEPParameterSpec) {
            if (key instanceof RSAPublicKey) {
                if (this.privateKeyOnly && opmode == 1) {
                    throw new InvalidKeyException("mode 1 requires RSAPrivateKey");
                }
                param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)key);
            } else if (key instanceof RSAPrivateKey) {
                if (this.publicKeyOnly && opmode == 1) {
                    throw new InvalidKeyException("mode 2 requires RSAPublicKey");
                }
                param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)key);
            } else {
                throw new InvalidKeyException("unknown key type passed to RSA");
            }
            if (params != null) {
                OAEPParameterSpec spec = (OAEPParameterSpec)params;
                this.paramSpec = params;
                if (!spec.getMGFAlgorithm().equalsIgnoreCase("MGF1") && !spec.getMGFAlgorithm().equals(PKCSObjectIdentifiers.id_mgf1.getId())) {
                    throw new InvalidAlgorithmParameterException("unknown mask generation function specified");
                }
                if (!(spec.getMGFParameters() instanceof MGF1ParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("unkown MGF parameters");
                }
                Digest digest = DigestFactory.getDigest(spec.getDigestAlgorithm());
                if (digest == null) {
                    throw new InvalidAlgorithmParameterException("no match on digest algorithm: " + spec.getDigestAlgorithm());
                }
                MGF1ParameterSpec mgfParams = (MGF1ParameterSpec)spec.getMGFParameters();
                Digest mgfDigest = DigestFactory.getDigest(mgfParams.getDigestAlgorithm());
                if (mgfDigest == null) {
                    throw new InvalidAlgorithmParameterException("no match on MGF digest algorithm: " + mgfParams.getDigestAlgorithm());
                }
                this.cipher = new OAEPEncoding(new RSABlindedEngine(), digest, mgfDigest, ((PSource.PSpecified)spec.getPSource()).getValue());
            }
        } else {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + params.getClass().getName());
        }
        if (!(this.cipher instanceof RSABlindedEngine)) {
            param = random != null ? new ParametersWithRandom(param, random) : new ParametersWithRandom(param, CryptoServicesRegistrar.getSecureRandom());
        }
        this.bOut.reset();
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
                throw new InvalidParameterException("unknown opmode " + opmode + " passed to RSA");
            }
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        OAEPParameterSpec paramSpec = null;
        if (params != null) {
            try {
                paramSpec = params.getParameterSpec(OAEPParameterSpec.class);
            }
            catch (InvalidParameterSpecException e) {
                throw new InvalidAlgorithmParameterException("cannot recognise parameters: " + e.toString(), e);
            }
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
            throw new InvalidKeyException("Eeeek! " + e.toString(), e);
        }
    }

    @Override
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        this.bOut.write(input, inputOffset, inputLen);
        if (this.cipher instanceof RSABlindedEngine ? this.bOut.size() > this.cipher.getInputBlockSize() + 1 : this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return null;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) {
        this.bOut.write(input, inputOffset, inputLen);
        if (this.cipher instanceof RSABlindedEngine ? this.bOut.size() > this.cipher.getInputBlockSize() + 1 : this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return 0;
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        if (input != null) {
            this.bOut.write(input, inputOffset, inputLen);
        }
        if (this.cipher instanceof RSABlindedEngine ? this.bOut.size() > this.cipher.getInputBlockSize() + 1 : this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        return this.getOutput();
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException {
        if (outputOffset + this.engineGetOutputSize(inputLen) > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (input != null) {
            this.bOut.write(input, inputOffset, inputLen);
        }
        if (this.cipher instanceof RSABlindedEngine ? this.bOut.size() > this.cipher.getInputBlockSize() + 1 : this.bOut.size() > this.cipher.getInputBlockSize()) {
            throw new ArrayIndexOutOfBoundsException("too much data for RSA block");
        }
        byte[] out = this.getOutput();
        for (int i = 0; i != out.length; ++i) {
            output[outputOffset + i] = out[i];
        }
        return out.length;
    }

    private byte[] getOutput() throws BadPaddingException {
        try {
            byte[] byArray = this.cipher.processBlock(this.bOut.getBuf(), 0, this.bOut.size());
            return byArray;
        }
        catch (InvalidCipherTextException e) {
            throw new BadBlockException("unable to decrypt block", e);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new BadBlockException("unable to decrypt block", e);
        }
        finally {
            this.bOut.erase();
        }
    }

    public static class ISO9796d1Padding
    extends CipherSpi {
        public ISO9796d1Padding() {
            super(new ISO9796d1Encoding(new RSABlindedEngine()));
        }
    }

    public static class NoPadding
    extends CipherSpi {
        public NoPadding() {
            super(new RSABlindedEngine());
        }
    }

    public static class OAEPPadding
    extends CipherSpi {
        public OAEPPadding() {
            super(OAEPParameterSpec.DEFAULT);
        }
    }

    public static class PKCS1v1_5Padding
    extends CipherSpi {
        public PKCS1v1_5Padding() {
            super(new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    public static class PKCS1v1_5Padding_PrivateOnly
    extends CipherSpi {
        public PKCS1v1_5Padding_PrivateOnly() {
            super(false, true, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }

    public static class PKCS1v1_5Padding_PublicOnly
    extends CipherSpi {
        public PKCS1v1_5Padding_PublicOnly() {
            super(true, false, new PKCS1Encoding(new RSABlindedEngine()));
        }
    }
}

