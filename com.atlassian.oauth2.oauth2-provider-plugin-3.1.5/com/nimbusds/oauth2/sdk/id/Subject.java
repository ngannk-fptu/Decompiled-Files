/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class Subject
extends Identifier {
    private static final long serialVersionUID = 4305952346483638353L;

    public Subject(String value) {
        super(value);
    }

    public Subject(int byteLength) {
        super(byteLength);
    }

    public Subject() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Subject && this.toString().equals(object.toString());
    }
}

