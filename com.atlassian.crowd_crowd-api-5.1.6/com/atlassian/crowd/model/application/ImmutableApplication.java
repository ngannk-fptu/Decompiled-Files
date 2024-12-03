/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.BooleanAttributeUtil;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.ImmutableApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.model.webhook.ImmutableWebhook;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class ImmutableApplication
implements Application {
    private final Long id;
    private final String name;
    private final ApplicationType type;
    private final String description;
    private final PasswordCredential passwordCredential;
    private final boolean permanent;
    private final boolean active;
    private final Map<String, String> attributes;
    private final List<ApplicationDirectoryMapping> directoryMappings;
    private final Set<RemoteAddress> remoteAddresses;
    private final Set<ImmutableWebhook> webhooks;
    private final boolean lowercaseOutput;
    private final boolean aliasingEnabled;
    private final boolean filteringUsersWithAccessEnabled;
    private final boolean filteringGroupsWithAccessEnabled;
    private final boolean membershipAggregationEnabled;
    private final boolean cachedDirectoriesAuthenticationOrderOptimisationEnabled;
    private final boolean authenticationWithoutPasswordEnabled;
    private final boolean authenticationByEmailEnabled;
    private final Date createdDate;
    private final Date updatedDate;

    @Deprecated
    public ImmutableApplication(Long id, String name, ApplicationType type, String description, PasswordCredential passwordCredential, boolean permanent, boolean active, Map<String, String> attributes, List<DirectoryMapping> directoryMappings, Set<RemoteAddress> remoteAddresses, Set<Webhook> webhooks, boolean lowercaseOutput, boolean aliasingEnabled, Date createdDate, Date updatedDate) {
        this(id, name, type, description, passwordCredential, permanent, active, attributes, Lists.transform(directoryMappings, ImmutableApplicationDirectoryMapping::from), remoteAddresses, webhooks, lowercaseOutput, aliasingEnabled, false, false, false, false, false, false, createdDate, updatedDate);
    }

    private ImmutableApplication(Long id, String name, ApplicationType type, String description, PasswordCredential passwordCredential, boolean permanent, boolean active, Map<String, String> attributes, List<ApplicationDirectoryMapping> directoryMappings, Set<RemoteAddress> remoteAddresses, Set<Webhook> webhooks, boolean lowercaseOutput, boolean aliasingEnabled, boolean membershipAggregationEnabled, boolean cachedDirectoriesAuthenticationOrderOptimisationEnabled, boolean authenticationWithoutPasswordEnabled, boolean authenticationByEmailEnabled, boolean filteringUsersWithAccessEnabled, boolean filteringGroupsWithAccessEnabled, Date createdDate, Date updatedDate) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.passwordCredential = passwordCredential;
        this.permanent = permanent;
        this.active = active;
        this.attributes = attributes == null ? Collections.emptyMap() : ImmutableMap.copyOf(attributes);
        this.directoryMappings = directoryMappings == null ? Collections.emptyList() : ImmutableList.copyOf(directoryMappings);
        this.remoteAddresses = remoteAddresses == null ? Collections.emptySet() : ImmutableSet.copyOf(remoteAddresses);
        this.webhooks = webhooks == null ? Collections.emptySet() : ImmutableSet.copyOf((Collection)webhooks.stream().map(ImmutableWebhook::builder).map(b -> b.setApplication(this).build()).collect(Collectors.toSet()));
        this.lowercaseOutput = lowercaseOutput;
        this.aliasingEnabled = aliasingEnabled;
        this.membershipAggregationEnabled = membershipAggregationEnabled;
        this.cachedDirectoriesAuthenticationOrderOptimisationEnabled = cachedDirectoriesAuthenticationOrderOptimisationEnabled;
        this.authenticationWithoutPasswordEnabled = authenticationWithoutPasswordEnabled;
        this.authenticationByEmailEnabled = authenticationByEmailEnabled;
        this.filteringUsersWithAccessEnabled = filteringUsersWithAccessEnabled;
        this.filteringGroupsWithAccessEnabled = filteringGroupsWithAccessEnabled;
        this.createdDate = createdDate == null ? null : new Date(createdDate.getTime());
        this.updatedDate = updatedDate == null ? null : new Date(updatedDate.getTime());
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public ApplicationType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public PasswordCredential getCredential() {
        return this.passwordCredential == null ? null : new PasswordCredential(this.passwordCredential);
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    public boolean isActive() {
        return this.active;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public List<DirectoryMapping> getDirectoryMappings() {
        return this.getApplicationDirectoryMappings().stream().map(applicationDirectoryMapping -> DirectoryMapping.fromApplicationDirectoryMapping((Application)this, (ApplicationDirectoryMapping)applicationDirectoryMapping)).collect(Collectors.toList());
    }

    @Nonnull
    public List<ApplicationDirectoryMapping> getApplicationDirectoryMappings() {
        return this.directoryMappings;
    }

    public DirectoryMapping getDirectoryMapping(long directoryId) {
        ApplicationDirectoryMapping mapping = this.getApplicationDirectoryMapping(directoryId);
        return mapping == null ? null : DirectoryMapping.fromApplicationDirectoryMapping((Application)this, (ApplicationDirectoryMapping)mapping);
    }

    public ApplicationDirectoryMapping getApplicationDirectoryMapping(long directoryId) {
        Long dirId = directoryId;
        return this.directoryMappings.stream().filter(mapping -> dirId.equals(mapping.getDirectory().getId())).findFirst().orElse(null);
    }

    public Set<RemoteAddress> getRemoteAddresses() {
        return this.remoteAddresses;
    }

    public boolean hasRemoteAddress(String remoteAddress) {
        return this.remoteAddresses.contains(new RemoteAddress(remoteAddress));
    }

    public Set<Webhook> getWebhooks() {
        return Collections.unmodifiableSet(this.webhooks);
    }

    public boolean isLowerCaseOutput() {
        return this.lowercaseOutput;
    }

    public boolean isAliasingEnabled() {
        return this.aliasingEnabled;
    }

    public boolean isFilteringUsersWithAccessEnabled() {
        return this.filteringUsersWithAccessEnabled;
    }

    public boolean isFilteringGroupsWithAccessEnabled() {
        return this.filteringGroupsWithAccessEnabled;
    }

    public boolean isMembershipAggregationEnabled() {
        return this.membershipAggregationEnabled;
    }

    public boolean isCachedDirectoriesAuthenticationOrderOptimisationEnabled() {
        return this.cachedDirectoriesAuthenticationOrderOptimisationEnabled;
    }

    public boolean isAuthenticationWithoutPasswordEnabled() {
        return this.authenticationWithoutPasswordEnabled;
    }

    public boolean isAuthenticationViaEmailEnabled() {
        return this.authenticationByEmailEnabled;
    }

    public Date getCreatedDate() {
        return this.createdDate == null ? null : new Date(this.createdDate.getTime());
    }

    public Date getUpdatedDate() {
        return this.updatedDate == null ? null : new Date(this.updatedDate.getTime());
    }

    public Set<String> getValues(String key) {
        String value = this.attributes.get(key);
        return value == null ? Collections.emptySet() : Collections.singleton(value);
    }

    public String getValue(String key) {
        return this.attributes.get(key);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public static Builder builder(String name, ApplicationType type) {
        return new Builder(name, type);
    }

    public static Builder builder(Application application) {
        return new Builder(application);
    }

    public static ImmutableApplication from(Application application) {
        if (application instanceof ImmutableApplication) {
            return (ImmutableApplication)application;
        }
        return ImmutableApplication.builder(application).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableApplication that = (ImmutableApplication)o;
        return this.permanent == that.permanent && this.active == that.active && this.lowercaseOutput == that.lowercaseOutput && this.aliasingEnabled == that.aliasingEnabled && this.authenticationWithoutPasswordEnabled == that.authenticationWithoutPasswordEnabled && this.authenticationByEmailEnabled == that.authenticationByEmailEnabled && this.filteringUsersWithAccessEnabled == that.filteringUsersWithAccessEnabled && this.filteringGroupsWithAccessEnabled == that.filteringGroupsWithAccessEnabled && this.membershipAggregationEnabled == that.membershipAggregationEnabled && this.cachedDirectoriesAuthenticationOrderOptimisationEnabled == that.cachedDirectoriesAuthenticationOrderOptimisationEnabled && Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && this.type == that.type && Objects.equals(this.description, that.description) && Objects.equals(this.passwordCredential, that.passwordCredential) && Objects.equals(this.attributes, that.attributes) && Objects.equals(this.directoryMappings, that.directoryMappings) && Objects.equals(this.remoteAddresses, that.remoteAddresses) && Objects.equals(this.webhooks, that.webhooks) && Objects.equals(this.createdDate, that.createdDate) && Objects.equals(this.updatedDate, that.updatedDate);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.type, this.description, this.passwordCredential, this.permanent, this.active, this.attributes, this.directoryMappings, this.remoteAddresses, this.webhooks, this.lowercaseOutput, this.aliasingEnabled, this.filteringUsersWithAccessEnabled, this.authenticationWithoutPasswordEnabled, this.authenticationByEmailEnabled, this.filteringGroupsWithAccessEnabled, this.membershipAggregationEnabled, this.cachedDirectoriesAuthenticationOrderOptimisationEnabled, this.createdDate, this.updatedDate);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("type", (Object)this.type).add("description", (Object)this.description).add("permanent", this.permanent).add("active", this.active).add("directoryMappings", this.directoryMappings).add("remoteAddresses", this.remoteAddresses).toString();
    }

    public static class Builder {
        private Long id;
        private String name;
        private ApplicationType type;
        private String description;
        private PasswordCredential passwordCredential;
        private boolean permanent;
        private boolean active;
        private Map<String, String> attributes = new HashMap<String, String>();
        private List<ApplicationDirectoryMapping> directoryMappings;
        private Set<RemoteAddress> remoteAddresses;
        private Set<Webhook> webhooks;
        private Date createdDate;
        private Date updatedDate;

        public Builder(String name, ApplicationType type) {
            this.name = name;
            this.type = type;
        }

        public Builder(Application application) {
            Preconditions.checkNotNull((Object)application, (Object)"application");
            this.id = application.getId();
            this.name = application.getName();
            this.type = application.getType();
            this.description = application.getDescription();
            this.passwordCredential = application.getCredential();
            this.permanent = application.isPermanent();
            this.active = application.isActive();
            this.attributes = new HashMap<String, String>(application.getAttributes());
            this.setDirectoryMappings(application.getDirectoryMappings());
            this.remoteAddresses = ImmutableSet.copyOf((Collection)application.getRemoteAddresses());
            this.webhooks = ImmutableSet.copyOf((Collection)application.getWebhooks());
            this.createdDate = application.getCreatedDate();
            this.updatedDate = application.getUpdatedDate();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(ApplicationType type) {
            this.type = type;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setPasswordCredential(PasswordCredential passwordCredential) {
            this.passwordCredential = passwordCredential;
            return this;
        }

        public Builder setPermanent(boolean permanent) {
            this.permanent = permanent;
            return this;
        }

        public Builder setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setDirectoryMappings(List<DirectoryMapping> directoryMappings) {
            this.setApplicationDirectoryMappings(directoryMappings);
            return this;
        }

        public Builder setApplicationDirectoryMappings(Collection<? extends ApplicationDirectoryMapping> directoryMappings) {
            this.directoryMappings = directoryMappings.stream().map(ImmutableApplicationDirectoryMapping::from).collect(Collectors.toList());
            return this;
        }

        public Builder setRemoteAddresses(Set<RemoteAddress> remoteAddresses) {
            this.remoteAddresses = remoteAddresses;
            return this;
        }

        public Builder setWebhooks(Set<Webhook> webhooks) {
            this.webhooks = webhooks;
            return this;
        }

        public Builder setLowercaseOutput(boolean lowercaseOutput) {
            this.attributes.put("lowerCaseOutput", Boolean.toString(lowercaseOutput));
            return this;
        }

        public Builder setAliasingEnabled(boolean aliasingEnabled) {
            this.attributes.put("aliasingEnabled", Boolean.toString(aliasingEnabled));
            return this;
        }

        public Builder setMembershipAggregationEnabled(boolean membershipAggregationEnabled) {
            this.attributes.put("aggregateMemberships", Boolean.toString(membershipAggregationEnabled));
            return this;
        }

        public Builder setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder setUpdatedDate(Date updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }

        public Builder setFilteringUsersWithAccessEnabled(boolean filteringUsersWithAccessEnabled) {
            this.attributes.put("filterUsersWithAccess", Boolean.toString(filteringUsersWithAccessEnabled));
            return this;
        }

        public Builder setFilteringGroupsWithAccessEnabled(boolean filteringGroupsWithAccessEnabled) {
            this.attributes.put("filterGroupsWithAccess", Boolean.toString(filteringGroupsWithAccessEnabled));
            return this;
        }

        public Builder setCachedDirectoriesAuthenticationOrderOptimisationEnabled(boolean cachedDirectoriesAuthenticationOrderOptimisationEnabled) {
            this.attributes.put("optimizeCachedDirectoriesAuthenticationAuthenticationOrder", Boolean.toString(cachedDirectoriesAuthenticationOrderOptimisationEnabled));
            return this;
        }

        public Builder setAuthenticationWithoutPasswordEnabled(boolean authenticationWithoutPasswordEnabled) {
            this.attributes.put("insecureAuthenticationEnabled", Boolean.toString(authenticationWithoutPasswordEnabled));
            return this;
        }

        public Builder setAuthenticationByEmailEnabled(boolean authenticationByEmailEnabled) {
            this.attributes.put("authenticationByEmailEnabled", Boolean.toString(authenticationByEmailEnabled));
            return this;
        }

        public ImmutableApplication build() {
            return new ImmutableApplication(this.id, this.name, this.type, this.description, this.passwordCredential, this.permanent, this.active, this.attributes, this.directoryMappings, this.remoteAddresses, this.webhooks, BooleanAttributeUtil.getBooleanAttribute(this.attributes, "lowerCaseOutput"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "aliasingEnabled"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "aggregateMemberships"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "optimizeCachedDirectoriesAuthenticationAuthenticationOrder"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "insecureAuthenticationEnabled"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "authenticationByEmailEnabled"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "filterUsersWithAccess"), BooleanAttributeUtil.getBooleanAttribute(this.attributes, "filterGroupsWithAccess"), this.createdDate, this.updatedDate);
        }
    }
}

