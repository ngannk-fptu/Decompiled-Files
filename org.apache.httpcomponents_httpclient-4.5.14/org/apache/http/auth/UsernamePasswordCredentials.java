/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.util.Args
 *  org.apache.http.util.LangUtils
 */
package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.http.auth.Credentials;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class UsernamePasswordCredentials
implements Credentials,
Serializable {
    private static final long serialVersionUID = 243343858802739403L;
    private final BasicUserPrincipal principal;
    private final String password;

    @Deprecated
    public UsernamePasswordCredentials(String usernamePassword) {
        Args.notNull((Object)usernamePassword, (String)"Username:password string");
        int atColon = usernamePassword.indexOf(58);
        if (atColon >= 0) {
            this.principal = new BasicUserPrincipal(usernamePassword.substring(0, atColon));
            this.password = usernamePassword.substring(atColon + 1);
        } else {
            this.principal = new BasicUserPrincipal(usernamePassword);
            this.password = null;
        }
    }

    public UsernamePasswordCredentials(String userName, String password) {
        Args.notNull((Object)userName, (String)"Username");
        this.principal = new BasicUserPrincipal(userName);
        this.password = password;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    public String getUserName() {
        return this.principal.getName();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public int hashCode() {
        return this.principal.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UsernamePasswordCredentials) {
            UsernamePasswordCredentials that = (UsernamePasswordCredentials)o;
            if (LangUtils.equals((Object)this.principal, (Object)that.principal)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        return this.principal.toString();
    }
}

