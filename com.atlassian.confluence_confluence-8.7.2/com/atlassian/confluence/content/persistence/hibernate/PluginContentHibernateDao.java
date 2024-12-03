/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.plugin.PluginAccessor
 *  com.google.common.base.Preconditions
 *  javax.persistence.EntityManager
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.query.Query
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.support.DataAccessUtils
 */
package com.atlassian.confluence.content.persistence.hibernate;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.persistence.CustomContentDao;
import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryFactory;
import com.atlassian.confluence.content.persistence.hibernate.HibernateContentQueryModuleDescriptor;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.internal.persistence.hibernate.AbstractContentEntityObjectHibernateDao;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.plugin.PluginAccessor;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.support.DataAccessUtils;

public class PluginContentHibernateDao
extends AbstractContentEntityObjectHibernateDao<CustomContentEntityObject>
implements CustomContentDao,
InitializingBean {
    private PluginAccessor pluginAccessor;
    private HibernateDatabaseCapabilities databaseCapabilities;

    @Override
    public Class<CustomContentEntityObject> getPersistentClass() {
        return CustomContentEntityObject.class;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setDatabaseCapabilities(HibernateDatabaseCapabilities databaseCapabilities) {
        this.databaseCapabilities = databaseCapabilities;
    }

    @Override
    public <T> Iterator<T> findByQuery(ContentQuery<T> contentQuery, int offset, int maxResults) {
        return this.query(contentQuery, offset, maxResults, this::createIteratorFromQuery);
    }

    @Override
    public <T> List<T> queryForList(ContentQuery<T> contentQuery, int offset, int maxResults) {
        return this.query(contentQuery, offset, maxResults, Query::list);
    }

    public <T, R> R query(ContentQuery<T> contentQuery, int offset, int maxResults, Function<Query<T>, R> f) {
        HibernateContentQueryModuleDescriptor descriptor = this.findQueryModuleByName(contentQuery.getName());
        return (R)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Query query = PluginContentHibernateDao.getQuery(descriptor.getModule(), session, contentQuery.getParameters());
            query.setFirstResult(offset);
            query.setMaxResults(maxResults);
            return f.apply(query);
        });
    }

    @Override
    public <T> List<T> queryForList(ContentQuery<T> contentQuery) {
        HibernateContentQueryModuleDescriptor descriptor = this.findQueryModuleByName(contentQuery.getName());
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Query query = PluginContentHibernateDao.getQuery(descriptor.getModule(), session, contentQuery.getParameters());
            return query.list();
        });
    }

    @Override
    public <T> List<T> findByQuery(ContentQuery<T> contentQuery, boolean cacheable, LimitedRequest limitedRequest) {
        int offset = limitedRequest.getStart();
        int maxResultCount = limitedRequest.getLimit() + 1;
        HibernateContentQueryModuleDescriptor descriptor = this.findQueryModuleByName(contentQuery.getName());
        return (List)this.getHibernateTemplate().executeWithNativeSession(session -> {
            Query query = PluginContentHibernateDao.getQuery(descriptor.getModule(), session, contentQuery.getParameters());
            query.setFirstResult(offset);
            query.setMaxResults(maxResultCount);
            return query.list();
        });
    }

    @Override
    public int findTotalInSpace(long spaceId, String pluginModuleKey) {
        return DataAccessUtils.intResult((Collection)((Collection)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select count(*) from CustomContentEntityObject content where content.space.id = :spaceId and content.contentStatus = 'current' and content.pluginModuleKey = :pluginModuleKey and content.originalVersion is null");
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            return query.list();
        })));
    }

    @Override
    public Iterator<CustomContentEntityObject> findCurrentInSpace(long spaceId, String pluginModuleKey, int offset, int maxResults, CustomContentManager.SortField sortField, CustomContentManager.SortOrder sortOrder) {
        Iterator currentContent = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.space.id = :spaceId and content.contentStatus = 'current' and content.pluginModuleKey = :pluginModuleKey and content.originalVersion is null " + this.makeOrderBy("content", sortField, sortOrder));
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            query.setFirstResult(offset);
            query.setMaxResults(maxResults);
            return query.list().iterator();
        });
        Preconditions.checkState((currentContent != null ? 1 : 0) != 0);
        return currentContent;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllInSpaceWithAttachments(String pluginModuleKey, long spaceId) {
        Iterator contentWithAttachments = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select distinct content from CustomContentEntityObject content join content.attachments attachment where content.space.id = :spaceId and content.pluginModuleKey = :pluginModuleKey");
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            return this.createIteratorFromQuery(query);
        });
        Preconditions.checkState((contentWithAttachments != null ? 1 : 0) != 0);
        return contentWithAttachments;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllInSpace(String pluginModuleKey, long spaceId) {
        Iterator content = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.space.id = :spaceId and content.pluginModuleKey = :pluginModuleKey order by content.creationDate desc");
            query.setParameter("spaceId", (Object)spaceId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            return query.list().iterator();
        });
        Preconditions.checkState((content != null ? 1 : 0) != 0);
        return content;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllInSpaceWithAttachments(long spaceId) {
        Iterator contentWithAttachments = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select distinct content from CustomContentEntityObject content join content.attachments attachment where content.space.id = :spaceId ");
            query.setParameter("spaceId", (Object)spaceId);
            return this.createIteratorFromQuery(query);
        });
        Preconditions.checkState((contentWithAttachments != null ? 1 : 0) != 0);
        return contentWithAttachments;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllInSpace(long spaceId) {
        Iterator content = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.space.id = :spaceId order by content.creationDate desc");
            query.setParameter("spaceId", (Object)spaceId);
            return query.list().iterator();
        });
        Preconditions.checkState((content != null ? 1 : 0) != 0);
        return content;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAll(String pluginContentKey) {
        Iterator content = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.pluginModuleKey = :pluginModuleKey order by content.creationDate desc, content.id desc");
            query.setParameter("pluginModuleKey", (Object)pluginContentKey);
            return query.list().iterator();
        });
        Preconditions.checkState((content != null ? 1 : 0) != 0);
        return content;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllWithAttachments(String pluginContentKey) {
        Iterator contentWithAttachments = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select distinct content from CustomContentEntityObject content join content.attachments attachment where content.pluginModuleKey = :pluginModuleKey ");
            query.setParameter("pluginModuleKey", (Object)pluginContentKey);
            return this.createIteratorFromQuery(query);
        });
        Preconditions.checkState((contentWithAttachments != null ? 1 : 0) != 0);
        return contentWithAttachments;
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllChildren(long parentId) {
        Iterator children = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.parentContent.id = :parentId ");
            query.setParameter("parentId", (Object)parentId);
            return query.list().iterator();
        });
        Preconditions.checkState((children != null ? 1 : 0) != 0);
        return children;
    }

    @Override
    public Iterator<CustomContentEntityObject> findChildrenOfType(long parentId, String pluginModuleKey, int offset, int maxResults, CustomContentManager.SortField sortField, CustomContentManager.SortOrder sortOrder) {
        Iterator children = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.parentContent.id = :parentId and content.contentStatus = 'current' and content.pluginModuleKey = :pluginModuleKey and content.originalVersion is null " + this.makeOrderBy("content", sortField, sortOrder));
            query.setParameter("parentId", (Object)parentId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            query.setFirstResult(offset);
            query.setMaxResults(maxResults);
            return query.list().iterator();
        });
        Preconditions.checkState((children != null ? 1 : 0) != 0);
        return children;
    }

    @Override
    public long countChildrenOfType(long parentId, String pluginModuleKey) {
        List result = (List)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("select count(content) from CustomContentEntityObject content where content.parentContent.id = :parentId and content.contentStatus = 'current' and content.pluginModuleKey = :pluginModuleKey and content.originalVersion is null ");
            query.setParameter("parentId", (Object)parentId);
            query.setParameter("pluginModuleKey", (Object)pluginModuleKey);
            return query.list();
        });
        return DataAccessUtils.longResult((Collection)result);
    }

    @Override
    public Iterator<CustomContentEntityObject> findAllContainedOfType(long containerContentId, String pluginContentKey) {
        Iterator contained = (Iterator)this.getHibernateTemplate().execute(session -> {
            Query query = session.createQuery("from CustomContentEntityObject content where content.containerContent.id = :containerContentId and content.contentStatus = 'current' and content.pluginModuleKey = :pluginModuleKey and content.originalVersion is null ");
            query.setParameter("containerContentId", (Object)containerContentId);
            query.setParameter("pluginModuleKey", (Object)pluginContentKey);
            return query.list().iterator();
        });
        Preconditions.checkState((contained != null ? 1 : 0) != 0);
        return contained;
    }

    private String makeOrderBy(String entityAlias, CustomContentManager.SortField sortField, CustomContentManager.SortOrder sortOrder) {
        StringBuilder buf = new StringBuilder(" order by ").append(entityAlias).append(".");
        switch (sortField) {
            case CREATED: {
                buf.append("creationDate ");
                break;
            }
            case MODIFIED: {
                buf.append("dateModified ");
                break;
            }
            case TITLE: {
                buf.append("title ");
            }
        }
        buf.append((Object)sortOrder);
        return buf.toString();
    }

    private HibernateContentQueryModuleDescriptor findQueryModuleByName(String queryName) {
        List it = this.pluginAccessor.getEnabledModuleDescriptorsByClass(HibernateContentQueryModuleDescriptor.class);
        for (HibernateContentQueryModuleDescriptor hibernateContentQueryModuleDescriptor : it) {
            if (!queryName.equals(hibernateContentQueryModuleDescriptor.getQueryName())) continue;
            return hibernateContentQueryModuleDescriptor;
        }
        throw new IllegalArgumentException("Unable to find content query named: " + queryName + ". Available queries are: " + it);
    }

    private <T> Iterator<T> createIteratorFromQuery(Query<T> query) throws HibernateException {
        return this.databaseSupportsSelectDistinctWithClob() ? query.list().iterator() : query.iterate();
    }

    private boolean databaseSupportsSelectDistinctWithClob() {
        return this.databaseCapabilities.isPostgreSql() || this.databaseCapabilities.isHSQL() || this.databaseCapabilities.isH2();
    }

    private static <T> Query<T> getQuery(HibernateContentQueryFactory contentQueryFactory, Session session, Object ... parameters) {
        return (Query)contentQueryFactory.getQuery((EntityManager)session, parameters);
    }

    @Override
    public void saveRawWithoutReindex(EntityObject objectToSave) {
        this.getHibernateTemplate().saveOrUpdate((Object)objectToSave);
    }
}

