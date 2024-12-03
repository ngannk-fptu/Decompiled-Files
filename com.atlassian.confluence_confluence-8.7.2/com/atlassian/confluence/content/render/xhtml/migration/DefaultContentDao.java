/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.internal.content.render.xhtml.migration.ContentDaoInternal;
import com.atlassian.confluence.internal.persistence.hibernate.AbstractContentEntityObjectHibernateDao;
import com.atlassian.core.bean.EntityObject;
import java.util.List;

public class DefaultContentDao
extends AbstractContentEntityObjectHibernateDao<ContentEntityObject>
implements ContentDaoInternal {
    @Override
    public Class<ContentEntityObject> getPersistentClass() {
        return ContentEntityObject.class;
    }

    @Override
    public int getLatestContentCount() {
        List countList = this.findNamedQuery("confluence.content_getContentForXhtmlConversionCount");
        return (Integer)countList.get(0);
    }

    @Override
    public List<ContentEntityObject> getLatestOrderedWikiContentFromContentId(long startContentId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getContentForXhtmlConversionWithStartContentId", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "startContentId", startContentId);
    }

    @Override
    public List<ContentEntityObject> getOrderedXhtmlContentFromContentId(long startContentId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getOrderedXhtmlContentFromId", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "startContentId", startContentId);
    }

    @Override
    public List<ContentEntityObject> getLatestOrderedXhtmlContentFromContentIds(long startContentId, long endContentId) {
        return this.findNamedQueryStringParams("confluence.content_getLatestOrderedXhtmlContentFromIds", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, -1, "startContentId", startContentId, "endContentId", endContentId);
    }

    @Override
    public List<Long> getLatestOrderedXhtmlContentIds(long startContentId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getLatestOrderedXhtmlContentIds", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "startContentId", startContentId);
    }

    @Override
    public int getCountOfXhtmlContent() {
        return (Integer)this.findNamedQuery("confluence.content_getCountOfXhtmlContent").get(0);
    }

    @Override
    public int getCountOfLatestXhtmlContent() {
        return (Integer)this.findNamedQuery("confluence.content_getCountOfLatestXhtmlContent").get(0);
    }

    @Override
    public void saveRawWithoutReindex(EntityObject objectToSave) {
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
    }

    @Override
    public List<ContentEntityObject> getXhtmlSpaceDescriptionsFromContentId(long startContentId, int maxRows) {
        return this.findNamedQueryStringParams("confluence.content_getXhtmlSpaceDescriptionsFromId", HibernateObjectDao.Cacheability.NOT_CACHEABLE, 0, maxRows, "startContentId", startContentId);
    }

    @Override
    public int getCountOfXhtmlSpaceDescriptions() {
        List countList = this.findNamedQuery("confluence.content_getCountOfXhtmlSpaceDescriptions");
        return (Integer)countList.get(0);
    }

    @Override
    protected void updateModificationData(EntityObject objectToSave) {
        if (objectToSave.getCreationDate() == null) {
            objectToSave.setCreationDate(objectToSave.getCurrentDate());
        }
    }

    @Override
    protected void updateEntityModificationData(ContentEntityObject objectToSave) {
        this.updateModificationData(objectToSave);
    }
}

