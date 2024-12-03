/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageEffectsConfig;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.Supplier;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

class TransformContext {
    private final String imageLabel;
    private final long lastModified;
    private final Supplier<InputStream> imageSupplier;
    private final long imageDataSize;
    private final String cacheEntryName;
    private final boolean forbiddenAccess;
    private final ImageEffectsConfig config;
    private final EventPublisher eventPublisher;
    private final boolean rotationOnly;
    private final boolean rotationAndThumbnailOnly;
    private final Optional<Long> attachmentId;
    private final String attachmentContentType;
    private final String attachmentFilename;

    TransformContext(String imageLabel, long lastModified, Supplier<InputStream> imageSupplier, long imageDataSize, String cacheEntryName, boolean forbiddenAccess, ImageEffectsConfig config, EventPublisher eventPublisher, boolean rotationOnly, boolean rotationAndThumbnailOnly, Optional<Long> attachmentId, String attachmentContentType, String attachmentFilename) {
        this.imageLabel = Objects.requireNonNull(imageLabel);
        this.lastModified = lastModified;
        this.imageSupplier = Objects.requireNonNull(imageSupplier);
        this.imageDataSize = imageDataSize;
        this.cacheEntryName = Objects.requireNonNull(cacheEntryName);
        this.forbiddenAccess = forbiddenAccess;
        this.config = Objects.requireNonNull(config);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.rotationOnly = rotationOnly;
        this.rotationAndThumbnailOnly = rotationAndThumbnailOnly;
        this.attachmentId = attachmentId;
        this.attachmentContentType = attachmentContentType;
        this.attachmentFilename = attachmentFilename;
    }

    public String getImageLabel() {
        return this.imageLabel;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public Supplier<InputStream> getImageSupplier() {
        return this.imageSupplier;
    }

    public long getImageDataSize() {
        return this.imageDataSize;
    }

    public String getCacheEntryName() {
        return this.cacheEntryName;
    }

    public boolean isForbiddenAccess() {
        return this.forbiddenAccess;
    }

    public ImageEffectsConfig getConfig() {
        return this.config;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public boolean isRotationOnly() {
        return this.rotationOnly;
    }

    public boolean isRotationAndThumbnailOnly() {
        return this.rotationAndThumbnailOnly;
    }

    public Optional<Long> getAttachmentId() {
        return this.attachmentId;
    }

    public String getAttachmentContentType() {
        return this.attachmentContentType;
    }

    public String getAttachmentFilename() {
        return this.attachmentFilename;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TransformContext that = (TransformContext)o;
        if (this.lastModified != that.lastModified) {
            return false;
        }
        if (this.imageDataSize != that.imageDataSize) {
            return false;
        }
        if (this.forbiddenAccess != that.forbiddenAccess) {
            return false;
        }
        if (!Objects.equals(this.imageLabel, that.imageLabel)) {
            return false;
        }
        if (!Objects.equals(this.imageSupplier, that.imageSupplier)) {
            return false;
        }
        if (!Objects.equals(this.cacheEntryName, that.cacheEntryName)) {
            return false;
        }
        return Objects.equals(this.config, that.config);
    }

    public int hashCode() {
        int result = this.imageLabel != null ? this.imageLabel.hashCode() : 0;
        result = 31 * result + (int)(this.lastModified ^ this.lastModified >>> 32);
        result = 31 * result + (this.imageSupplier != null ? this.imageSupplier.hashCode() : 0);
        result = 31 * result + (int)(this.imageDataSize ^ this.imageDataSize >>> 32);
        result = 31 * result + (this.cacheEntryName != null ? this.cacheEntryName.hashCode() : 0);
        result = 31 * result + (this.forbiddenAccess ? 1 : 0);
        result = 31 * result + (this.config != null ? this.config.hashCode() : 0);
        return result;
    }
}

