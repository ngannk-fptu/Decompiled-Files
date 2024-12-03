/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class ImmutableJustInTimeConfig
implements JustInTimeConfig {
    @Nullable
    private final Boolean enabled;
    @Nullable
    private final String displayNameMappingExpression;
    @Nullable
    private final String emailMappingExpression;
    @Nullable
    private final String groupsMappingSource;
    private final List<String> additionalJitScopes;

    protected ImmutableJustInTimeConfig(@Nullable Boolean enabled, @Nullable String displayNameMappingExpression, @Nullable String emailMappingExpression, @Nullable String groupsMappingSource, Iterable<String> additionalJitScopes) {
        this.enabled = enabled;
        this.displayNameMappingExpression = displayNameMappingExpression;
        this.emailMappingExpression = emailMappingExpression;
        this.groupsMappingSource = groupsMappingSource;
        this.additionalJitScopes = ImmutableList.copyOf(additionalJitScopes);
    }

    @Override
    public Optional<Boolean> isEnabled() {
        return Optional.ofNullable(this.enabled);
    }

    @Override
    public Optional<String> getDisplayNameMappingExpression() {
        return Optional.ofNullable(this.displayNameMappingExpression);
    }

    @Override
    public Optional<String> getEmailMappingExpression() {
        return Optional.ofNullable(this.emailMappingExpression);
    }

    @Override
    public Optional<String> getGroupsMappingSource() {
        return Optional.ofNullable(this.groupsMappingSource);
    }

    @Override
    public List<String> getAdditionalJitScopes() {
        return this.additionalJitScopes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(JustInTimeConfig data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JustInTimeConfig that = (JustInTimeConfig)o;
        return Objects.equals(this.isEnabled(), that.isEnabled()) && Objects.equals(this.getDisplayNameMappingExpression(), that.getDisplayNameMappingExpression()) && Objects.equals(this.getEmailMappingExpression(), that.getEmailMappingExpression()) && Objects.equals(this.getGroupsMappingSource(), that.getGroupsMappingSource()) && Objects.equals(this.getAdditionalJitScopes(), that.getAdditionalJitScopes());
    }

    public int hashCode() {
        return Objects.hash(this.isEnabled(), this.getDisplayNameMappingExpression(), this.getEmailMappingExpression(), this.getGroupsMappingSource(), this.getAdditionalJitScopes());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("enabled", this.isEnabled()).add("displayNameMappingExpression", this.getDisplayNameMappingExpression()).add("emailMappingExpression", this.getEmailMappingExpression()).add("groupsMappingSource", this.getGroupsMappingSource()).add("additionalScopes", this.getAdditionalJitScopes()).toString();
    }

    public static final class Builder {
        private Boolean enabled;
        private String displayNameMappingExpression;
        private String emailMappingExpression;
        private String groupsMappingSource;
        private List<String> additionalJitScopes = new ArrayList<String>();

        private Builder() {
        }

        private Builder(JustInTimeConfig initialData) {
            if (initialData != null) {
                this.enabled = initialData.isEnabled().orElse(null);
                this.displayNameMappingExpression = initialData.getDisplayNameMappingExpression().orElse(null);
                this.emailMappingExpression = initialData.getEmailMappingExpression().orElse(null);
                this.groupsMappingSource = initialData.getGroupsMappingSource().orElse(null);
                this.additionalJitScopes = new ArrayList<String>(initialData.getAdditionalJitScopes());
            }
        }

        public Builder setEnabled(@Nullable Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setDisplayNameMappingExpression(@Nullable String displayNameMappingExpression) {
            this.displayNameMappingExpression = displayNameMappingExpression;
            return this;
        }

        public Builder setEmailMappingExpression(@Nullable String emailMappingExpression) {
            this.emailMappingExpression = emailMappingExpression;
            return this;
        }

        public Builder setGroupsMappingSource(@Nullable String groupsMappingSource) {
            this.groupsMappingSource = groupsMappingSource;
            return this;
        }

        public Builder setAdditionalJitScopes(Iterable<String> additionalJitScopes) {
            this.additionalJitScopes = additionalJitScopes != null ? ImmutableList.copyOf(additionalJitScopes) : Collections.emptyList();
            return this;
        }

        public Builder addAdditionalJitScope(String additionalScope) {
            this.additionalJitScopes.add(additionalScope);
            return this;
        }

        public Builder addAdditionalJitScopes(Iterable<String> additionalScopes) {
            for (String additionalScope : additionalScopes) {
                this.addAdditionalJitScope(additionalScope);
            }
            return this;
        }

        public ImmutableJustInTimeConfig build() {
            return new ImmutableJustInTimeConfig(this.enabled, this.displayNameMappingExpression, this.emailMappingExpression, this.groupsMappingSource, this.additionalJitScopes);
        }
    }
}

