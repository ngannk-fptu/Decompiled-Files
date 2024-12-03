/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.authenticator;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class JAASCallbackHandler
implements CallbackHandler {
    private String password;
    private String username;

    public JAASCallbackHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; ++i) {
            Callback callback = callbacks[i];
            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback)callback;
                nc.setName(this.username);
                continue;
            }
            if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback)callback;
                pc.setPassword(this.password.toCharArray());
                continue;
            }
            throw new UnsupportedCallbackException(callback, "Unrecognised Callback");
        }
    }
}

