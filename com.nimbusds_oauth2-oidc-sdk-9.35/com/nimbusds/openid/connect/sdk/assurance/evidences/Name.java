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
public final class Name
extends Identifier {
    private static final long serialVersionUID = 5685882782032397432L;

    public Name(String value) {
        super(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Name && this.toString().equals(object.toString());
    }
}

