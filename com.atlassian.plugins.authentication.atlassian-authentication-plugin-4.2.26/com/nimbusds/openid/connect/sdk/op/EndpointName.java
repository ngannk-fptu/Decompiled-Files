/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class EndpointName
extends Identifier {
    private static final long serialVersionUID = 1L;
    public static final EndpointName AR = new EndpointName("ar");
    public static final EndpointName PAR = new EndpointName("par");

    public EndpointName(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof EndpointName && this.toString().equals(object.toString());
    }
}

