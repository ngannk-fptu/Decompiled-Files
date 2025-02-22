/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class UserKeyingMaterialSpec
implements AlgorithmParameterSpec {
    private final byte[] userKeyingMaterial;
    private final byte[] salt;

    public UserKeyingMaterialSpec(byte[] userKeyingMaterial) {
        this(userKeyingMaterial, null);
    }

    public UserKeyingMaterialSpec(byte[] userKeyingMaterial, byte[] salt) {
        this.userKeyingMaterial = Arrays.clone(userKeyingMaterial);
        this.salt = Arrays.clone(salt);
    }

    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
}

