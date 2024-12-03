/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.JWSSignerOption;
import net.jcip.annotations.Immutable;

@Immutable
public final class UserAuthenticationRequired
implements JWSSignerOption {
    private static final UserAuthenticationRequired SINGLETON = new UserAuthenticationRequired();

    public static UserAuthenticationRequired getInstance() {
        return SINGLETON;
    }

    private UserAuthenticationRequired() {
    }

    public String toString() {
        return "UserAuthenticationRequired";
    }
}

