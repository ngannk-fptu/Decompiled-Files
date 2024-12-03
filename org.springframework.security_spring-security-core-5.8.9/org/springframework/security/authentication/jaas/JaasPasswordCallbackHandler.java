/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.PasswordCallback;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import org.springframework.security.core.Authentication;

public class JaasPasswordCallbackHandler
implements JaasAuthenticationCallbackHandler {
    @Override
    public void handle(Callback callback, Authentication auth) {
        if (callback instanceof PasswordCallback) {
            ((PasswordCallback)callback).setPassword(auth.getCredentials().toString().toCharArray());
        }
    }
}

