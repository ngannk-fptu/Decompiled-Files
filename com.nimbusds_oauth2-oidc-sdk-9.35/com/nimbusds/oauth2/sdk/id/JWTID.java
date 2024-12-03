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
public final class JWTID
extends Identifier {
    private static final long serialVersionUID = 6958512198352608856L;

    public JWTID(String value) {
        super(value);
    }

    public JWTID(int byteLength) {
        super(byteLength);
    }

    public JWTID() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof JWTID && this.toString().equals(object.toString());
    }
}

