/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class OperationType
extends Identifier {
    public static final OperationType FETCH = new OperationType("fetch");
    public static final OperationType RESOLVE_METADATA = new OperationType("resolve_metadata");
    public static final OperationType LISTING = new OperationType("listing");

    public OperationType(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof OperationType && this.toString().equals(object.toString());
    }
}

