/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.id;

import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class State
extends Identifier {
    public State(String value) {
        super(value);
    }

    public State(int byteLength) {
        super(byteLength);
    }

    public State() {
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof State && this.toString().equals(object.toString());
    }

    public static State parse(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return new State(s);
    }
}

