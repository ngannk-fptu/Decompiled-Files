/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class SerialNumber
extends Identifier {
    private static final long serialVersionUID = -2649164370990279544L;

    public SerialNumber(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SerialNumber && this.toString().equals(object.toString());
    }
}

