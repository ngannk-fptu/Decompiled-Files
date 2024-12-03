/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import java.util.UUID;
import net.jcip.annotations.Immutable;

@Immutable
public final class SoftwareID
extends Identifier {
    public SoftwareID(String value) {
        super(value);
    }

    public SoftwareID() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SoftwareID && this.toString().equals(object.toString());
    }
}

