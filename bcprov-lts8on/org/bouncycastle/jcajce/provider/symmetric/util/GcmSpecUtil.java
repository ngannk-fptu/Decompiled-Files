/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AccessController;
import java.security.InvalidAlgorithmParameterException;
import java.security.PrivilegedExceptionAction;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.spec.GCMParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.internal.asn1.cms.GCMParameters;
import org.bouncycastle.util.Integers;

public class GcmSpecUtil {
    static final Class gcmSpecClass = GCMParameterSpec.class;

    public static boolean gcmSpecExists() {
        return true;
    }

    public static boolean gcmSpecExtractable() {
        return true;
    }

    public static boolean isGcmSpec(AlgorithmParameterSpec paramSpec) {
        return gcmSpecClass != null && gcmSpecClass.isInstance(paramSpec);
    }

    public static boolean isGcmSpec(Class paramSpecClass) {
        return gcmSpecClass == paramSpecClass;
    }

    public static AlgorithmParameterSpec extractGcmSpec(ASN1Primitive spec) throws InvalidParameterSpecException {
        try {
            GCMParameters gcmParams = GCMParameters.getInstance(spec);
            return new GCMParameterSpec(Integers.valueOf(gcmParams.getIcvLen() * 8), gcmParams.getNonce());
        }
        catch (Exception e) {
            throw new InvalidParameterSpecException("Construction failed: " + e.getMessage());
        }
    }

    static AEADParameters extractAeadParameters(final KeyParameter keyParam, final AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
        try {
            return (AEADParameters)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws Exception {
                    GCMParameterSpec spec = (GCMParameterSpec)params;
                    return new AEADParameters(keyParam, spec.getTLen(), spec.getIV());
                }
            });
        }
        catch (Exception e) {
            throw new InvalidAlgorithmParameterException("Cannot process GCMParameterSpec.");
        }
    }

    public static GCMParameters extractGcmParameters(final AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        try {
            return (GCMParameters)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws Exception {
                    GCMParameterSpec spec = (GCMParameterSpec)paramSpec;
                    return new GCMParameters(spec.getIV(), spec.getTLen() / 8);
                }
            });
        }
        catch (Exception e) {
            throw new InvalidParameterSpecException("Cannot process GCMParameterSpec");
        }
    }
}

