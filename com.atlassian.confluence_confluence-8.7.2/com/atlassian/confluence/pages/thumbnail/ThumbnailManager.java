/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumbnail
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.core.util.thumbnail.Thumbnail;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ThumbnailManager {
    @Deprecated
    public static final String THUMBNAIL_PATH_SUFFIX = File.separator + "thumbnails" + File.separator;
    public static final String THUMBNAIL_IMAGE_DARK_FEATURE = "thumbnail-image";

    public boolean isThumbnailable(@Nullable Attachment var1);

    public InputStream getThumbnailData(@NonNull Attachment var1) throws FileNotFoundException;

    public InputStream getThumbnailData(@NonNull Attachment var1, @Nullable ImageDimensions var2) throws FileNotFoundException;

    @Deprecated
    public File getThumbnailFile(long var1, int var3, long var4);

    @Deprecated
    public File getThumbnailFile(@NonNull Attachment var1);

    @Deprecated
    public File getThumbnailFile(@NonNull Attachment var1, @Nullable ImageDimensions var2);

    public boolean removeThumbnail(@NonNull Attachment var1);

    public Thumbnail getThumbnail(@NonNull Attachment var1) throws IllegalArgumentException;

    public Thumbnail getThumbnail(@NonNull Attachment var1, @Nullable ImageDimensions var2) throws IllegalArgumentException;

    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment var1) throws CannotGenerateThumbnailException;

    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment var1, @Nullable ImageDimensions var2) throws CannotGenerateThumbnailException;

    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment var1, @Nullable String var2) throws CannotGenerateThumbnailException;

    public ThumbnailInfo getThumbnailInfo(@NonNull Attachment var1, @Nullable String var2, @Nullable ImageDimensions var3) throws CannotGenerateThumbnailException;

    public boolean isThumbnailable(@Nullable ThumbnailInfo var1);
}

