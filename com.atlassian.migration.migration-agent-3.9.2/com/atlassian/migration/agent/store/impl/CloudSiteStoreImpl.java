/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.store.CloudSiteStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CloudSiteStoreImpl
implements CloudSiteStore {
    private final EntityManagerTemplate tmpl;

    public CloudSiteStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    @Nonnull
    public List<CloudSite> getAllSites() {
        return this.tmpl.query(CloudSite.class, "select site from CloudSite site").list();
    }

    @Override
    @Nonnull
    public List<CloudSite> getNonFailingSites() {
        return this.tmpl.query(CloudSite.class, "select site from CloudSite site where isFailing = false").list();
    }

    @Override
    public Optional<CloudSite> getByCloudId(String cloudId) {
        return this.tmpl.query(CloudSite.class, "select site from CloudSite site where site.cloudId=:cloudId").param("cloudId", (Object)cloudId).first();
    }

    @Override
    public Optional<CloudSite> getByCloudUrl(String cloudUrl) {
        return this.tmpl.query(CloudSite.class, "select site from CloudSite site where site.cloudUrl=:cloudUrl").param("cloudUrl", (Object)cloudUrl).first();
    }

    @Override
    public Optional<CloudSite> getByContainerToken(String containerToken) {
        return this.tmpl.query(CloudSite.class, "select site from CloudSite site where site.containerToken=:containerToken").param("containerToken", (Object)containerToken).first();
    }

    @Override
    public void removeSiteByCloudId(String cloudId) {
        this.tmpl.query("delete from CloudSite site where site.cloudId=:cloudId").param("cloudId", (Object)cloudId).flush(true).update();
    }

    @Override
    public CloudSite getByStepId(String stepId) {
        return this.tmpl.query(CloudSite.class, "select step.plan.cloudSite from Step step where step.id=:stepId").param("stepId", (Object)stepId).single();
    }

    @Override
    public Optional<String> getNonFailingToken() {
        return this.tmpl.query(String.class, "select containerToken from CloudSite where isFailing = false order by createdTime desc").first();
    }

    @Override
    public void markTokenAsFailed(String token) {
        this.tmpl.query("update CloudSite set isFailing = true where containerToken = :token").param("token", (Object)token).update();
    }

    @Override
    public void markTokenAsFailedForCloudId(String cloudId) {
        this.tmpl.query("update CloudSite set isFailing = true where cloudId = :cloudId").param("cloudId", (Object)cloudId).update();
    }

    @Override
    public void markAllTokensAsFailed() {
        this.tmpl.query("update CloudSite set isFailing = true").update();
    }

    @Override
    public CloudSite create(CloudSite site) {
        this.tmpl.persist(site);
        return site;
    }

    @Override
    public CloudSite update(CloudSite site) {
        this.tmpl.merge(site);
        return site;
    }
}

