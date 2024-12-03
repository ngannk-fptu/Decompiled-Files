/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.security.core.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.util.ObjectUtils;

public class SecurityContextImpl
implements SecurityContext {
    private static final long serialVersionUID = 580L;
    private Authentication authentication;

    public SecurityContextImpl() {
    }

    public SecurityContextImpl(Authentication authentication) {
        this.authentication = authentication;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SecurityContextImpl) {
            SecurityContextImpl other = (SecurityContextImpl)obj;
            if (this.getAuthentication() == null && other.getAuthentication() == null) {
                return true;
            }
            if (this.getAuthentication() != null && other.getAuthentication() != null && this.getAuthentication().equals(other.getAuthentication())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Authentication getAuthentication() {
        return this.authentication;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.authentication);
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append(" [");
        if (this.authentication == null) {
            sb.append("Null authentication");
        } else {
            sb.append("Authentication=").append(this.authentication);
        }
        sb.append("]");
        return sb.toString();
    }
}

