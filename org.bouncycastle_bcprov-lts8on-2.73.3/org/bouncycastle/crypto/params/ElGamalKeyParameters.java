/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ElGamalParameters;

public class ElGamalKeyParameters
extends AsymmetricKeyParameter {
    private ElGamalParameters params;

    protected ElGamalKeyParameters(boolean isPrivate, ElGamalParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public ElGamalParameters getParameters() {
        return this.params;
    }

    public int hashCode() {
        return this.params != null ? this.params.hashCode() : 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ElGamalKeyParameters)) {
            return false;
        }
        ElGamalKeyParameters dhKey = (ElGamalKeyParameters)obj;
        if (this.params == null) {
            return dhKey.getParameters() == null;
        }
        return this.params.equals(dhKey.getParameters());
    }
}

