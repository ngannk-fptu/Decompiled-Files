/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class DocumentNumber
extends Identifier {
    private static final long serialVersionUID = -2649164370990279544L;

    public DocumentNumber(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof DocumentNumber && this.toString().equals(object.toString());
    }
}

