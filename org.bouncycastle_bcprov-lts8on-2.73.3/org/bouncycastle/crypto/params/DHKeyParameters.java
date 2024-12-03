/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;

public class DHKeyParameters
extends AsymmetricKeyParameter {
    private DHParameters params;

    protected DHKeyParameters(boolean isPrivate, DHParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DHParameters getParameters() {
        return this.params;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DHKeyParameters)) {
            return false;
        }
        DHKeyParameters dhKey = (DHKeyParameters)obj;
        if (this.params == null) {
            return dhKey.getParameters() == null;
        }
        return this.params.equals(dhKey.getParameters());
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

