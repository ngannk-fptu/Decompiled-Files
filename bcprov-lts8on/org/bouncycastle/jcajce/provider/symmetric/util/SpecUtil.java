/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.security.AlgorithmParameters;
import java.security.spec.AlgorithmParameterSpec;

class SpecUtil {
    SpecUtil() {
    }

    static AlgorithmParameterSpec extractSpec(AlgorithmParameters params, Class[] availableSpecs) {
        try {
            return params.getParameterSpec(AlgorithmParameterSpec.class);
        }
        catch (Exception e) {
            for (int i = 0; i != availableSpecs.length; ++i) {
                if (availableSpecs[i] == null) continue;
                try {
                    return params.getParameterSpec(availableSpecs[i]);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            return null;
        }
    }
}

