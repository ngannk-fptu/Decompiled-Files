/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.base.Preconditions
 *  io.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.pages.Attachment;
import com.google.common.base.Preconditions;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class ReadThroughAttachmentDownloadPathCache {
    private final ReadThroughCache<AttachmentDownloadPathCacheKey, Option<String>> cache;

    ReadThroughAttachmentDownloadPathCache(ReadThroughCache<AttachmentDownloadPathCacheKey, Option<String>> cache) {
        this.cache = cache;
    }

    public static ReadThroughAttachmentDownloadPathCache create(CacheFactory cacheFactory) {
        return new ReadThroughAttachmentDownloadPathCache(ReadThroughAtlassianCache.create(cacheFactory, CoreCache.ATTACHMENT_DOWNLOAD_PATH_BY_CONTENT_ID_AND_FILENAME));
    }

    public AttachmentDownloadPathCacheKey toKey(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        Preconditions.checkArgument((container != null ? 1 : 0) != 0, (String)"Can't generate cache key for attachment with no content: %s", (Object)attachment);
        return this.toKey(container, attachment.getFileName());
    }

    public AttachmentDownloadPathCacheKey toKey(ContentEntityObject content, String attachmentFileName) {
        return new AttachmentDownloadPathCacheKey(content.getId(), attachmentFileName);
    }

    public @NonNull Optional<String> getAttachmentDownloadPath(AttachmentDownloadPathCacheKey key, Supplier<String> pathSupplier) {
        return this.cache.get(key, () -> Option.option((Object)((String)pathSupplier.get()))).toOptional();
    }

    public void remove(AttachmentDownloadPathCacheKey key) {
        this.cache.remove(key);
    }

    public static class AttachmentDownloadPathCacheKey
    implements Serializable {
        private final long contentId;
        private final String attachmentFileName;

        private AttachmentDownloadPathCacheKey(long contentId, String attachmentFileName) {
            this.contentId = contentId;
            this.attachmentFileName = attachmentFileName;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AttachmentDownloadPathCacheKey that = (AttachmentDownloadPathCacheKey)o;
            return this.contentId == that.contentId && Objects.equals(this.attachmentFileName, that.attachmentFileName);
        }

        public int hashCode() {
            return Objects.hash(this.contentId, this.attachmentFileName);
        }
    }
}

