/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.binary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public final class BinaryDownloadOptions {
    private final String mediaType;
    private final String characterEncoding;
    private final String fileName;
    private final String dispositionType;
    private boolean downloadDomainIgnored = false;
    public static final BinaryDownloadOptions DEFAULT = BinaryDownloadOptions.builder().build();

    private BinaryDownloadOptions(String mediaType, String characterEncoding, String fileName, String dispositionType, boolean downloadDomainIgnored) {
        this.mediaType = mediaType;
        this.characterEncoding = characterEncoding;
        this.fileName = fileName;
        this.dispositionType = dispositionType;
        this.downloadDomainIgnored = downloadDomainIgnored;
    }

    @Nullable
    public final String getMediaType() {
        return this.mediaType;
    }

    @Nullable
    public final String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Nullable
    public final String getFileName() {
        return this.fileName;
    }

    @NotNull
    public final String getDispositionType() {
        return this.dispositionType;
    }

    public boolean isDownloadDomainIgnored() {
        return this.downloadDomainIgnored;
    }

    @NotNull
    public static BinaryDownloadOptionsBuilder builder() {
        return new BinaryDownloadOptionsBuilder();
    }

    public static final class BinaryDownloadOptionsBuilder {
        private String mediaType = null;
        private String characterEncoding = null;
        private String fileName = null;
        private DispositionType dispositionType = DispositionType.INLINE;
        private boolean domainOverrideIgnored = false;

        private BinaryDownloadOptionsBuilder() {
        }

        @NotNull
        public BinaryDownloadOptionsBuilder withMediaType(@NotNull String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        @NotNull
        public BinaryDownloadOptionsBuilder withCharacterEncoding(@NotNull String characterEncoding) {
            this.characterEncoding = characterEncoding;
            return this;
        }

        @NotNull
        public BinaryDownloadOptionsBuilder withFileName(@NotNull String fileName) {
            this.fileName = fileName;
            return this;
        }

        @NotNull
        public BinaryDownloadOptionsBuilder withDispositionTypeInline() {
            this.dispositionType = DispositionType.INLINE;
            return this;
        }

        @NotNull
        public BinaryDownloadOptionsBuilder withDispositionTypeAttachment() {
            this.dispositionType = DispositionType.ATTACHMENT;
            return this;
        }

        public BinaryDownloadOptionsBuilder withDomainOverrideIgnored(boolean domainOverrideIgnored) {
            this.domainOverrideIgnored = domainOverrideIgnored;
            return this;
        }

        @NotNull
        public BinaryDownloadOptions build() {
            return new BinaryDownloadOptions(this.mediaType, this.characterEncoding, this.fileName, null != this.dispositionType ? this.dispositionType.toString() : DispositionType.INLINE.toString(), this.domainOverrideIgnored);
        }

        private static enum DispositionType {
            INLINE("inline"),
            ATTACHMENT("attachment");

            private final String value;

            private DispositionType(String value) {
                this.value = value;
            }

            public String toString() {
                return this.value;
            }
        }
    }
}

