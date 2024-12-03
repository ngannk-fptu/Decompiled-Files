/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.pages.persistence.AbstractPageDaoInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.dao.support.DataAccessUtils;

public class HibernateAbstractPageDao
extends HibernateObjectDao
implements AbstractPageDaoInternal {
    private static final Date INLINE_TASK_RELEASE_DATE = new DateTime(2013, 9, 3, 0, 0).toDate();

    protected AbstractPage getByClassId(long id) {
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (!(ceo instanceof AbstractPage)) {
            return null;
        }
        return (AbstractPage)ceo;
    }

    @Override
    public AbstractPage getAbstractPageById(long id) {
        return this.getByClassId(id);
    }

    @Override
    public List<AbstractPage> getAbstractPageByIds(Iterable<Long> ids) {
        ImmutableList.Builder builder = ImmutableList.builder();
        Iterable partition = Iterables.partition(ids, (int)1000);
        for (List idList : partition) {
            List ceos = this.findNamedQueryStringParam("confluence.abstractpage_getByIds", "ids", idList);
            builder.addAll((Iterable)Collections2.filter((Collection)ceos, ceo -> ceo instanceof AbstractPage));
        }
        return builder.build();
    }

    @Override
    public List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long startContentId, long endContentId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getOrderedXhtmlContentInIdRange", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "startContentId", startContentId, "endContentId", endContentId);
    }

    @Override
    public long getHighestCeoId() {
        Long highestId = (Long)this.findNamedQuery("confluence.content_getHighestCeoId").get(0);
        return highestId == null ? 0L : highestId;
    }

    @Override
    public List<ContentEntityObject> getPreviousVersionsOfPageWithTaskId(long pageId, long taskId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getOlderPageVersionsWithTask", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "pageId", pageId, "taskIdXml", "%<ac:task-id>" + taskId + "</ac:task-id>%", "taskReleaseDate", INLINE_TASK_RELEASE_DATE);
    }

    @Override
    public int getCountOfLatestXhtmlContent(long endContentId) {
        return (Integer)this.findNamedQueryStringParam("confluence.content_getCountOfXhtmlContentBeforeId", "endContentId", endContentId).get(0);
    }

    @Override
    public int countStaleSharedDrafts() {
        return DataAccessUtils.intResult((Collection)this.findNamedQuery("confluence.content_countStaleSharedDrafts", HibernateObjectDao.Cacheability.NOT_CACHEABLE));
    }

    @Override
    public List<ContentEntityObject> getStaleSharedDrafts(LimitedRequest limitedRequest) {
        return this.getStaleSharedDraftsInternal(limitedRequest);
    }

    @Override
    public List<ContentEntityObject> getStaleSharedDrafts() {
        return this.getStaleSharedDrafts(LimitedRequestImpl.create((int)0));
    }

    private List<ContentEntityObject> getStaleSharedDraftsInternal(LimitedRequest limitedRequest) {
        if (limitedRequest.getLimit() > 0) {
            return this.findNamedQueryStringParams("confluence.content_findStaleSharedDrafts", HibernateObjectDao.Cacheability.NOT_CACHEABLE, limitedRequest.getStart(), limitedRequest.getLimit(), new Object[0]);
        }
        return this.findNamedQuery("confluence.content_findStaleSharedDrafts", HibernateObjectDao.Cacheability.NOT_CACHEABLE);
    }

    @Override
    public Class getPersistentClass() {
        return AbstractPage.class;
    }
}

