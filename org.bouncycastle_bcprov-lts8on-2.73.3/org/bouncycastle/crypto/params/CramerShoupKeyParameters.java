/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.CramerShoupParameters;

public class CramerShoupKeyParameters
extends AsymmetricKeyParameter {
    private CramerShoupParameters params;

    protected CramerShoupKeyParameters(boolean isPrivate, CramerShoupParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public CramerShoupParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof CramerShoupKeyParameters)) {
            return false;
        }
        CramerShoupKeyParameters csKey = (CramerShoupKeyParameters)obj;
        if (this.params == null) {
            return csKey.getParameters() == null;
        }
        return this.params.equals(csKey.getParameters());
    }

    public int hashCode() {
        int code;
        int n = code = this.isPrivate() ? 0 : 1;
        if (this.params != null) {
            code ^= this.params.hashCode();
        }
        return code;
    }
}

