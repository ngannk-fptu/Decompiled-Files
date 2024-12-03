/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import org.springframework.security.authentication.jaas.JaasAuthenticationCallbackHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public class JaasNameCallbackHandler
implements JaasAuthenticationCallbackHandler {
    @Override
    public void handle(Callback callback, Authentication authentication) {
        if (callback instanceof NameCallback) {
            ((NameCallback)callback).setName(this.getUserName(authentication));
        }
    }

    private String getUserName(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        }
        return principal.toString();
    }
}

