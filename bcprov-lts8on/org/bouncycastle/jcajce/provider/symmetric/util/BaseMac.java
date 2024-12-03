/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.MacSpi;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC2Parameters;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.jcajce.provider.symmetric.util.BCPBEKey;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.GcmSpecUtil;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;
import org.bouncycastle.jcajce.spec.SkeinParameterSpec;

public class BaseMac
extends MacSpi
implements PBE {
    private static final Class gcmSpecClass = ClassUtil.loadClass(BaseMac.class, "javax.crypto.spec.GCMParameterSpec");
    private Mac macEngine;
    private int scheme = 2;
    private int pbeHash = 1;
    private int keySize = 160;

    protected BaseMac(Mac macEngine) {
        this.macEngine = macEngine;
    }

    protected BaseMac(Mac macEngine, int scheme, int pbeHash, int keySize) {
        this.macEngine = macEngine;
        this.scheme = scheme;
        this.pbeHash = pbeHash;
        this.keySize = keySize;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void engineInit(Key key, AlgorithmParameterSpec params) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters param;
        SecretKey k;
        if (key == null) {
            throw new InvalidKeyException("key is null");
        }
        if (key instanceof PKCS12Key) {
            PBEParameterSpec pbeSpec;
            try {
                k = (SecretKey)key;
            }
            catch (Exception e) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
            try {
                pbeSpec = (PBEParameterSpec)params;
            }
            catch (Exception e) {
                throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
            }
            if (k instanceof PBEKey && pbeSpec == null) {
                pbeSpec = new PBEParameterSpec(((PBEKey)k).getSalt(), ((PBEKey)k).getIterationCount());
            }
            int digest = 1;
            int keySize = 160;
            if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
                digest = 6;
                keySize = 256;
            } else if (this.macEngine instanceof HMac && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
                if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
                    digest = 7;
                    keySize = 224;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                    digest = 4;
                    keySize = 256;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
                    digest = 8;
                    keySize = 384;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
                    digest = 9;
                    keySize = 512;
                } else {
                    if (!this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                    digest = 2;
                    keySize = 160;
                }
            }
            param = PBE.Util.makePBEMacParameters(k, 2, digest, keySize, pbeSpec);
        } else if (key instanceof BCPBEKey) {
            k = (BCPBEKey)key;
            if (((BCPBEKey)k).getParam() != null) {
                param = ((BCPBEKey)k).getParam();
            } else {
                if (!(params instanceof PBEParameterSpec)) throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                param = PBE.Util.makePBEMacParameters((BCPBEKey)k, params);
            }
        } else {
            if (params instanceof PBEParameterSpec) {
                throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + params.getClass().getName());
            }
            param = new KeyParameter(key.getEncoded());
        }
        KeyParameter keyParam = param instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)param).getParameters() : (KeyParameter)param;
        if (params instanceof AEADParameterSpec) {
            AEADParameterSpec aeadSpec = (AEADParameterSpec)params;
            param = new AEADParameters(keyParam, aeadSpec.getMacSizeInBits(), aeadSpec.getNonce(), aeadSpec.getAssociatedData());
        } else if (params instanceof IvParameterSpec) {
            param = new ParametersWithIV(keyParam, ((IvParameterSpec)params).getIV());
        } else if (params instanceof RC2ParameterSpec) {
            param = new ParametersWithIV(new RC2Parameters(keyParam.getKey(), ((RC2ParameterSpec)params).getEffectiveKeyBits()), ((RC2ParameterSpec)params).getIV());
        } else if (params instanceof SkeinParameterSpec) {
            param = new SkeinParameters.Builder(BaseMac.copyMap(((SkeinParameterSpec)params).getParameters())).setKey(keyParam.getKey()).build();
        } else if (params == null) {
            param = new KeyParameter(key.getEncoded());
        } else if (gcmSpecClass != null && gcmSpecClass.isAssignableFrom(params.getClass())) {
            param = GcmSpecUtil.extractAeadParameters(keyParam, params);
        } else if (!(params instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + params.getClass().getName());
        }
        try {
            this.macEngine.init(param);
            return;
        }
        catch (Exception e) {
            throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + e.getMessage());
        }
    }

    @Override
    protected int engineGetMacLength() {
        return this.macEngine.getMacSize();
    }

    @Override
    protected void engineReset() {
        this.macEngine.reset();
    }

    @Override
    protected void engineUpdate(byte input) {
        this.macEngine.update(input);
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len) {
        this.macEngine.update(input, offset, len);
    }

    @Override
    protected byte[] engineDoFinal() {
        byte[] out = new byte[this.engineGetMacLength()];
        this.macEngine.doFinal(out, 0);
        return out;
    }

    private static Hashtable copyMap(Map paramsMap) {
        Hashtable newTable = new Hashtable();
        for (Object key : paramsMap.keySet()) {
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }
}

