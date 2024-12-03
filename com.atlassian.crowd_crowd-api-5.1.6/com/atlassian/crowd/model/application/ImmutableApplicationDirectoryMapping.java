/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;

public class ImmutableApplicationDirectoryMapping
implements ApplicationDirectoryMapping {
    private final ImmutableDirectory directory;
    private final boolean allowAllToAuthenticate;
    private final ImmutableSet<String> authorisedGroupNames;
    private final ImmutableSet<OperationType> allowedOperations;

    private ImmutableApplicationDirectoryMapping(Builder builder) {
        this.directory = builder.directory;
        this.allowAllToAuthenticate = builder.allowAllToAuthenticate;
        this.authorisedGroupNames = builder.authorisedGroupNames;
        this.allowedOperations = builder.allowedOperations;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationDirectoryMapping applicationDirectoryMapping) {
        return new Builder((ApplicationDirectoryMapping)Preconditions.checkNotNull((Object)applicationDirectoryMapping));
    }

    public static ImmutableApplicationDirectoryMapping from(ApplicationDirectoryMapping applicationDirectoryMapping) {
        return applicationDirectoryMapping instanceof ImmutableApplicationDirectoryMapping ? (ImmutableApplicationDirectoryMapping)applicationDirectoryMapping : ImmutableApplicationDirectoryMapping.builder(applicationDirectoryMapping).build();
    }

    public Directory getDirectory() {
        return this.directory;
    }

    public boolean isAllowAllToAuthenticate() {
        return this.allowAllToAuthenticate;
    }

    public Set<String> getAuthorisedGroupNames() {
        return this.authorisedGroupNames;
    }

    public Set<OperationType> getAllowedOperations() {
        return this.allowedOperations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableApplicationDirectoryMapping that = (ImmutableApplicationDirectoryMapping)o;
        return this.allowAllToAuthenticate == that.allowAllToAuthenticate && Objects.equals(this.directory, that.directory) && Objects.equals(this.authorisedGroupNames, that.authorisedGroupNames) && Objects.equals(this.allowedOperations, that.allowedOperations);
    }

    public int hashCode() {
        return Objects.hash(this.directory, this.allowAllToAuthenticate, this.authorisedGroupNames, this.allowedOperations);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("directory", (Object)this.directory).add("allowAllToAuthenticate", this.allowAllToAuthenticate).add("authorisedGroupNames", this.authorisedGroupNames).add("allowedOperations", this.allowedOperations).toString();
    }

    public static final class Builder {
        private ImmutableDirectory directory;
        private boolean allowAllToAuthenticate;
        private ImmutableSet<String> authorisedGroupNames = ImmutableSet.of();
        private ImmutableSet<OperationType> allowedOperations = ImmutableSet.of();

        private Builder() {
        }

        private Builder(ApplicationDirectoryMapping mapping) {
            this.setDirectory(mapping.getDirectory());
            this.setAllowedOperations(mapping.getAllowedOperations());
            this.setAuthorisedGroupNames(mapping.getAuthorisedGroupNames());
            this.setAllowAllToAuthenticate(mapping.isAllowAllToAuthenticate());
        }

        public Builder setDirectory(Directory directory) {
            this.directory = ImmutableDirectory.from(directory);
            return this;
        }

        public Builder setAllowAllToAuthenticate(boolean allowAllToAuthenticate) {
            this.allowAllToAuthenticate = allowAllToAuthenticate;
            return this;
        }

        public Builder setAuthorisedGroupNames(Set<String> authorisedGroupNames) {
            this.authorisedGroupNames = ImmutableSet.copyOf(authorisedGroupNames);
            return this;
        }

        public Builder setAllowedOperations(Set<OperationType> allowedOperations) {
            this.allowedOperations = ImmutableSet.copyOf(allowedOperations);
            return this;
        }

        public ImmutableApplicationDirectoryMapping build() {
            return new ImmutableApplicationDirectoryMapping(this);
        }
    }
}

