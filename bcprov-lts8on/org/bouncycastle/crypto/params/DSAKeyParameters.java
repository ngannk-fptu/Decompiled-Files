/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;

public class DSAKeyParameters
extends AsymmetricKeyParameter {
    private DSAParameters params;

    public DSAKeyParameters(boolean isPrivate, DSAParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public DSAParameters getParameters() {
        return this.params;
    }
}

