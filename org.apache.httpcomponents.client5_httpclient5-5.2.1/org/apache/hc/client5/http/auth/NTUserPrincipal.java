/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.LangUtils
 */
package org.apache.hc.client5.http.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import java.util.Objects;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

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
            return Objects.equals(this.username, that.username) && Objects.equals(this.domain, that.domain);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.ntname;
    }
}

