/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.impl.pages.attachments;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.AbstractDelegatingAttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.FlushableCachingDao;
import com.google.common.base.Preconditions;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ReadThroughCachingAttachmentDao
extends AbstractDelegatingAttachmentDao
implements FlushableCachingDao {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughCachingAttachmentDao.class);
    private final ReadThroughCache<String, Attachment> cache;

    public ReadThroughCachingAttachmentDao(AttachmentDaoInternal delegate, ReadThroughCache<String, Attachment> cache) {
        super(delegate);
        this.cache = cache;
    }

    @Override
    public Attachment getLatestAttachment(ContentEntityObject content, String fileName) {
        Assert.notNull((Object)content, (String)"Content must not be null");
        Assert.notNull((Object)fileName, (String)"File name must not be null");
        return this.cache.get(ReadThroughCachingAttachmentDao.cacheKey(content.getId(), fileName), () -> super.getLatestAttachment(content, fileName));
    }

    @Override
    public void removeAttachmentFromServer(Attachment attachment) {
        this.removeAttachmentFromCache(attachment);
        super.removeAttachmentFromServer(attachment);
    }

    @Override
    public List<Attachment> removeAllVersionsFromServer(Attachment attachment) {
        this.removeAttachmentFromCache(attachment);
        return super.removeAllVersionsFromServer(attachment);
    }

    @Override
    public void removeAttachmentVersionFromServer(Attachment attachment) {
        this.removeAttachmentFromCache(attachment);
        super.removeAttachmentVersionFromServer(attachment);
    }

    @Override
    public void flush() {
        if (log.isDebugEnabled()) {
            log.debug("Flushing attachment ID cache");
        }
        this.cache.removeAll();
        if (this.getDelegate() instanceof FlushableCachingDao) {
            ((FlushableCachingDao)((Object)this.getDelegate())).flush();
        }
    }

    @Override
    public void moveAttachment(Attachment attachment, Attachment oldAttachment, ContentEntityObject newContent) {
        super.moveAttachment(attachment, oldAttachment, newContent);
        this.cache.remove(ReadThroughCachingAttachmentDao.cacheKey(oldAttachment));
    }

    private void removeAttachmentFromCache(Attachment attachment) {
        if (log.isDebugEnabled()) {
            log.debug("Remove attachment ID from cache: " + attachment);
        }
        this.cache.remove(ReadThroughCachingAttachmentDao.cacheKey(attachment));
    }

    private static String cacheKey(long contentId, String fileName) {
        return String.format("%s-%s", contentId, fileName.toLowerCase());
    }

    private static String cacheKey(Attachment attachment) {
        ContentEntityObject container = attachment.getContainer();
        Preconditions.checkArgument((container != null ? 1 : 0) != 0, (String)"Can't generate cache key for attachment with no content: %s", (Object)attachment);
        return ReadThroughCachingAttachmentDao.cacheKey(container.getId(), attachment.getFileName());
    }
}

