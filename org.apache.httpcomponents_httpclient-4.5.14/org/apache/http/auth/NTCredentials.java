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
import java.util.Locale;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTUserPrincipal;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class NTCredentials
implements Credentials,
Serializable {
    private static final long serialVersionUID = -7385699315228907265L;
    private final NTUserPrincipal principal;
    private final String password;
    private final String workstation;

    @Deprecated
    public NTCredentials(String usernamePassword) {
        String username;
        Args.notNull((Object)usernamePassword, (String)"Username:password string");
        int atColon = usernamePassword.indexOf(58);
        if (atColon >= 0) {
            username = usernamePassword.substring(0, atColon);
            this.password = usernamePassword.substring(atColon + 1);
        } else {
            username = usernamePassword;
            this.password = null;
        }
        int atSlash = username.indexOf(47);
        this.principal = atSlash >= 0 ? new NTUserPrincipal(username.substring(0, atSlash).toUpperCase(Locale.ROOT), username.substring(atSlash + 1)) : new NTUserPrincipal(null, username.substring(atSlash + 1));
        this.workstation = null;
    }

    public NTCredentials(String userName, String password, String workstation, String domain) {
        Args.notNull((Object)userName, (String)"User name");
        this.principal = new NTUserPrincipal(domain, userName);
        this.password = password;
        this.workstation = workstation != null ? workstation.toUpperCase(Locale.ROOT) : null;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    public String getUserName() {
        return this.principal.getUsername();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getDomain() {
        return this.principal.getDomain();
    }

    public String getWorkstation() {
        return this.workstation;
    }

    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode((int)hash, (Object)this.principal);
        hash = LangUtils.hashCode((int)hash, (Object)this.workstation);
        return hash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NTCredentials) {
            NTCredentials that = (NTCredentials)o;
            if (LangUtils.equals((Object)this.principal, (Object)that.principal) && LangUtils.equals((Object)this.workstation, (Object)that.workstation)) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.principal);
        buffer.append("][workstation: ");
        buffer.append(this.workstation);
        buffer.append("]");
        return buffer.toString();
    }
}

