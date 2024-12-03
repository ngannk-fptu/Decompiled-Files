/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.binary;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public final class BinaryUploadOptions {
    private final boolean domainOverrideIgnore;
    public static final BinaryUploadOptions DEFAULT = BinaryUploadOptions.builder().build();

    private BinaryUploadOptions(boolean domainOverrideIgnore) {
        this.domainOverrideIgnore = domainOverrideIgnore;
    }

    public boolean isDomainOverrideIgnored() {
        return this.domainOverrideIgnore;
    }

    @NotNull
    public static BinaryUploadOptionsBuilder builder() {
        return new BinaryUploadOptionsBuilder();
    }

    public static final class BinaryUploadOptionsBuilder {
        private boolean domainOverrideIgnore = false;

        private BinaryUploadOptionsBuilder() {
        }

        public BinaryUploadOptionsBuilder withDomainOverrideIgnore(boolean domainOverrideIgnore) {
            this.domainOverrideIgnore = domainOverrideIgnore;
            return this;
        }

        public BinaryUploadOptions build() {
            return new BinaryUploadOptions(this.domainOverrideIgnore);
        }
    }
}

