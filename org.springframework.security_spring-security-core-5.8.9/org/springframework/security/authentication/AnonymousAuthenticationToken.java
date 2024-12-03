/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.authentication;

import java.io.Serializable;
import java.util.Collection;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

public class AnonymousAuthenticationToken
extends AbstractAuthenticationToken
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Object principal;
    private final int keyHash;

    public AnonymousAuthenticationToken(String key, Object principal, Collection<? extends GrantedAuthority> authorities) {
        this(AnonymousAuthenticationToken.extractKeyHash(key), principal, authorities);
    }

    private AnonymousAuthenticationToken(Integer keyHash, Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.isTrue((principal != null && !"".equals(principal) ? 1 : 0) != 0, (String)"principal cannot be null or empty");
        Assert.notEmpty(authorities, (String)"authorities cannot be null or empty");
        this.keyHash = keyHash;
        this.principal = principal;
        this.setAuthenticated(true);
    }

    private static Integer extractKeyHash(String key) {
        Assert.hasLength((String)key, (String)"key cannot be empty or null");
        return key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (obj instanceof AnonymousAuthenticationToken) {
            AnonymousAuthenticationToken test = (AnonymousAuthenticationToken)obj;
            return this.getKeyHash() == test.getKeyHash();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.keyHash;
        return result;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    public int getKeyHash() {
        return this.keyHash;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}

