/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Strings
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.collections.MapUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.model.InternalEntity;
import com.atlassian.crowd.model.InternalEntityTemplate;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.BooleanAttributeUtil;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.crowd.model.webhook.Webhook;
import com.atlassian.crowd.util.InternalEntityUtils;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Deprecated
public class ApplicationImpl
extends InternalEntity
implements Application {
    private String lowerName;
    private ApplicationType type;
    private String description;
    private PasswordCredential credential;
    private Map<String, String> attributes = new HashMap<String, String>();
    private List<DirectoryMapping> directoryMappings = new ArrayList<DirectoryMapping>();
    private Set<RemoteAddress> remoteAddresses = new HashSet<RemoteAddress>();
    private Set<Webhook> webhooks = new HashSet<Webhook>();

    protected ApplicationImpl() {
    }

    protected ApplicationImpl(String name, long id, ApplicationType type, String description, PasswordCredential credential, boolean active, Map<String, String> attributes, List<DirectoryMapping> directoryMappings, Set<RemoteAddress> remoteAddresses, Set<Webhook> webhooks, Date createdDate, Date updatedDate) {
        this.setNameAndLowerName(name);
        this.id = id;
        this.type = type;
        this.description = description;
        this.credential = credential;
        this.active = active;
        this.attributes = MapUtils.isEmpty(attributes) ? Maps.newHashMap() : Maps.newHashMap(attributes);
        this.directoryMappings = CollectionUtils.isEmpty(directoryMappings) ? Lists.newArrayList() : Lists.newArrayList(directoryMappings);
        this.remoteAddresses = CollectionUtils.isEmpty(remoteAddresses) ? Sets.newHashSet() : Sets.newHashSet(remoteAddresses);
        this.webhooks = CollectionUtils.isEmpty(webhooks) ? Sets.newHashSet() : Sets.newHashSet(webhooks);
        this.createdDate = createdDate == null ? new Date() : new Date(createdDate.getTime());
        this.updatedDate = updatedDate == null ? new Date() : new Date(updatedDate.getTime());
    }

    public static ApplicationImpl convertIfNeeded(Application application) {
        return application instanceof ApplicationImpl ? (ApplicationImpl)application : ApplicationImpl.newInstance(application);
    }

    public static ApplicationImpl newInstance(Application application) {
        long applicationId = application.getId() == null ? -1L : application.getId();
        return new ApplicationImpl(application.getName(), applicationId, application.getType(), application.getDescription(), application.getCredential(), application.isActive(), application.getAttributes(), application.getDirectoryMappings(), application.getRemoteAddresses(), application.getWebhooks(), application.getCreatedDate(), application.getUpdatedDate());
    }

    public static ApplicationImpl newInstance(String name, ApplicationType type) {
        return ApplicationImpl.newInstanceWithPassword(name, type, null);
    }

    public static ApplicationImpl newInstanceWithIdAndCredential(String name, ApplicationType type, PasswordCredential credential, long id) {
        return new ApplicationImpl(name, id, type, null, credential, true, null, null, null, null, null, null);
    }

    public static ApplicationImpl newInstanceWithCredential(String name, ApplicationType type, PasswordCredential credential) {
        return new ApplicationImpl(name, -1L, type, null, credential, true, null, null, null, null, null, null);
    }

    public static ApplicationImpl newInstanceWithPassword(String name, ApplicationType type, String password) {
        return ApplicationImpl.newInstanceWithCredential(name, type, PasswordCredential.unencrypted((String)password));
    }

    public ApplicationImpl(InternalEntityTemplate template) {
        super(template);
    }

    public void updateDetailsFromApplication(Application application) {
        this.setName(application.getName());
        this.setDescription(application.getDescription());
        this.setType(application.getType());
        this.setActive(application.isActive());
        this.updateAttributesFrom(application.getAttributes());
        this.setAliasingEnabled(application.isAliasingEnabled());
        this.setMembershipAggregationEnabled(application.isMembershipAggregationEnabled());
        this.setAuthenticationOrderOptimizationForCachedDirectoriesEnabled(application.isCachedDirectoriesAuthenticationOrderOptimisationEnabled());
        this.setAuthenticationViaEmailEnabled(application.isAuthenticationViaEmailEnabled());
        this.setLowerCaseOutput(application.isLowerCaseOutput());
        this.setRemoteAddresses(application.getRemoteAddresses());
        this.setFilterUsersWithAccessEnabled(application.isFilteringUsersWithAccessEnabled());
        this.setFilterGroupsWithAccessEnabled(application.isFilteringGroupsWithAccessEnabled());
        this.webhooks.retainAll(application.getWebhooks());
        this.webhooks.addAll(application.getWebhooks());
    }

    public void updateAttributesFrom(Map<String, String> attributes) {
        this.attributes.entrySet().retainAll(attributes.entrySet());
        this.attributes.putAll(Maps.filterValues(attributes, value -> !Strings.isNullOrEmpty((String)value)));
    }

    public void validate() {
        Validate.notNull((Object)this.name, (String)"name cannot be null", (Object[])new Object[0]);
        Validate.isTrue((boolean)IdentifierUtils.toLowerCase((String)this.name).equals(this.lowerName), (String)"lowerName must be the lower-case representation of name", (Object[])new Object[0]);
        Validate.notNull((Object)this.type, (String)"type cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.credential, (String)"credential cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.credential.getCredential(), (String)"credential cannot have null value", (Object[])new Object[0]);
        Validate.isTrue((boolean)this.credential.isEncryptedCredential(), (String)"credential must be encrypted", (Object[])new Object[0]);
        Validate.notNull((Object)this.createdDate, (String)"createdDate cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)this.updatedDate, (String)"updatedDate cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.attributes, (String)"attributes cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.directoryMappings, (String)"directoryMappings cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.remoteAddresses, (String)"remoteAddresses cannot be null", (Object[])new Object[0]);
        Validate.notNull(this.webhooks, (String)"webhooks cannot be null", (Object[])new Object[0]);
    }

    @Override
    public void setName(String name) {
        this.setNameAndLowerName(name);
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getLowerName() {
        return this.lowerName;
    }

    private void setLowerName(String lowerName) {
        this.lowerName = lowerName;
    }

    public ApplicationType getType() {
        return this.type;
    }

    public void setType(ApplicationType type) {
        Validate.notNull((Object)type);
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PasswordCredential getCredential() {
        return this.credential;
    }

    public void setCredential(PasswordCredential credential) {
        this.credential = credential;
    }

    public boolean isPermanent() {
        return this.type.equals((Object)ApplicationType.CROWD) || this.type.equals((Object)ApplicationType.PLUGIN);
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<DirectoryMapping> getDirectoryMappings() {
        return this.directoryMappings;
    }

    @Nonnull
    public List<ApplicationDirectoryMapping> getApplicationDirectoryMappings() {
        return Lists.transform(this.directoryMappings, (Function)Functions.identity());
    }

    public void addDirectoryMapping(Directory directory, boolean allowAllToAuthenticate, OperationType ... operationTypes) {
        DirectoryMapping directoryMapping = this.getDirectoryMapping(directory.getId());
        if (directoryMapping == null) {
            directoryMapping = new DirectoryMapping((Application)this, directory, allowAllToAuthenticate, new HashSet<OperationType>(Arrays.asList(operationTypes)));
            this.directoryMappings.add(directoryMapping);
        } else {
            directoryMapping.setAllowAllToAuthenticate(allowAllToAuthenticate);
            directoryMapping.setAllowedOperations(new HashSet<OperationType>(Arrays.asList(operationTypes)));
        }
    }

    public void addGroupMapping(long directoryId, String groupName) {
        DirectoryMapping directoryMapping = this.getDirectoryMapping(directoryId);
        if (directoryMapping == null) {
            throw new IllegalArgumentException("The application <" + this.name + "> does not contain a directory mapping for directory with id <" + directoryId + ">");
        }
        directoryMapping.addGroupMapping(groupName);
    }

    public DirectoryMapping getDirectoryMapping(long directoryId) {
        for (DirectoryMapping mapping : this.directoryMappings) {
            if (mapping.getDirectory().getId() != directoryId) continue;
            return mapping;
        }
        return null;
    }

    @Nullable
    public ApplicationDirectoryMapping getApplicationDirectoryMapping(long directoryId) {
        return this.getDirectoryMapping(directoryId);
    }

    public boolean removeDirectoryMapping(long directoryId) {
        DirectoryMapping mapping = this.getDirectoryMapping(directoryId);
        return this.directoryMappings.remove(mapping);
    }

    private void setDirectoryMappings(List<DirectoryMapping> directoryMappings) {
        this.directoryMappings = directoryMappings;
    }

    public Set<RemoteAddress> getRemoteAddresses() {
        return this.remoteAddresses;
    }

    public void addRemoteAddress(String remoteAddress) {
        this.remoteAddresses.add(new RemoteAddress(remoteAddress));
    }

    public void setRemoteAddresses(Set<RemoteAddress> remoteAddresses) {
        this.remoteAddresses = remoteAddresses;
    }

    public boolean hasRemoteAddress(String remoteAddress) {
        return this.getRemoteAddresses().contains(new RemoteAddress(remoteAddress));
    }

    public boolean removeRemoteAddress(String remoteAddress) {
        return this.getRemoteAddresses().remove(new RemoteAddress(remoteAddress));
    }

    public Set<Webhook> getWebhooks() {
        return this.webhooks;
    }

    public void setWebhooks(Set<Webhook> webhooks) {
        this.webhooks = webhooks;
    }

    public Set<String> getValues(String name) {
        String value = this.getValue(name);
        if (value != null) {
            return Collections.singleton(value);
        }
        return null;
    }

    public String getValue(String name) {
        return this.attributes.get(name);
    }

    public Set<String> getKeys() {
        return this.attributes.keySet();
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    public void setAttribute(String name, String value) {
        this.attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public boolean isFilteringUsersWithAccessEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "filterUsersWithAccess");
    }

    public void setFilterUsersWithAccessEnabled(boolean filterUsersWithAccessEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "filterUsersWithAccess", filterUsersWithAccessEnabled);
    }

    public boolean isFilteringGroupsWithAccessEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "filterGroupsWithAccess");
    }

    public void setFilterGroupsWithAccessEnabled(boolean filterGroupsWithAccessEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "filterGroupsWithAccess", filterGroupsWithAccessEnabled);
    }

    public boolean isLowerCaseOutput() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "lowerCaseOutput");
    }

    public void setLowerCaseOutput(boolean value) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "lowerCaseOutput", value);
    }

    public void setAliasingEnabled(boolean aliasingEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "aliasingEnabled", aliasingEnabled);
    }

    public boolean isAliasingEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "aliasingEnabled");
    }

    public boolean isMembershipAggregationEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "aggregateMemberships");
    }

    public void setMembershipAggregationEnabled(boolean membershipAggregationEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "aggregateMemberships", membershipAggregationEnabled);
    }

    public boolean isCachedDirectoriesAuthenticationOrderOptimisationEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "optimizeCachedDirectoriesAuthenticationAuthenticationOrder");
    }

    public void setAuthenticationOrderOptimizationForCachedDirectoriesEnabled(boolean authenticationOrderOptimizationForCachedDirectoriesEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "optimizeCachedDirectoriesAuthenticationAuthenticationOrder", authenticationOrderOptimizationForCachedDirectoriesEnabled);
    }

    public boolean isAuthenticationWithoutPasswordEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "insecureAuthenticationEnabled");
    }

    public void setAuthenticationWithoutPasswordEnabled(boolean authenticationWithoutPasswordEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "insecureAuthenticationEnabled", authenticationWithoutPasswordEnabled);
    }

    public boolean isAuthenticationViaEmailEnabled() {
        return BooleanAttributeUtil.getBooleanAttribute(this.attributes, "authenticationByEmailEnabled");
    }

    public void setAuthenticationViaEmailEnabled(boolean authenticationViaEmailEnabled) {
        BooleanAttributeUtil.setBooleanAttribute(this.attributes, "authenticationByEmailEnabled", authenticationViaEmailEnabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationImpl)) {
            return false;
        }
        ApplicationImpl that = (ApplicationImpl)o;
        return !(this.getLowerName() != null ? !this.getLowerName().equals(that.getLowerName()) : that.getLowerName() != null);
    }

    @Override
    public int hashCode() {
        return this.getLowerName() != null ? this.getLowerName().hashCode() : 0;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("lowerName", (Object)this.getLowerName()).append("type", (Object)this.getType()).append("description", (Object)this.getDescription()).append("credential", (Object)this.getCredential()).append("attributes", this.getAttributes()).append("directoryMappings", this.getDirectoryMappings()).append("remoteAddresses", this.getRemoteAddresses()).append("webhooks", this.getWebhooks()).toString();
    }

    private void setNameAndLowerName(String name) {
        Validate.notNull((Object)name);
        InternalEntityUtils.validateLength(name);
        this.name = name;
        this.lowerName = IdentifierUtils.toLowerCase((String)name);
    }
}

