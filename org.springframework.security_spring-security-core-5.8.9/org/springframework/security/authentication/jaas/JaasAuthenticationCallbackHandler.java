/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authentication.jaas;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.UnsupportedCallbackException;
import org.springframework.security.core.Authentication;

public interface JaasAuthenticationCallbackHandler {
    public void handle(Callback var1, Authentication var2) throws IOException, UnsupportedCallbackException;
}

