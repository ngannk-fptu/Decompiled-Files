/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECKeyParameters
extends AsymmetricKeyParameter {
    private final ECDomainParameters parameters;

    protected ECKeyParameters(boolean isPrivate, ECDomainParameters parameters) {
        super(isPrivate);
        if (null == parameters) {
            throw new NullPointerException("'parameters' cannot be null");
        }
        this.parameters = parameters;
    }

    public ECDomainParameters getParameters() {
        return this.parameters;
    }
}

