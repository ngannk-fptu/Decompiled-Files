/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientID
extends Identifier {
    private static final long serialVersionUID = 8098426263125084877L;

    public ClientID(String value) {
        super(value);
    }

    public ClientID(Identifier value) {
        super(value.getValue());
    }

    public ClientID(int byteLength) {
        super(byteLength);
    }

    public ClientID() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ClientID && this.toString().equals(object.toString());
    }
}

