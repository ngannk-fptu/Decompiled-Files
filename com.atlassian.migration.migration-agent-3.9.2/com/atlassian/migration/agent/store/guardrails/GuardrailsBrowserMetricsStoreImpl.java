/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.gson.Gson
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.assessment.BrowserMetricsDto;
import com.atlassian.migration.agent.entity.BrowserMetrics;
import com.atlassian.migration.agent.store.guardrails.GuardrailsBrowserMetricsStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.gson.Gson;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuardrailsBrowserMetricsStoreImpl
implements GuardrailsBrowserMetricsStore {
    private final EntityManagerTemplate tmpl;
    private final Logger log = LoggerFactory.getLogger(GuardrailsBrowserMetricsStoreImpl.class);
    private final long maxMetricsAge = TimeUnit.DAYS.toMillis(14L);
    private final long twentyFourHoursPeriod = TimeUnit.HOURS.toMillis(24L);
    private final Gson gson = new Gson();

    public GuardrailsBrowserMetricsStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public BrowserMetrics createBrowserMetrics(ConfluenceUser confluenceUser, BrowserMetricsDto browserMetricsDto) {
        this.log.info("Saving browser-metrics into the database.");
        BrowserMetrics browserMetrics = new BrowserMetrics(System.currentTimeMillis(), confluenceUser.getKey().toString(), this.gson.toJson((Object)browserMetricsDto));
        this.tmpl.persist(browserMetrics);
        return browserMetrics;
    }

    @Override
    public Optional<BrowserMetrics> findMostRecent(ConfluenceUser user) {
        return this.tmpl.query(BrowserMetrics.class, "select bm from BrowserMetrics bm where userKey=:userKey and createdAt >= :createdAt order by createdAt desc").param("userKey", (Object)user.getKey().getStringValue()).param("createdAt", (Object)(System.currentTimeMillis() - this.twentyFourHoursPeriod)).first();
    }

    @Override
    public List<BrowserMetrics> findAll() {
        return this.tmpl.query(BrowserMetrics.class, "select bm from BrowserMetrics bm").list();
    }

    @Override
    public List<BrowserMetrics> getPage(int pageSize, int offset) {
        List<BrowserMetrics> metrics = this.tmpl.query(BrowserMetrics.class, "select bm from BrowserMetrics bm order by createdAt desc").max(pageSize).first(offset).list();
        this.tmpl.evictAll(metrics);
        return metrics;
    }

    @Override
    public long getCount() {
        return this.tmpl.query(Long.class, "select count(*) from BrowserMetrics").first().orElse(0L);
    }

    @Override
    public void deleteStaleMetrics() {
        this.tmpl.query("delete bm from BrowserMetrics bm where createdAt < :createdAt").param("createdAt", (Object)(System.currentTimeMillis() - this.maxMetricsAge));
        this.log.info("Deleted $rowsDeleted stale browser metrics rows.");
    }
}

