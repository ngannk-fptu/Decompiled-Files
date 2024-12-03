/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.pages.thumbnail;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ThumbnailManagerInternal
extends ThumbnailManager {
    public static final String THUMBNAIL_DIR_NAME = "thumbnails";

    @Override
    @Deprecated
    default public File getThumbnailFile(long attachmentId, int version, long contentId) {
        return this.getThumbnailPath(attachmentId, version, contentId).asJavaFile();
    }

    @Override
    @Deprecated
    default public File getThumbnailFile(@NonNull Attachment attachment, @Nullable ImageDimensions imageDimensions) {
        return this.getThumbnailPath(attachment, imageDimensions).asJavaFile();
    }

    @Override
    @Deprecated
    default public File getThumbnailFile(@NonNull Attachment attachment) {
        return this.getThumbnailPath(attachment, null).asJavaFile();
    }

    public FilesystemPath getThumbnailPath(long var1, int var3, long var4);

    public FilesystemPath getThumbnailPath(@NonNull Attachment var1, @Nullable ImageDimensions var2);
}

