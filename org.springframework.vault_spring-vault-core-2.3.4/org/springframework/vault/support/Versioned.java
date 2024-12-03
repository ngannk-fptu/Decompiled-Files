/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.vault.support;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Versioned<T> {
    @Nullable
    private final T data;
    private final Version version;
    @Nullable
    private final Metadata metadata;

    private Versioned(T data, Version version) {
        this.version = version;
        this.metadata = null;
        this.data = data;
    }

    private Versioned(@Nullable T data, Version version, Metadata metadata) {
        this.version = version;
        this.metadata = metadata;
        this.data = data;
    }

    public static <T> Versioned<T> create(T secret) {
        Assert.notNull(secret, (String)"Versioned data must not be null");
        return new Versioned<T>(secret, Version.unversioned());
    }

    public static <T> Versioned<T> create(@Nullable T secret, Version version) {
        Assert.notNull((Object)version, (String)"Version must not be null");
        return new Versioned<T>(secret, version);
    }

    public static <T> Versioned<T> create(@Nullable T secret, Metadata metadata) {
        Assert.notNull((Object)metadata, (String)"Metadata must not be null");
        return new Versioned<T>(secret, metadata.getVersion(), metadata);
    }

    public Version getVersion() {
        return this.version;
    }

    public boolean hasMetadata() {
        return this.metadata != null;
    }

    @Nullable
    public Metadata getMetadata() {
        return this.metadata;
    }

    public Metadata getRequiredMetadata() {
        Metadata metadata = this.metadata;
        if (metadata == null) {
            throw new IllegalStateException("Required Metadata is not present");
        }
        return metadata;
    }

    public boolean hasData() {
        return this.data != null;
    }

    @Nullable
    public T getData() {
        return this.data;
    }

    public T getRequiredData() {
        T data = this.data;
        if (data == null) {
            throw new IllegalStateException("Required data is not present");
        }
        return data;
    }

    public Optional<T> toOptional() {
        return Optional.ofNullable(this.data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Versioned)) {
            return false;
        }
        Versioned versioned = (Versioned)o;
        return Objects.equals(this.data, versioned.data) && Objects.equals(this.version, versioned.version) && Objects.equals(this.metadata, versioned.metadata);
    }

    public int hashCode() {
        return Objects.hash(this.data, this.version, this.metadata);
    }

    public static class Version {
        static final Version UNVERSIONED = new Version(0);
        private final int version;

        private Version(int version) {
            this.version = version;
        }

        public static Version unversioned() {
            return UNVERSIONED;
        }

        public static Version from(int versionNumber) {
            if (versionNumber > 0) {
                return new Version(versionNumber);
            }
            return UNVERSIONED;
        }

        public boolean isVersioned() {
            return this.version > 0;
        }

        public int getVersion() {
            return this.version;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Version)) {
                return false;
            }
            Version version1 = (Version)o;
            return this.version == version1.version;
        }

        public int hashCode() {
            return Objects.hash(this.version);
        }

        public String toString() {
            return String.format("Version[%d]", this.version);
        }
    }

    public static class Metadata {
        private final Instant createdAt;
        @Nullable
        private final Instant deletedAt;
        private final boolean destroyed;
        private final Version version;

        private Metadata(Instant createdAt, @Nullable Instant deletedAt, boolean destroyed, Version version) {
            this.createdAt = createdAt;
            this.deletedAt = deletedAt;
            this.destroyed = destroyed;
            this.version = version;
        }

        public static MetadataBuilder builder() {
            return new MetadataBuilder();
        }

        public Instant getCreatedAt() {
            return this.createdAt;
        }

        public boolean isDeleted() {
            return this.deletedAt != null;
        }

        @Nullable
        public Instant getDeletedAt() {
            return this.deletedAt;
        }

        public Version getVersion() {
            return this.version;
        }

        public boolean isDestroyed() {
            return this.destroyed;
        }

        public String toString() {
            return this.getClass().getSimpleName() + " [createdAt=" + this.createdAt + ", deletedAt=" + this.deletedAt + ", destroyed=" + this.destroyed + ", version=" + this.version + ']';
        }

        public static class MetadataBuilder {
            @Nullable
            private Instant createdAt;
            @Nullable
            private Instant deletedAt;
            private boolean destroyed;
            @Nullable
            private Version version;

            private MetadataBuilder() {
            }

            public MetadataBuilder createdAt(Instant createdAt) {
                Assert.notNull((Object)createdAt, (String)"Created at must not be null");
                this.createdAt = createdAt;
                return this;
            }

            public MetadataBuilder deletedAt(Instant deletedAt) {
                this.deletedAt = deletedAt;
                return this;
            }

            public MetadataBuilder destroyed() {
                return this.destroyed(true);
            }

            public MetadataBuilder destroyed(boolean destroyed) {
                this.destroyed = destroyed;
                return this;
            }

            public MetadataBuilder version(Version version) {
                Assert.notNull((Object)version, (String)"Version must not be null");
                this.version = version;
                return this;
            }

            public Metadata build() {
                Assert.notNull((Object)this.createdAt, (String)"CreatedAt must not be null");
                Assert.notNull((Object)this.version, (String)"Version must not be null");
                return new Metadata(this.createdAt, this.deletedAt, this.destroyed, this.version);
            }
        }
    }
}

