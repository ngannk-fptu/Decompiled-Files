/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.impl.retention.ContentRetentionDao;
import com.atlassian.confluence.impl.retention.rules.HistoricalVersion;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class DefaultContentRetentionDao
implements ContentRetentionDao {
    static final String SELECT_HISTORICAL_PAGE_VERSIONS = "select new com.atlassian.confluence.impl.retention.rules.HistoricalVersion(page.id, page.originalVersion.id, page.originalVersion.space.id, page.version, page.lastModificationDate, 'PAGE') from Page page where page.originalVersion.id >= :startOriginalId and page.contentStatus = 'current' and page.lastModificationDate IS NOT NULL order by page.originalVersion.id asc, page.version desc";
    static final String SELECT_HISTORICAL_ATTACHMENT_VERSIONS = "select new com.atlassian.confluence.impl.retention.rules.HistoricalVersion(attachment.id, attachment.originalVersion.id, attachment.originalVersion.space.id, attachment.version, attachment.lastModificationDate, 'ATTACHMENT') from Attachment attachment where attachment.originalVersion.id >= :startOriginalId and attachment.contentStatus = 'current' and attachment.lastModificationDate IS NOT NULL order by attachment.originalVersion.id asc, attachment.version desc";
    private final SessionFactory sessionFactory;

    public DefaultContentRetentionDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<HistoricalVersion> findHistoricalPageVersions(long startOriginalId, int limit) {
        Query query = this.sessionFactory.getCurrentSession().createQuery(SELECT_HISTORICAL_PAGE_VERSIONS, HistoricalVersion.class);
        query.setParameter("startOriginalId", (Object)startOriginalId);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<HistoricalVersion> findHistoricalAttachmentVersions(long startOriginalId, int limit) {
        Query query = this.sessionFactory.getCurrentSession().createQuery(SELECT_HISTORICAL_ATTACHMENT_VERSIONS, HistoricalVersion.class);
        query.setParameter("startOriginalId", (Object)startOriginalId);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}

