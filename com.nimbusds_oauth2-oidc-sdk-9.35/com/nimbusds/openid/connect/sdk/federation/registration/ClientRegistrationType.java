/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.registration;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientRegistrationType
extends Identifier {
    private static final long serialVersionUID = 1L;
    public static final ClientRegistrationType AUTOMATIC = new ClientRegistrationType("automatic");
    public static final ClientRegistrationType EXPLICIT = new ClientRegistrationType("explicit");

    public ClientRegistrationType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ClientRegistrationType && this.toString().equals(object.toString());
    }
}

