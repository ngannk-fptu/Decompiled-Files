/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public class DPoPIssuer
extends Identifier {
    private static final long serialVersionUID = 2801103134383988309L;

    public DPoPIssuer(String value) {
        super(value);
    }

    public DPoPIssuer(ClientID clientID) {
        super(clientID.getValue());
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DPoPIssuer && this.toString().equals(o.toString());
    }
}

