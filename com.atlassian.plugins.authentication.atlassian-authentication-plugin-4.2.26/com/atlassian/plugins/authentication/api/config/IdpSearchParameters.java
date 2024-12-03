/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.plugins.authentication.api.config;

import com.atlassian.plugins.authentication.api.config.PageParameters;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class IdpSearchParameters {
    private final PageParameters pageParameters;
    @Nullable
    private final Boolean enabledRestriction;
    @Nullable
    private final SsoType ssoTypeRestriction;
    @Nullable
    private final Boolean includeCustomerLoginsRestriction;

    protected IdpSearchParameters(PageParameters pageParameters, @Nullable Boolean enabledRestriction, @Nullable SsoType ssoTypeRestriction, @Nullable Boolean includeCustomerLoginsRestriction) {
        this.pageParameters = Objects.requireNonNull(pageParameters);
        this.enabledRestriction = enabledRestriction;
        this.ssoTypeRestriction = ssoTypeRestriction;
        this.includeCustomerLoginsRestriction = includeCustomerLoginsRestriction;
    }

    public PageParameters getPageParameters() {
        return this.pageParameters;
    }

    public Optional<Boolean> getEnabledRestriction() {
        return Optional.ofNullable(this.enabledRestriction);
    }

    public Optional<SsoType> getSsoTypeRestriction() {
        return Optional.ofNullable(this.ssoTypeRestriction);
    }

    public Optional<Boolean> getIncludeCustomerLoginsRestriction() {
        return Optional.ofNullable(this.includeCustomerLoginsRestriction);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IdpSearchParameters that = (IdpSearchParameters)o;
        return Objects.equals(this.pageParameters, that.pageParameters) && Objects.equals(this.enabledRestriction, that.enabledRestriction) && this.ssoTypeRestriction == that.ssoTypeRestriction && Objects.equals(this.includeCustomerLoginsRestriction, that.includeCustomerLoginsRestriction);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.pageParameters, this.enabledRestriction, this.ssoTypeRestriction, this.includeCustomerLoginsRestriction});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("parameters", (Object)this.getPageParameters()).add("enabledFiltering", this.getEnabledRestriction()).add("ssoType", this.getSsoTypeRestriction()).add("includeCustomerLoginsRestriction", this.getIncludeCustomerLoginsRestriction()).toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(IdpSearchParameters data) {
        return new Builder(data);
    }

    public static IdpSearchParameters allEnabled() {
        return IdpSearchParameters.builder().setEnabledRestriction(true).build();
    }

    public static IdpSearchParameters allEnabledOfType(SsoType ssoType) {
        return IdpSearchParameters.builder().setEnabledRestriction(true).setSsoTypeRestriction(ssoType).build();
    }

    public static final class Builder {
        private PageParameters pageParameters = PageParameters.ALL_RESULTS;
        private Boolean enabledRestriction;
        private SsoType ssoTypeRestriction;
        private Boolean includeCustomerLoginsRestriction;

        private Builder() {
        }

        private Builder(IdpSearchParameters initialData) {
            this.pageParameters = initialData.getPageParameters();
            this.enabledRestriction = initialData.getEnabledRestriction().orElse(null);
            this.ssoTypeRestriction = initialData.getSsoTypeRestriction().orElse(null);
            this.includeCustomerLoginsRestriction = initialData.getIncludeCustomerLoginsRestriction().orElse(null);
        }

        public Builder setPageParameters(PageParameters pageParameters) {
            this.pageParameters = pageParameters;
            return this;
        }

        public Builder setEnabledRestriction(@Nullable Boolean enabledRestriction) {
            this.enabledRestriction = enabledRestriction;
            return this;
        }

        public Builder setSsoTypeRestriction(@Nullable SsoType ssoTypeRestriction) {
            this.ssoTypeRestriction = ssoTypeRestriction;
            return this;
        }

        public Builder setIncludeCustomerLoginsRestriction(@Nullable Boolean includeCustomerLoginsRestriction) {
            this.includeCustomerLoginsRestriction = includeCustomerLoginsRestriction;
            return this;
        }

        public IdpSearchParameters build() {
            return new IdpSearchParameters(this.pageParameters, this.enabledRestriction, this.ssoTypeRestriction, this.includeCustomerLoginsRestriction);
        }
    }
}

