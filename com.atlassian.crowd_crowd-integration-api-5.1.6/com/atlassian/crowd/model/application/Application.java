/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.application;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.model.webhook.Webhook;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Application
extends Serializable,
Attributes {
    public Long getId();

    public String getName();

    public ApplicationType getType();

    public String getDescription();

    public PasswordCredential getCredential();

    public boolean isPermanent();

    public boolean isActive();

    public Map<String, String> getAttributes();

    @Deprecated
    public List<DirectoryMapping> getDirectoryMappings();

    @Nonnull
    public List<ApplicationDirectoryMapping> getApplicationDirectoryMappings();

    @Deprecated
    public DirectoryMapping getDirectoryMapping(long var1);

    @Nullable
    public ApplicationDirectoryMapping getApplicationDirectoryMapping(long var1);

    public Set<RemoteAddress> getRemoteAddresses();

    public boolean hasRemoteAddress(String var1);

    public Set<Webhook> getWebhooks();

    @ExperimentalApi
    public boolean isFilteringUsersWithAccessEnabled();

    @ExperimentalApi
    public boolean isFilteringGroupsWithAccessEnabled();

    public boolean isLowerCaseOutput();

    public boolean isAliasingEnabled();

    public boolean isMembershipAggregationEnabled();

    public boolean isCachedDirectoriesAuthenticationOrderOptimisationEnabled();

    public boolean isAuthenticationWithoutPasswordEnabled();

    public boolean isAuthenticationViaEmailEnabled();

    public Date getCreatedDate();

    public Date getUpdatedDate();
}

