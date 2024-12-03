/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSession
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.persistence.dao.DraftDao;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.util.Assert;

public class SessionDraftDao
implements DraftDao {
    private final HttpSession session;

    public SessionDraftDao(HttpSession session) {
        Assert.notNull((Object)session, (String)"Session must not be null");
        this.session = session;
    }

    @Override
    public void saveOrUpdate(Draft draft) {
        draft.setLastModificationDate(new Date());
        this.session.setAttribute(this.getSessionKey(draft.getPageId()), (Object)draft);
    }

    @Override
    public Draft getDraft(String pageId, ConfluenceUser owner, String type, String spaceKey) {
        return this.getDraft(pageId);
    }

    private Draft getDraft(String pageId) {
        return (Draft)this.session.getAttribute(this.getSessionKey(pageId));
    }

    @Override
    public Draft getDraft(long draftId) {
        throw new UnsupportedOperationException("the SessionDraftDao does not support Draft getDraft(long id)");
    }

    @Override
    public void remove(Draft draft) {
        this.session.removeAttribute(this.getSessionKey(draft.getPageId()));
    }

    @Override
    public List<Draft> findByCreatorName(String creatorName) {
        if (creatorName == null) {
            return this.findAll();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Draft> findAll() {
        ArrayList<Draft> results = new ArrayList<Draft>();
        Enumeration attributeNames = this.session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = (String)attributeNames.nextElement();
            if (!attributeName.startsWith("confluence.pages.draft")) continue;
            results.add((Draft)this.session.getAttribute(attributeName));
        }
        return results;
    }

    String getSessionKey(String contentId) {
        return "confluence.pages.draft" + contentId;
    }

    @Override
    public int countDrafts(String creatorName) {
        return this.findAll().size();
    }
}

