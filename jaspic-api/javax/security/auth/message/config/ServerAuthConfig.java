/*
 * Decompiled with CFR 0.152.
 */
package javax.security.auth.message.config;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.config.AuthConfig;
import javax.security.auth.message.config.ServerAuthContext;

public interface ServerAuthConfig
extends AuthConfig {
    public ServerAuthContext getAuthContext(String var1, Subject var2, Map var3) throws AuthException;
}

