/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.image.MemoryAwareImageRenderPredicate;
import com.atlassian.confluence.content.render.image.SimpleImageSizeRenderPredicate;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumbnailRenderer;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailRenderException;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatingThumbnailRenderer {
    private static final Logger log = LoggerFactory.getLogger(DelegatingThumbnailRenderer.class);
    private final AttachmentManager attachmentManager;
    private final ThumbnailRenderer thumbnailRenderer;

    public DelegatingThumbnailRenderer(Thumber thumber, AttachmentManager attachmentManager) {
        this(thumber, attachmentManager, new MemoryAwareImageRenderPredicate());
    }

    public DelegatingThumbnailRenderer(Thumber thumber, AttachmentManager attachmentManager, int rasterSizeThresholdPx) {
        this(thumber, attachmentManager, new SimpleImageSizeRenderPredicate(rasterSizeThresholdPx));
    }

    public DelegatingThumbnailRenderer(Thumber thumber, AttachmentManager attachmentManager, Predicate<ImageDimensions> rasterBasedRenderingThreshold) {
        this.attachmentManager = Objects.requireNonNull(attachmentManager);
        this.thumbnailRenderer = new ThumbnailRenderer(Objects.requireNonNull(thumber), rasterBasedRenderingThreshold);
    }

    private Thumbnail retrieveIfExists(File thumbnailFile, Attachment attachment) {
        try {
            if (thumbnailFile.exists()) {
                log.debug("Thumbnail file '{}' already exists. Returning existing thumbnail.", (Object)thumbnailFile);
                ImageDimensions size = ThumbnailRenderer.dimensions(thumbnailFile);
                if (size != null) {
                    return new Thumbnail(size.getHeight(), size.getWidth(), attachment.getFileName(), attachment.getId(), Thumbnail.MimeType.PNG);
                }
                log.info("Thumbnail size couldnt be calculated for file '{}'.", (Object)thumbnailFile);
            }
        }
        catch (ThumbnailRenderException ex) {
            log.warn("Could not retrieve existing thumbnail from " + thumbnailFile.getAbsolutePath() + " will attempt to recreate thumbnail, for more info set log : " + log.getName() + " to INFO level");
            log.info("More info: ", (Throwable)ex);
        }
        return null;
    }

    public Thumbnail retrieveOrCreateThumbNail(@NonNull Attachment attachment, FilesystemPath thumbnailFile, int maxWidth, int maxHeight) throws ThumbnailRenderException {
        return this.retrieveOrCreateThumbNail(attachment, thumbnailFile.asJavaFile(), maxWidth, maxHeight);
    }

    public Thumbnail retrieveOrCreateThumbNail(@NonNull Attachment attachment, File thumbnailFile, int maxWidth, int maxHeight) throws ThumbnailRenderException {
        Thumbnail thumbnail;
        block11: {
            log.debug("Creating thumbnail for {} using {}", (Object)attachment, (Object)thumbnailFile);
            if (attachment == null) {
                throw new IllegalArgumentException("Attachment cannot be null. File = " + thumbnailFile);
            }
            if (attachment.isDeleted()) {
                throw new IllegalArgumentException("Attachment " + attachment.toString() + " is trashed");
            }
            Thumbnail existing = this.retrieveIfExists(thumbnailFile, attachment);
            if (existing != null) {
                return existing;
            }
            InputStream attachmentData = this.attachmentManager.getAttachmentData(attachment);
            try {
                Thumbnail thumbnail2 = this.thumbnailRenderer.createThumbnail(attachmentData, thumbnailFile, maxWidth, maxHeight);
                thumbnail = new Thumbnail(thumbnail2.getHeight(), thumbnail2.getWidth(), attachment.getFileName(), attachment.getId(), thumbnail2.getMimeType());
                if (attachmentData == null) break block11;
            }
            catch (Throwable throwable) {
                try {
                    if (attachmentData != null) {
                        try {
                            attachmentData.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException ex) {
                    throw new ThumbnailRenderException(ex);
                }
            }
            attachmentData.close();
        }
        return thumbnail;
    }
}

