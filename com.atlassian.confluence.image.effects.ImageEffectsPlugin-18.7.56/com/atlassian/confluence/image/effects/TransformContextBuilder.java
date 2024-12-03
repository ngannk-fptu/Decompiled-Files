/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.image.effects;

import com.atlassian.confluence.image.effects.ImageEffectsConfig;
import com.atlassian.confluence.image.effects.TransformContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.Supplier;
import java.io.InputStream;
import java.util.Optional;

class TransformContextBuilder {
    private String imageLabel;
    private Long lastModified;
    private Supplier<InputStream> imageSupplier;
    private Long imageDataSize;
    private String cacheEntryName;
    private Boolean forbiddenAccess;
    private ImageEffectsConfig config;
    private EventPublisher eventPublisher;
    private boolean rotationOnly = false;
    private boolean rotationAndThumbnailOnly = false;
    private Optional<Long> attachmentId = Optional.empty();
    private String attachmentContentType;
    private String attachmentFilename;

    TransformContextBuilder() {
    }

    TransformContextBuilder imageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
        return this;
    }

    TransformContextBuilder lastModified(long lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    TransformContextBuilder imageSupplier(Supplier<InputStream> imageSupplier) {
        this.imageSupplier = imageSupplier;
        return this;
    }

    TransformContextBuilder cacheEntryName(String cacheEntryName) {
        this.cacheEntryName = cacheEntryName;
        return this;
    }

    TransformContextBuilder forbiddenAccess(boolean forbiddenAccess) {
        this.forbiddenAccess = forbiddenAccess;
        return this;
    }

    public TransformContextBuilder config(ImageEffectsConfig config) {
        this.config = config;
        return this;
    }

    TransformContextBuilder imageDataSize(long imageDataSize) {
        this.imageDataSize = imageDataSize;
        return this;
    }

    TransformContextBuilder eventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }

    TransformContextBuilder rotationOnly(boolean rotationOnly) {
        this.rotationOnly = rotationOnly;
        return this;
    }

    TransformContextBuilder rotationAndThumbnailOnly(boolean rotationAndThumbnailOnly) {
        this.rotationAndThumbnailOnly = rotationAndThumbnailOnly;
        return this;
    }

    TransformContextBuilder attachmentId(long attachmentId) {
        this.attachmentId = Optional.of(attachmentId);
        return this;
    }

    TransformContextBuilder attachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
        return this;
    }

    TransformContextBuilder attachmentFilename(String attachmentFilename) {
        this.attachmentFilename = attachmentFilename;
        return this;
    }

    TransformContext build() {
        return new TransformContext(this.imageLabel, this.lastModified, this.imageSupplier, this.imageDataSize, this.cacheEntryName, this.forbiddenAccess, this.config, this.eventPublisher, this.rotationOnly, this.rotationAndThumbnailOnly, this.attachmentId, this.attachmentContentType, this.attachmentFilename);
    }
}

