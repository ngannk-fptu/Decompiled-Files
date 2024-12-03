/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.rememberme.CrowdRememberMeToken
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.dao.rememberme;

import com.atlassian.crowd.model.rememberme.CrowdRememberMeToken;
import com.atlassian.crowd.model.rememberme.InternalCrowdRememberMeToken;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface CrowdRememberMeTokenDAO {
    public Optional<InternalCrowdRememberMeToken> findByIdExclusiveLock(Long var1);

    public List<InternalCrowdRememberMeToken> findTokensForUser(String var1, long var2);

    public InternalCrowdRememberMeToken save(InternalCrowdRememberMeToken var1);

    public int removeAllExpiredTokens(LocalDateTime var1, LocalDateTime var2);

    public int removeTokensForSeries(String var1);

    public Optional<InternalCrowdRememberMeToken> findBySeriesAndToken(@Nonnull String var1, @Nonnull String var2);

    public int removeTokensForUserInDirectory(String var1, long var2);

    public int removeTokensForDirectory(long var1);

    public boolean setUsedByRemoteAddress(CrowdRememberMeToken var1);

    public void removeAll();

    public void refresh(InternalCrowdRememberMeToken var1);
}

