/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.licensing;

import com.google.common.base.MoreObjects;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class ApplicationLicensedUser {
    private final Long id;
    private final String username;
    @Nullable
    private final Date lastActive;
    @Nullable
    private final String fullName;
    @Nullable
    private final String email;

    protected ApplicationLicensedUser(Long id, String username, @Nullable Date lastActive, @Nullable String fullName, @Nullable String email) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.lastActive = lastActive;
        this.fullName = fullName;
        this.email = email;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public Optional<Date> getLastActive() {
        return Optional.ofNullable(this.lastActive);
    }

    public Optional<String> getFullName() {
        return Optional.ofNullable(this.fullName);
    }

    public Optional<String> getEmail() {
        return Optional.ofNullable(this.email);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationLicensedUser data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationLicensedUser that = (ApplicationLicensedUser)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getUsername(), that.getUsername()) && Objects.equals(this.getLastActive(), that.getLastActive()) && Objects.equals(this.getFullName(), that.getFullName()) && Objects.equals(this.getEmail(), that.getEmail());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getUsername(), this.getLastActive(), this.getFullName(), this.getEmail());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("username", (Object)this.getUsername()).add("lastActive", this.getLastActive()).add("fullName", this.getFullName()).add("email", this.getEmail()).toString();
    }

    public static final class Builder {
        private Long id;
        private String username;
        private Date lastActive;
        private String fullName;
        private String email;

        private Builder() {
        }

        private Builder(ApplicationLicensedUser initialData) {
            this.id = initialData.getId();
            this.username = initialData.getUsername();
            this.lastActive = initialData.getLastActive().orElse(null);
            this.fullName = initialData.getFullName().orElse(null);
            this.email = initialData.getEmail().orElse(null);
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setLastActive(@Nullable Date lastActive) {
            this.lastActive = lastActive;
            return this;
        }

        public Builder setFullName(@Nullable String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder setEmail(@Nullable String email) {
            this.email = email;
            return this;
        }

        public ApplicationLicensedUser build() {
            return new ApplicationLicensedUser(this.id, this.username, this.lastActive, this.fullName, this.email);
        }
    }
}

