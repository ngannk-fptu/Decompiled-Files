/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class VerificationProcess
extends Identifier {
    private static final long serialVersionUID = -6981905244217816233L;

    public VerificationProcess(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof VerificationProcess && this.toString().equals(object.toString());
    }
}

