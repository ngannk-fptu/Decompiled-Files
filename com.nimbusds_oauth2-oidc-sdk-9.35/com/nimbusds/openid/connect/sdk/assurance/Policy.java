/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Policy
extends Identifier {
    private static final long serialVersionUID = 3002588396846440098L;

    public Policy(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Policy && this.toString().equals(object.toString());
    }
}

