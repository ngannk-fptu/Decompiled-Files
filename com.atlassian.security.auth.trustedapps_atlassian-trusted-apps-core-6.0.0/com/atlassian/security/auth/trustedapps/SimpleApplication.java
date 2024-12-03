/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import java.security.PublicKey;

public class SimpleApplication
implements Application {
    private final String id;
    private final PublicKey publicKey;

    public SimpleApplication(String id, PublicKey publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}

