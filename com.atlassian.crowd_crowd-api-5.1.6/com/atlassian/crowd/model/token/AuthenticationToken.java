/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.model.token;

import com.atlassian.crowd.model.token.TokenLifetime;
import java.util.Date;

public interface AuthenticationToken {
    public static final long APPLICATION_TOKEN_DIRECTORY_ID = -1L;

    public Long getId();

    public String getRandomHash();

    public String getName();

    public String getUnaliasedUsername();

    public long getDirectoryId();

    public long getRandomNumber();

    default public boolean isUserToken() {
        return !this.isApplicationToken();
    }

    default public boolean isApplicationToken() {
        return this.getDirectoryId() == -1L;
    }

    public Date getCreatedDate();

    public long getLastAccessedTime();

    public TokenLifetime getLifetime();

    public String getIdentifierHash();
}

