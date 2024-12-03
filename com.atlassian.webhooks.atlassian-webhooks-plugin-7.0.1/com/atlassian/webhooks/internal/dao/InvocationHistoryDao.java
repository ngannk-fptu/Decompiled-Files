/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.DetailedInvocation
 *  com.atlassian.webhooks.history.InvocationCounts
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.webhooks.history.DetailedInvocation;
import com.atlassian.webhooks.history.InvocationCounts;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.internal.dao.ao.AoHistoricalInvocation;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface InvocationHistoryDao {
    public void addCounts(int var1, String var2, Date var3, int var4, int var5, int var6);

    @Nonnull
    public Map<String, String> decodeHeaders(String var1, String var2);

    public int deleteDailyCountsOlderThan(int var1);

    public void deleteForWebhook(int var1);

    @Nonnull
    public InvocationCounts getCounts(int var1, @Nullable String var2, int var3);

    @Nonnull
    public Map<String, InvocationCounts> getCountsByEvent(int var1, @Nonnull Collection<String> var2, int var3);

    @Nonnull
    public Map<Integer, InvocationCounts> getCountsByWebhook(@Nonnull Collection<Integer> var1, int var2);

    @Nullable
    public AoHistoricalInvocation getLatestInvocation(int var1, @Nullable String var2, @Nullable Collection<InvocationOutcome> var3);

    @Nonnull
    public List<AoHistoricalInvocation> getLatestInvocations(int var1, @Nullable String var2, @Nullable Collection<InvocationOutcome> var3);

    @Nonnull
    public Multimap<String, AoHistoricalInvocation> getLatestInvocationsByEvent(int var1, @Nonnull Collection<String> var2);

    @Nonnull
    public Multimap<Integer, AoHistoricalInvocation> getLatestInvocationsByWebhook(@Nonnull Collection<Integer> var1);

    public void saveInvocation(int var1, @Nonnull DetailedInvocation var2);
}

