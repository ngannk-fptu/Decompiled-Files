/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Status
extends Identifier {
    private static final long serialVersionUID = 3002588396846440098L;

    public Status(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Status && this.toString().equals(object.toString());
    }
}

