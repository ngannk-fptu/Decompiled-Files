/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.provider.symmetric.util.SpecUtil;

public class BaseStreamCipher
extends BaseWrapCipher
implements PBE {
    private Class[] availableSpecs = new Class[]{RC2ParameterSpec.class, RC5ParameterSpec.class, IvParameterSpec.class, PBEParameterSpec.class};
    private StreamCipher cipher;
    private int keySizeInBits;
    private int digest;
    private ParametersWithIV ivParam;
    private int ivLength = 0;
    private PBEParameterSpec pbeSpec = null;
    private String pbeAlgorithm = null;

    protected BaseStreamCipher(StreamCipher engine, int ivLength) {
        this(engine, ivLength, -1, -1);
    }

    protected BaseStreamCipher(StreamCipher engine, int ivLength, int keySizeInBits) {
        this(engine, ivLength, keySizeInBits, -1);
    }

    protected BaseStreamCipher(StreamCipher engine, int ivLength, int keySizeInBits, int digest) {
        this.cipher = engine;
        this.ivLength = ivLength;
        this.keySizeInBits = keySizeInBits;
        this.digest = digest;
    }

    @Override
    protected int engineGetBlockSize() {
        return 0;
    }

    @Override
    protected byte[] engineGetIV() {
        return this.ivParam != null ? this.ivParam.getIV() : null;
    }

    @Override
    protected int engineGetKeySize(Key key) {
        return key.getEncoded().length * 8;
    }

    @Override
    protected int engineGetOutputSize(int inputLen) {
        return inputLen;
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        if (this.engineParams == null) {
            if (this.pbeSpec != null) {
                try {
                    AlgorithmParameters engineParams = this.createParametersInstance(this.pbeAlgorithm);
                    engineParams.init(this.pbeSpec);
                    return engineParams;
                }
                catch (Exception e) {
                    return null;
                }
            }
            if (this.ivParam != null) {
                String name = this.cipher.getAlgorithmName();
                if (name.indexOf(47) >= 0) {
                    name = name.substring(0, name.indexOf(47));
                }
                if (name.startsWith("ChaCha7539")) {
                    name = "ChaCha7539";
                } else if (name.startsWith("Grain")) {
                    name = "Grainv1";
                } else if (name.startsWith("HC")) {
                    int endIndex = name.indexOf(45);
                    name = name.substring(0, endIndex) + name.substring(endIndex + 1);
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

    @Override
    protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
        if (!mode.equalsIgnoreCase("ECB") && !mode.equals("NONE")) {
            throw new NoSuchAlgorithmException("can't support mode " + mode);
        }
    }

    @Override
    protected void engineSetPadding(String padding) throws NoSuchPaddingException {
        if (!padding.equalsIgnoreCase("NoPadding")) {
            throw new NoSuchPaddingException("Padding " + padding + " unknown.");
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        SecretKey k;
        this.pbeSpec = null;
        this.pbeAlgorithm = null;
        this.engineParams = null;
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key for algorithm " + key.getAlgorithm() + " not suitable for symmetric enryption.");
        }
        if (key instanceof PKCS12Key) {
            k = (PKCS12Key)key;
            this.pbeSpec = (PBEParameterSpec)params;
            if (k instanceof PKCS12KeyWithParameters && this.pbeSpec == null) {
                this.pbeSpec = new PBEParameterSpec(((PKCS12KeyWithParameters)k).getSalt(), ((PKCS12KeyWithParameters)k).getIterationCount());
            }
            param = PBE.Util.makePBEParameters(((PKCS12Key)k).getEncoded(), 2, this.digest, this.keySizeInBits, this.ivLength * 8, this.pbeSpec, this.cipher.getAlgorithmName());
        } else if (key instanceof BCPBEKey) {
            k = (BCPBEKey)key;
            this.pbeAlgorithm = ((BCPBEKey)k).getOID() != null ? ((BCPBEKey)k).getOID().getId() : ((BCPBEKey)k).getAlgorithm();
            if (((BCPBEKey)k).getParam() != null) {
                param = ((BCPBEKey)k).getParam();
                this.pbeSpec = new PBEParameterSpec(((BCPBEKey)k).getSalt(), ((BCPBEKey)k).getIterationCount());
            } else if (params instanceof PBEParameterSpec) {
                param = PBE.Util.makePBEParameters((BCPBEKey)k, params, this.cipher.getAlgorithmName());
                this.pbeSpec = (PBEParameterSpec)params;
            } else {
                throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
            }
            if (((BCPBEKey)k).getIvSize() != 0) {
                this.ivParam = (ParametersWithIV)param;
            }
        } else if (params == null) {
            if (this.digest > 0) {
                throw new InvalidKeyException("Algorithm requires a PBE key");
            }
            param = new KeyParameter(key.getEncoded());
        } else if (params instanceof IvParameterSpec) {
            param = new ParametersWithIV(new KeyParameter(key.getEncoded()), ((IvParameterSpec)params).getIV());
            this.ivParam = (ParametersWithIV)param;
        } else {
            throw new InvalidAlgorithmParameterException("unknown parameter type.");
        }
        if (this.ivLength != 0 && !(param instanceof ParametersWithIV)) {
            SecureRandom ivRandom = random;
            if (ivRandom == null) {
                ivRandom = CryptoServicesRegistrar.getSecureRandom();
            }
            if (opmode == 1 || opmode == 3) {
                byte[] iv = new byte[this.ivLength];
                ivRandom.nextBytes(iv);
                param = new ParametersWithIV(param, iv);
                this.ivParam = (ParametersWithIV)param;
            } else {
                throw new InvalidAlgorithmParameterException("no IV set when one expected");
            }
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
        }
        catch (Exception e) {
            throw new InvalidKeyException(e.getMessage());
        }
    }

    @Override
    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec paramSpec = null;
        if (params != null && (paramSpec = SpecUtil.extractSpec(params, this.availableSpecs)) == null) {
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
    protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
        byte[] out = new byte[inputLen];
        this.cipher.processBytes(input, inputOffset, inputLen, out, 0);
        return out;
    }

    @Override
    protected int engineUpdate(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + inputLen > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        try {
            this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
            return inputLen;
        }
        catch (DataLengthException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen) {
        if (inputLen != 0) {
            byte[] out = this.engineUpdate(input, inputOffset, inputLen);
            this.cipher.reset();
            return out;
        }
        this.cipher.reset();
        return new byte[0];
    }

    @Override
    protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset) throws ShortBufferException {
        if (outputOffset + inputLen > output.length) {
            throw new ShortBufferException("output buffer too short for input.");
        }
        if (inputLen != 0) {
            this.cipher.processBytes(input, inputOffset, inputLen, output, outputOffset);
        }
        this.cipher.reset();
        return inputLen;
    }
}

