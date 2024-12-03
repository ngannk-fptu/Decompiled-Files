/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.plugin.rest.entity.admin.DirectoryMappingsEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class ApplicationEntity {
    @JsonProperty(value="id")
    private Long id;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="description")
    private String description;
    @JsonProperty(value="type")
    private ApplicationType type;
    @JsonProperty(value="active")
    private Boolean active;
    @JsonProperty(value="aliasingEnabled")
    private Boolean aliasingEnabled;
    @JsonProperty(value="lowercaseOutput")
    private Boolean lowercaseOutput;
    @JsonProperty(value="aggregateMemberships")
    private Boolean aggregateMemberships;
    @JsonProperty(value="cachedDirectoriesAuthenticationOrderOptimisation")
    private Boolean cachedDirectoriesAuthenticationOrderOptimisation;
    @Expandable(value="directoryMappings")
    @JsonProperty(value="directoryMappings")
    private DirectoryMappingsEntity directoryMappings;
    @JsonProperty(value="filteringUsersWithAccessEnabled")
    private Boolean filteringUsersWithAccessEnabled;
    @JsonProperty(value="filteringGroupsWithAccessEnabled")
    private Boolean filteringGroupsWithAccessEnabled;

    protected ApplicationEntity() {
    }

    public ApplicationEntity(Long id, String name, String description, ApplicationType type, Boolean active, Boolean aliasingEnabled, Boolean lowercaseOutput, Boolean aggregateMemberships, Boolean cachedDirectoriesAuthenticationOrderOptimisation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.active = active;
        this.aliasingEnabled = aliasingEnabled;
        this.lowercaseOutput = lowercaseOutput;
        this.aggregateMemberships = aggregateMemberships;
        this.cachedDirectoriesAuthenticationOrderOptimisation = cachedDirectoriesAuthenticationOrderOptimisation;
        this.filteringUsersWithAccessEnabled = null;
        this.filteringGroupsWithAccessEnabled = null;
    }

    public ApplicationEntity(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.type = builder.type;
        this.active = builder.active;
        this.aliasingEnabled = builder.aliasingEnabled;
        this.lowercaseOutput = builder.lowercaseOutput;
        this.aggregateMemberships = builder.aggregateMemberships;
        this.cachedDirectoriesAuthenticationOrderOptimisation = builder.cachedDirectoriesAuthenticationOrderOptimisation;
        this.filteringUsersWithAccessEnabled = builder.filteringUsersWithAccessEnabled;
        this.filteringGroupsWithAccessEnabled = builder.filteringGroupsWithAccessEnabled;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ApplicationType getType() {
        return this.type;
    }

    public Boolean getActive() {
        return this.active;
    }

    public Boolean getAliasingEnabled() {
        return this.aliasingEnabled;
    }

    public Boolean getLowercaseOutput() {
        return this.lowercaseOutput;
    }

    public Boolean getAggregateMemberships() {
        return this.aggregateMemberships;
    }

    public Boolean getCachedDirectoriesAuthenticationOrderOptimisation() {
        return this.cachedDirectoriesAuthenticationOrderOptimisation;
    }

    public Boolean getFilteringUsersWithAccessEnabled() {
        return this.filteringUsersWithAccessEnabled;
    }

    public Boolean getFilteringGroupsWithAccessEnabled() {
        return this.filteringGroupsWithAccessEnabled;
    }

    public DirectoryMappingsEntity getDirectoryMappings() {
        return this.directoryMappings;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationEntity that = (ApplicationEntity)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.description, that.description) && this.type == that.type && Objects.equals(this.active, that.active) && Objects.equals(this.aliasingEnabled, that.aliasingEnabled) && Objects.equals(this.lowercaseOutput, that.lowercaseOutput) && Objects.equals(this.aggregateMemberships, that.aggregateMemberships) && Objects.equals(this.cachedDirectoriesAuthenticationOrderOptimisation, that.cachedDirectoriesAuthenticationOrderOptimisation) && Objects.equals(this.directoryMappings, that.directoryMappings) && Objects.equals(this.filteringUsersWithAccessEnabled, that.filteringUsersWithAccessEnabled) && Objects.equals(this.filteringGroupsWithAccessEnabled, that.filteringGroupsWithAccessEnabled);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.description, this.type, this.active, this.aliasingEnabled, this.lowercaseOutput, this.aggregateMemberships, this.cachedDirectoriesAuthenticationOrderOptimisation, this.directoryMappings, this.filteringUsersWithAccessEnabled, this.filteringGroupsWithAccessEnabled);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("description", (Object)this.description).add("type", (Object)this.type).add("active", (Object)this.active).add("aliasingEnabled", (Object)this.aliasingEnabled).add("lowercaseOutput", (Object)this.lowercaseOutput).add("aggregateMemberships", (Object)this.aggregateMemberships).add("cachedDirectoriesAuthenticationOrderOptimisation", (Object)this.cachedDirectoriesAuthenticationOrderOptimisation).add("directoryMappings", (Object)this.directoryMappings).add("filteringUsersWithAccessEnabled", (Object)this.filteringUsersWithAccessEnabled).add("filteringGroupsWithAccessEnabled", (Object)this.filteringGroupsWithAccessEnabled).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationEntity applicationEntity) {
        return new Builder(applicationEntity);
    }

    public static final class Builder {
        private Long id;
        private String name;
        private String description;
        private ApplicationType type;
        private Boolean active;
        private Boolean aliasingEnabled;
        private Boolean lowercaseOutput;
        private Boolean aggregateMemberships;
        private Boolean cachedDirectoriesAuthenticationOrderOptimisation;
        private Boolean filteringUsersWithAccessEnabled;
        private Boolean filteringGroupsWithAccessEnabled;
        private DirectoryMappingsEntity directoryMappings;

        private Builder() {
        }

        private Builder(ApplicationEntity initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName();
            this.description = initialData.getDescription();
            this.type = initialData.getType();
            this.active = initialData.getActive();
            this.aliasingEnabled = initialData.getAliasingEnabled();
            this.lowercaseOutput = initialData.getLowercaseOutput();
            this.aggregateMemberships = initialData.getAggregateMemberships();
            this.cachedDirectoriesAuthenticationOrderOptimisation = initialData.getCachedDirectoriesAuthenticationOrderOptimisation();
            this.filteringUsersWithAccessEnabled = initialData.getFilteringUsersWithAccessEnabled();
            this.filteringGroupsWithAccessEnabled = initialData.getFilteringGroupsWithAccessEnabled();
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setType(ApplicationType type) {
            this.type = type;
            return this;
        }

        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setAliasingEnabled(Boolean aliasingEnabled) {
            this.aliasingEnabled = aliasingEnabled;
            return this;
        }

        public Builder setLowercaseOutput(Boolean lowercaseOutput) {
            this.lowercaseOutput = lowercaseOutput;
            return this;
        }

        public Builder setAggregateMemberships(Boolean aggregateMemberships) {
            this.aggregateMemberships = aggregateMemberships;
            return this;
        }

        public Builder setFilteringUsersWithAccessEnabled(Boolean filteringUsersWithAccessEnabled) {
            this.filteringUsersWithAccessEnabled = filteringUsersWithAccessEnabled;
            return this;
        }

        public Builder setFilteringGroupsWithAccessEnabled(Boolean filteringGroupsWithAccessEnabled) {
            this.filteringGroupsWithAccessEnabled = filteringGroupsWithAccessEnabled;
            return this;
        }

        public Builder setCachedDirectoriesAuthenticationOrderOptimisation(Boolean cachedDirectoriesAuthenticationOrderOptimisation) {
            this.cachedDirectoriesAuthenticationOrderOptimisation = cachedDirectoriesAuthenticationOrderOptimisation;
            return this;
        }

        public Builder setDirectoryMappings(DirectoryMappingsEntity directoryMappings) {
            this.directoryMappings = directoryMappings;
            return this;
        }

        public ApplicationEntity build() {
            return new ApplicationEntity(this);
        }
    }
}

