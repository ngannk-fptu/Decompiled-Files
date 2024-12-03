/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleCompleteKey
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.query.Query
 */
package com.atlassian.confluence.pages.templates.persistence.dao.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.ConfluenceHibernateObjectDao;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;

public class HibernatePageTemplateDao
extends ConfluenceHibernateObjectDao<PageTemplate>
implements PageTemplateDao {
    @Override
    public Class getPersistentClass() {
        return PageTemplate.class;
    }

    @Override
    public PageTemplate getById(long id) {
        return (PageTemplate)this.getByClassId(id);
    }

    public List findAllGlobalPageTemplates() {
        return this.findNamedQuery("confluence.pageTemplate_findAllGlobalPageTemplates");
    }

    @Override
    public PageTemplate findPageTemplateByName(String name) {
        return (PageTemplate)this.findSingleObject(this.findNamedQueryStringParam("confluence.pageTemplate_findPageTemplateByName", "name", name));
    }

    @Override
    public PageTemplate findPageTemplateByNameAndSpace(String name, Space space) {
        return (PageTemplate)this.findSingleObject(this.findNamedQueryStringParams("confluence.pageTemplate_findPageTemplateByNameAndSpaceKey", "name", name, "spaceid", (Object)space.getId()));
    }

    @Override
    public PageTemplate findCustomisedPluginTemplate(PluginTemplateReference pluginTemplateReference) {
        ModuleCompleteKey moduleCompleteKey = pluginTemplateReference.getModuleCompleteKey();
        ModuleCompleteKey referencingModuleCompleteKey = pluginTemplateReference.getReferencingModuleCompleteKey();
        Space space = pluginTemplateReference.getSpace();
        if (moduleCompleteKey == null) {
            return null;
        }
        String spaceKey = space != null ? space.getKey() : null;
        return (PageTemplate)this.findSingleObject((List)this.getHibernateTemplate().execute(session -> {
            StringBuilder hql = new StringBuilder("from PageTemplate pt where");
            hql.append(" pt.pluginKey = :pluginKey");
            hql.append(" and pt.moduleKey = :moduleKey");
            hql.append(" and pt.originalVersionPageTemplate is null");
            if (StringUtils.isBlank((CharSequence)spaceKey)) {
                hql.append(" and pt.space is null");
            } else {
                hql.append(" and pt.space.key = :spaceKey");
            }
            if (referencingModuleCompleteKey == null) {
                hql.append(" and pt.referencingPluginKey is null");
                hql.append(" and pt.referencingModuleKey is null");
            } else {
                hql.append(" and pt.referencingPluginKey = :referencingPluginKey");
                hql.append(" and pt.referencingModuleKey = :referencingModuleKey");
            }
            Query query = session.createQuery(hql.toString());
            query.setParameter("pluginKey", (Object)moduleCompleteKey.getPluginKey());
            query.setParameter("moduleKey", (Object)moduleCompleteKey.getModuleKey());
            if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
                query.setParameter("spaceKey", (Object)spaceKey);
            }
            if (referencingModuleCompleteKey != null) {
                query.setParameter("referencingPluginKey", (Object)referencingModuleCompleteKey.getPluginKey());
                query.setParameter("referencingModuleKey", (Object)referencingModuleCompleteKey.getModuleKey());
            }
            query.setCacheable(true);
            return query.list();
        }));
    }

    public List findPreviousVersions(long id) {
        return this.findNamedQueryStringParam("confluence.pageTemplate_findPreviousVersions", "originalVersionId", id, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    public List findLatestVersions() {
        return this.findNamedQuery("confluence.pageTemplate_findLatestPageTemplates");
    }

    @Override
    public List<PageTemplate> findBySpace(Space space) {
        return this.findNamedQueryStringParam("confluence.pageTemplate_findBySpace", "spaceId", space.getId());
    }
}

