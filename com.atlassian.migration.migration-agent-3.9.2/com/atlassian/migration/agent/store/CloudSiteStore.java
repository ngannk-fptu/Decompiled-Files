/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.CloudSite;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface CloudSiteStore {
    public CloudSite create(CloudSite var1);

    public CloudSite update(CloudSite var1);

    @Nonnull
    public List<CloudSite> getAllSites();

    @Nonnull
    public List<CloudSite> getNonFailingSites();

    public Optional<CloudSite> getByCloudId(String var1);

    public Optional<CloudSite> getByCloudUrl(String var1);

    public Optional<CloudSite> getByContainerToken(String var1);

    public void removeSiteByCloudId(String var1);

    public CloudSite getByStepId(String var1);

    public Optional<String> getNonFailingToken();

    public void markTokenAsFailed(String var1);

    public void markTokenAsFailedForCloudId(String var1);

    public void markAllTokensAsFailed();
}

