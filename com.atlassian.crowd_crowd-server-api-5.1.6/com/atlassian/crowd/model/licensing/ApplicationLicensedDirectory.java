/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.licensing;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;

public class ApplicationLicensedDirectory {
    private final String id;
    @Nullable
    private final String name;
    private final boolean isCrowdDirectory;
    @Nullable
    private final Long crowdDirectoryId;

    protected ApplicationLicensedDirectory(String id, @Nullable String name, boolean isCrowdDirectory, @Nullable Long crowdDirectoryId) {
        this.id = Objects.requireNonNull(id);
        this.name = name;
        this.isCrowdDirectory = isCrowdDirectory;
        this.crowdDirectoryId = crowdDirectoryId;
    }

    public String getId() {
        return this.id;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public boolean getIsCrowdDirectory() {
        return this.isCrowdDirectory;
    }

    public Optional<Long> getCrowdDirectoryId() {
        return Optional.ofNullable(this.crowdDirectoryId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationLicensedDirectory data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationLicensedDirectory that = (ApplicationLicensedDirectory)o;
        return Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.getIsCrowdDirectory(), that.getIsCrowdDirectory()) && Objects.equals(this.getCrowdDirectoryId(), that.getCrowdDirectoryId());
    }

    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getIsCrowdDirectory(), this.getCrowdDirectoryId());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.getId()).add("name", this.getName()).add("isCrowdDirectory", this.getIsCrowdDirectory()).add("crowdDirectoryId", this.getCrowdDirectoryId()).toString();
    }

    public static final class Builder {
        private String id;
        private String name;
        private boolean isCrowdDirectory;
        private Long crowdDirectoryId;

        private Builder() {
        }

        private Builder(ApplicationLicensedDirectory initialData) {
            this.id = initialData.getId();
            this.name = initialData.getName().orElse(null);
            this.isCrowdDirectory = initialData.getIsCrowdDirectory();
            this.crowdDirectoryId = initialData.getCrowdDirectoryId().orElse(null);
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(@Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder setIsCrowdDirectory(boolean isCrowdDirectory) {
            this.isCrowdDirectory = isCrowdDirectory;
            return this;
        }

        public Builder setCrowdDirectoryId(@Nullable Long crowdDirectoryId) {
            this.crowdDirectoryId = crowdDirectoryId;
            return this;
        }

        public ApplicationLicensedDirectory build() {
            return new ApplicationLicensedDirectory(this.id, this.name, this.isCrowdDirectory, this.crowdDirectoryId);
        }
    }
}

