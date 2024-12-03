/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.config;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.ClientAuthConfig;
import javax.security.auth.message.config.ServerAuthConfig;

public interface AuthConfigProvider {
    public ClientAuthConfig getClientAuthConfig(String var1, String var2, CallbackHandler var3) throws AuthException;

    public ServerAuthConfig getServerAuthConfig(String var1, String var2, CallbackHandler var3) throws AuthException;

    public void refresh();
}

