/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.security.spec.KeySpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

public class ECKeySpec
implements KeySpec {
    private ECParameterSpec spec;

    protected ECKeySpec(ECParameterSpec spec) {
        this.spec = spec;
    }

    public ECParameterSpec getParams() {
        return this.spec;
    }
}

