/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages.persistence.dao.hibernate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateObjectDao;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.persistence.dao.DraftDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.util.Assert;

public class HibernateDraftDao
extends HibernateObjectDao
implements DraftDao {
    private static final Logger log = LoggerFactory.getLogger(HibernateDraftDao.class);
    private ConfluenceUserDao confluenceUserDao;

    @Override
    public Class getPersistentClass() {
        return Draft.class;
    }

    protected Draft getByClassId(long id) {
        ContentEntityObject ceo = (ContentEntityObject)this.getHibernateTemplate().execute(session -> (ContentEntityObject)session.get(ContentEntityObject.class, (Serializable)Long.valueOf(id)));
        if (!(ceo instanceof Draft)) {
            return null;
        }
        return (Draft)ceo;
    }

    @Override
    public void saveOrUpdate(Draft draft) throws IllegalArgumentException {
        Assert.notNull((Object)draft, (String)"Draft should not be null");
        Assert.hasText((String)draft.getPageId(), (String)("Draft content ID should be provided, " + draft));
        Assert.notNull((Object)draft.getCreator(), (String)("Draft creator name should be provided, " + draft));
        Assert.hasText((String)draft.getDraftType(), (String)("Draft type should be provided, " + draft));
        Draft persistentDraft = null;
        if (!draft.isNewPage()) {
            persistentDraft = this.getDraft(draft.getPageId(), draft.getCreator(), draft.getDraftType(), draft.getDraftSpaceKey());
        }
        if (persistentDraft == null) {
            this.save(draft);
        } else {
            this.updateModificationData(draft);
            BeanUtils.copyProperties((Object)draft, (Object)persistentDraft, (String[])new String[]{"id", "content", "creatorName", "lastModifierName"});
        }
    }

    @Override
    public Draft getDraft(String pageId, ConfluenceUser creator, String draftType, String spaceKey) {
        if (StringUtils.isBlank((CharSequence)pageId) || creator == null || StringUtils.isBlank((CharSequence)draftType)) {
            log.warn("Could not retrieve draft with invalid draft parameters, pageId: [" + pageId + "], creator: [" + creator + "], draftType: [" + draftType + "]");
            return null;
        }
        List results = Draft.NEW.equals(pageId) ? this.findNamedQueryStringParams("confluence.draft_findByPageIdAndCreatorSpacekey", "pageId", pageId, "creator", (Object)creator, "draftType", (Object)draftType, "draftSpaceKey", (Object)spaceKey, HibernateObjectDao.Cacheability.CACHEABLE, 2) : this.findNamedQueryStringParams("confluence.draft_findByPageIdAndCreator", "pageId", (Object)pageId, "creator", (Object)creator, "draftType", (Object)draftType, HibernateObjectDao.Cacheability.CACHEABLE, 2);
        if (results == null || results.size() == 0) {
            return null;
        }
        if (results.size() > 1) {
            log.warn("Found " + results.size() + " drafts for pageId = " + pageId + " creator = '" + creator + "' draftType = '" + draftType + "'");
        }
        return (Draft)results.get(0);
    }

    @Override
    public Draft getDraft(long draftId) {
        return this.getByClassId(draftId);
    }

    @Override
    public void remove(Draft draft) {
        if (!draft.isPersistent()) {
            throw new IllegalArgumentException("Attempt to remove a draft that is not persisted: " + draft);
        }
        Draft persistentDraft = this.getDraft(draft.getId());
        if (persistentDraft != null) {
            super.remove(persistentDraft);
        }
    }

    @Override
    public List<Draft> findByCreatorName(String creatorName) {
        ConfluenceUser creator = this.confluenceUserDao.findByUsername(creatorName);
        if (creator == null) {
            return Collections.emptyList();
        }
        return this.findNamedQueryStringParam("confluence.draft_findByCreator", "creator", creator, HibernateObjectDao.Cacheability.CACHEABLE);
    }

    @Override
    public int countDrafts(String creatorName) {
        ConfluenceUser creator = this.confluenceUserDao.findByUsername(creatorName);
        if (creator == null) {
            return 0;
        }
        return DataAccessUtils.intResult((Collection)this.findNamedQueryStringParam("confluence.draft_countDraftsForCreator", "creator", creator));
    }

    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }
}

