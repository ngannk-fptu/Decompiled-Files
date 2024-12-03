/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import org.springframework.ldap.core.AuthenticationErrorCallback;

public final class CollectingAuthenticationErrorCallback
implements AuthenticationErrorCallback {
    private Exception error;

    @Override
    public void execute(Exception e) {
        this.error = e;
    }

    public Exception getError() {
        return this.error;
    }

    public boolean hasError() {
        return this.error != null;
    }
}

