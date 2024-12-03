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
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class NTUserPrincipal
implements Principal,
Serializable {
    private static final long serialVersionUID = -6870169797924406894L;
    private final String username;
    private final String domain;
    private final String ntname;

    public NTUserPrincipal(String domain, String username) {
        Args.notNull((Object)username, (String)"User name");
        this.username = username;
        this.domain = domain != null ? domain.toUpperCase(Locale.ROOT) : null;
        if (this.domain != null && !this.domain.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.domain);
            buffer.append('\\');
            buffer.append(this.username);
            this.ntname = buffer.toString();
        } else {
            this.ntname = this.username;
        }
    }

    @Override
    public String getName() {
        return this.ntname;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode((int)hash, (Object)this.username);
        hash = LangUtils.hashCode((int)hash, (Object)this.domain);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof NTUserPrincipal) {
            NTUserPrincipal that = (NTUserPrincipal)o;
            if (LangUtils.equals((Object)this.username, (Object)that.username) && LangUtils.equals((Object)this.domain, (Object)that.domain)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.ntname;
    }
}

