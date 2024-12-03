/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.authenticator;

import com.opensymphony.user.authenticator.AuthenticationException;
import com.opensymphony.user.authenticator.Authenticator;
import java.io.Serializable;
import java.util.Properties;

public abstract class AbstractAuthenticator
implements Authenticator,
Serializable {
    protected Properties properties;

    public boolean init(Properties properties) {
        this.properties = properties;
        return true;
    }

    public boolean login(String username, String password) throws AuthenticationException {
        return this.login(username, password, null);
    }
}

