/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventPublisher
 *  org.hibernate.SessionFactory
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.cache.ReadThroughEntityCache;
import com.atlassian.confluence.impl.pages.attachments.ReadThroughCachingAttachmentDao;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDaoFactory;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDataDao;
import com.atlassian.confluence.pages.persistence.dao.hibernate.AbstractHibernateAttachmentDao;
import com.atlassian.event.api.EventPublisher;
import org.hibernate.SessionFactory;

public class HibernateAttachmentDaoFactoryBean
implements AttachmentDaoFactory {
    private final SessionFactory sessionFactory;
    private final EventPublisher eventPublisher;
    private final CacheFactory cacheFactory;

    public HibernateAttachmentDaoFactoryBean(SessionFactory sessionFactory, EventPublisher eventPublisher, CacheFactory cacheFactory) {
        this.sessionFactory = sessionFactory;
        this.eventPublisher = eventPublisher;
        this.cacheFactory = cacheFactory;
    }

    @Override
    public AttachmentDao getInstance(AttachmentDataDao dataDao) {
        AttachmentDaoInternal dao = this.getUnderlyingDao(dataDao);
        if (this.cacheFactory != null) {
            return new ReadThroughCachingAttachmentDao(dao, this.createReadThroughCache(dao, CoreCache.ATTACHMENT_ID_BY_CONTENT_ID_AND_FILENAME));
        }
        return dao;
    }

    private ReadThroughCache<String, Attachment> createReadThroughCache(AttachmentDaoInternal dao, CoreCache cacheName) {
        return ReadThroughEntityCache.forConfluenceEntityObjects(ReadThroughAtlassianCache.create(this.cacheFactory, cacheName), dao::getById);
    }

    private AttachmentDaoInternal getUnderlyingDao(AttachmentDataDao dataDao) {
        AbstractHibernateAttachmentDao dao = (AbstractHibernateAttachmentDao)AbstractHibernateAttachmentDao.getInstance(dataDao);
        dao.setSessionFactory(this.sessionFactory);
        if (this.eventPublisher != null) {
            dao.setEventPublisher(this.eventPublisher);
        }
        return dao;
    }
}

