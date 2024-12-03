/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.token;

import com.atlassian.crowd.model.token.ExpirableUserTokenType;
import javax.annotation.Nullable;

public interface ExpirableUserToken {
    public String getToken();

    @Nullable
    public String getUsername();

    @Nullable
    public String getEmailAddress();

    public long getExpiryDate();

    public long getDirectoryId();

    public ExpirableUserTokenType getType();
}

