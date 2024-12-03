/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class OperationName
extends Identifier {
    private static final long serialVersionUID = 1496506482155366452L;

    public OperationName(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof OperationName && this.toString().equals(object.toString());
    }
}

