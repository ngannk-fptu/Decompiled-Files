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

    protected BaseMac(Mac mac) {
        this.macEngine = mac;
    }

    protected BaseMac(Mac mac, int n, int n2, int n3) {
        this.macEngine = mac;
        this.scheme = n;
        this.pbeHash = n2;
        this.keySize = n3;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void engineInit(Key key, AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        CipherParameters cipherParameters;
        AlgorithmParameterSpec algorithmParameterSpec2;
        Object object;
        if (key == null) {
            throw new InvalidKeyException("key is null");
        }
        if (key instanceof PKCS12Key) {
            try {
                object = (SecretKey)key;
            }
            catch (Exception exception) {
                throw new InvalidKeyException("PKCS12 requires a SecretKey/PBEKey");
            }
            try {
                algorithmParameterSpec2 = (PBEParameterSpec)algorithmParameterSpec;
            }
            catch (Exception exception) {
                throw new InvalidAlgorithmParameterException("PKCS12 requires a PBEParameterSpec");
            }
            if (object instanceof PBEKey && algorithmParameterSpec2 == null) {
                algorithmParameterSpec2 = new PBEParameterSpec(((PBEKey)object).getSalt(), ((PBEKey)object).getIterationCount());
            }
            int n = 1;
            int n2 = 160;
            if (this.macEngine.getAlgorithmName().startsWith("GOST")) {
                n = 6;
                n2 = 256;
            } else if (this.macEngine instanceof HMac && !this.macEngine.getAlgorithmName().startsWith("SHA-1")) {
                if (this.macEngine.getAlgorithmName().startsWith("SHA-224")) {
                    n = 7;
                    n2 = 224;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-256")) {
                    n = 4;
                    n2 = 256;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-384")) {
                    n = 8;
                    n2 = 384;
                } else if (this.macEngine.getAlgorithmName().startsWith("SHA-512")) {
                    n = 9;
                    n2 = 512;
                } else {
                    if (!this.macEngine.getAlgorithmName().startsWith("RIPEMD160")) throw new InvalidAlgorithmParameterException("no PKCS12 mapping for HMAC: " + this.macEngine.getAlgorithmName());
                    n = 2;
                    n2 = 160;
                }
            }
            cipherParameters = PBE.Util.makePBEMacParameters((SecretKey)object, 2, n, n2, (PBEParameterSpec)algorithmParameterSpec2);
        } else if (key instanceof BCPBEKey) {
            object = (BCPBEKey)key;
            if (((BCPBEKey)object).getParam() != null) {
                cipherParameters = ((BCPBEKey)object).getParam();
            } else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) throw new InvalidAlgorithmParameterException("PBE requires PBE parameters to be set.");
                cipherParameters = PBE.Util.makePBEMacParameters((BCPBEKey)object, algorithmParameterSpec);
            }
        } else {
            if (algorithmParameterSpec instanceof PBEParameterSpec) {
                throw new InvalidAlgorithmParameterException("inappropriate parameter type: " + algorithmParameterSpec.getClass().getName());
            }
            cipherParameters = new KeyParameter(key.getEncoded());
        }
        object = cipherParameters instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)cipherParameters).getParameters() : (KeyParameter)cipherParameters;
        if (algorithmParameterSpec instanceof AEADParameterSpec) {
            algorithmParameterSpec2 = (AEADParameterSpec)algorithmParameterSpec;
            cipherParameters = new AEADParameters((KeyParameter)object, ((AEADParameterSpec)algorithmParameterSpec2).getMacSizeInBits(), ((AEADParameterSpec)algorithmParameterSpec2).getNonce(), ((AEADParameterSpec)algorithmParameterSpec2).getAssociatedData());
        } else if (algorithmParameterSpec instanceof IvParameterSpec) {
            cipherParameters = new ParametersWithIV((CipherParameters)object, ((IvParameterSpec)algorithmParameterSpec).getIV());
        } else if (algorithmParameterSpec instanceof RC2ParameterSpec) {
            cipherParameters = new ParametersWithIV(new RC2Parameters(((KeyParameter)object).getKey(), ((RC2ParameterSpec)algorithmParameterSpec).getEffectiveKeyBits()), ((RC2ParameterSpec)algorithmParameterSpec).getIV());
        } else if (algorithmParameterSpec instanceof SkeinParameterSpec) {
            cipherParameters = new SkeinParameters.Builder(BaseMac.copyMap(((SkeinParameterSpec)algorithmParameterSpec).getParameters())).setKey(((KeyParameter)object).getKey()).build();
        } else if (algorithmParameterSpec == null) {
            cipherParameters = new KeyParameter(key.getEncoded());
        } else if (gcmSpecClass != null && gcmSpecClass.isAssignableFrom(algorithmParameterSpec.getClass())) {
            cipherParameters = GcmSpecUtil.extractAeadParameters((KeyParameter)object, algorithmParameterSpec);
        } else if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
            throw new InvalidAlgorithmParameterException("unknown parameter type: " + algorithmParameterSpec.getClass().getName());
        }
        try {
            this.macEngine.init(cipherParameters);
            return;
        }
        catch (Exception exception) {
            throw new InvalidAlgorithmParameterException("cannot initialize MAC: " + exception.getMessage());
        }
    }

    protected int engineGetMacLength() {
        return this.macEngine.getMacSize();
    }

    protected void engineReset() {
        this.macEngine.reset();
    }

    protected void engineUpdate(byte by) {
        this.macEngine.update(by);
    }

    protected void engineUpdate(byte[] byArray, int n, int n2) {
        this.macEngine.update(byArray, n, n2);
    }

    protected byte[] engineDoFinal() {
        byte[] byArray = new byte[this.engineGetMacLength()];
        this.macEngine.doFinal(byArray, 0);
        return byArray;
    }

    private static Hashtable copyMap(Map map) {
        Hashtable hashtable = new Hashtable();
        for (Object k : map.keySet()) {
            hashtable.put(k, map.get(k));
        }
        return hashtable;
    }
}

