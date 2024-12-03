/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.assessment.BrowserMetricsDto;
import com.atlassian.migration.agent.entity.BrowserMetrics;
import java.util.List;
import java.util.Optional;

public interface GuardrailsBrowserMetricsStore {
    public BrowserMetrics createBrowserMetrics(ConfluenceUser var1, BrowserMetricsDto var2);

    public Optional<BrowserMetrics> findMostRecent(ConfluenceUser var1);

    public List<BrowserMetrics> findAll();

    public List<BrowserMetrics> getPage(int var1, int var2);

    public long getCount();

    public void deleteStaleMetrics();
}

