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
import java.util.Objects;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.LangUtils;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public final class BasicUserPrincipal
implements Principal,
Serializable {
    private static final long serialVersionUID = -2266305184969850467L;
    private final String username;

    public BasicUserPrincipal(String username) {
        Args.notNull((Object)username, (String)"User name");
        this.username = username;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode((int)hash, (Object)this.username);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BasicUserPrincipal) {
            BasicUserPrincipal that = (BasicUserPrincipal)o;
            return Objects.equals(this.username, that.username);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("[principal: ");
        buffer.append(this.username);
        buffer.append("]");
        return buffer.toString();
    }
}

