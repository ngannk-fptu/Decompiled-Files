/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.pats.api;

import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.rest.RestTokenSearchRequest;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserProfile;
import com.querydsl.core.types.Predicate;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.data.domain.Page;

public interface TokenService {
    @Nonnull
    public TokenDTO create(@Nonnull UserKey var1, @Nonnull String var2, @Nullable Integer var3);

    public int delete(@Nullable UserKey var1, @Nonnull Predicate var2);

    public Page<TokenDTO> search(@Nonnull RestTokenSearchRequest var1);

    public List<UserProfile> searchForUsers(@Nullable String var1, int var2);
}

