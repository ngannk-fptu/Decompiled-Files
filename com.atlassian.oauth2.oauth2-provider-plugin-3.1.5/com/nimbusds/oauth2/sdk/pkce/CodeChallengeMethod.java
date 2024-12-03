/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.pkce;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class CodeChallengeMethod
extends Identifier {
    private static final long serialVersionUID = -8125202768444965372L;
    public static final CodeChallengeMethod PLAIN = new CodeChallengeMethod("plain");
    public static final CodeChallengeMethod S256 = new CodeChallengeMethod("S256");

    public static CodeChallengeMethod getDefault() {
        return PLAIN;
    }

    public CodeChallengeMethod(String value) {
        super(value);
    }

    public static CodeChallengeMethod parse(String value) {
        if (value.equals(PLAIN.getValue())) {
            return PLAIN;
        }
        if (value.equals(S256.getValue())) {
            return S256;
        }
        return new CodeChallengeMethod(value);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof CodeChallengeMethod && this.toString().equals(object.toString());
    }
}

