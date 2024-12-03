/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.persistence.dao.DraftDao;
import com.atlassian.confluence.pages.persistence.dao.SessionDraftDao;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.diffs.MergerManager;
import com.atlassian.confluence.util.diffs.SimpleMergeResult;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.user.User;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class DefaultDraftManager
implements DraftManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultDraftManager.class);
    private DraftDao draftDao;
    private PageManager pageManager;
    private AttachmentManager attachmentManager;
    private LabelManager labelManager;
    private HttpContext httpContext;
    private MergerManager mergerManager;
    private ConfluenceUserDao confluenceUserDao;

    @Override
    public void saveDraft(Draft draft) {
        Assert.notNull((Object)draft, (String)"Draft must not be null");
        Assert.hasText((String)draft.getPageId(), (String)"Page ID of draft must not be blank");
        Assert.hasText((String)draft.getDraftType(), (String)"Draft type must not be blank");
        DraftDao dao = this.getDao(draft.getCreatorName());
        if (!draft.isNewPage()) {
            Assert.notNull((Object)this.getAbstractPage(draft), (String)("Could not find page with ID [" + draft.getPageId() + "] to save draft."));
            Draft existingDraft = dao.getDraft(draft.getPageId(), draft.getCreator(), draft.getDraftType(), draft.getDraftSpaceKey());
            if (existingDraft != null) {
                existingDraft.setBodyAsString(draft.getBodyAsString());
                existingDraft.setTitle(draft.getTitle());
                existingDraft.setDraftSpaceKey(draft.getDraftSpaceKey());
                if (existingDraft.getDraftType().equals(DraftService.DraftType.PAGE.toString())) {
                    existingDraft.getProperties().setLongProperty("legacy.draft.parent.id", draft.getProperties().getLongProperty("legacy.draft.parent.id", 0L));
                }
                dao.saveOrUpdate(existingDraft);
                draft.setId(existingDraft.getId());
            } else {
                dao.saveOrUpdate(draft);
            }
        } else {
            dao.saveOrUpdate(draft);
        }
    }

    private DraftDao getDao(String owner) {
        if (owner == null) {
            HttpSession session = this.httpContext.getSession(true);
            if (session == null) {
                throw new IllegalStateException("Failed to find or create session");
            }
            return new SessionDraftDao(session);
        }
        return this.draftDao;
    }

    @Override
    public Draft findDraft(Long pageId, String owner, String type, String spaceKey) {
        ConfluenceUser creator = this.confluenceUserDao.findByUsername(owner);
        if (creator == null) {
            log.warn("Could not find a user with the specified username so no draft returned.");
            return null;
        }
        return this.getDao(owner).getDraft(String.valueOf(pageId), creator, type, spaceKey);
    }

    @Override
    public int countDrafts(String owner) {
        return this.getDao(owner).countDrafts(owner);
    }

    @Override
    public Draft getDraft(long draftId) {
        String loggedInUser = AuthenticatedUserThreadLocal.getUsername();
        if (loggedInUser == null) {
            return null;
        }
        return this.getDao(loggedInUser).getDraft(draftId);
    }

    @Override
    public void removeDraft(Draft draft) {
        if (draft != null) {
            this.removeAssociatedAttachments(draft);
            this.labelManager.removeAllLabels(draft);
            this.getDao(draft.getCreatorName()).remove(draft);
        }
    }

    private void removeAssociatedAttachments(Draft draft) {
        for (Attachment attachment : this.attachmentManager.getLatestVersionsOfAttachments(draft)) {
            this.attachmentManager.removeAttachmentFromServer(attachment);
        }
    }

    @Override
    public List<Draft> findDraftsForUser(User user) {
        String owner = user == null ? null : user.getName();
        return this.getDao(owner).findByCreatorName(owner);
    }

    @Override
    public boolean isMergeRequired(Draft draft) {
        if (draft.isNewPage()) {
            return false;
        }
        AbstractPage page = this.getAbstractPage(draft);
        return page != null && draft.getPageVersion() != page.getVersion();
    }

    @Override
    public MergeResult mergeContent(Draft draft) {
        AbstractPage page = null;
        try {
            page = this.getAbstractPage(draft);
            if (page == null) {
                log.info("Cannot merge draft because page does not exist: " + draft);
                return SimpleMergeResult.FAIL_MERGE_RESULT;
            }
            AbstractPage originalVersion = (AbstractPage)this.pageManager.getOtherVersion(page, draft.getPageVersion());
            if (originalVersion == null) {
                log.info("Original version of page: {} v.{} does not exist. Cannot merge content.", (Object)page, (Object)draft.getPageVersion());
                return SimpleMergeResult.FAIL_MERGE_RESULT;
            }
            return this.mergerManager.getMerger().mergeContent(originalVersion.getBodyAsString(), page.getBodyAsString(), draft.getBodyAsString());
        }
        catch (RuntimeException e) {
            log.warn("Error merging latest version of page with draft: " + draft, (Throwable)e);
            log.debug("Draft contents:\n{}", (Object)draft.getBodyAsString());
            if (page != null) {
                log.debug("Current page contents:\n{}", (Object)page.getBodyAsString());
            }
            return SimpleMergeResult.FAIL_MERGE_RESULT;
        }
    }

    @Override
    public Draft create(String username, DraftService.DraftType draftType, String spaceKey) {
        return this.create(username, draftType, spaceKey, 0L);
    }

    @Override
    public Draft create(String username, DraftService.DraftType draftType, String spaceKey, long parentPageId) {
        Assert.notNull((Object)((Object)draftType), (String)"Draft type is required");
        Assert.isTrue((boolean)StringUtils.isNotBlank((CharSequence)spaceKey), (String)"Space key is required");
        Draft newDraft = new Draft();
        newDraft.setDraftType(draftType.toString());
        newDraft.setDraftSpaceKey(spaceKey);
        newDraft.setCreatorName(username);
        if (DraftService.DraftType.PAGE == draftType) {
            newDraft.getProperties().setLongProperty("legacy.draft.parent.id", parentPageId);
        }
        this.saveDraft(newDraft);
        return newDraft;
    }

    @Override
    @Deprecated
    public Draft getOrCreate(String username, String draftType, String spaceKey) {
        Assert.isTrue((boolean)StringUtils.isNotBlank((CharSequence)draftType), (String)"Draft type is required");
        Assert.isTrue((boolean)StringUtils.isNotBlank((CharSequence)spaceKey), (String)"Space key is required");
        Draft existingDraft = this.findDraft(Content.UNSET, username, draftType, spaceKey);
        if (existingDraft != null) {
            return existingDraft;
        }
        return this.create(username, DraftService.DraftType.getByRepresentation(draftType), spaceKey, 0L);
    }

    @Override
    public void removeAllDrafts() {
        for (Draft draft : this.draftDao.findAll()) {
            this.removeDraft(draft);
        }
    }

    @Override
    public void removeDraftsForUser(String username) {
        DraftDao dao = this.getDao(username);
        for (Draft draft : dao.findByCreatorName(username)) {
            this.removeDraft(draft);
        }
    }

    private AbstractPage getAbstractPage(Draft draft) {
        try {
            return this.pageManager.getAbstractPage(Long.parseLong(draft.getPageId()));
        }
        catch (NumberFormatException nfe) {
            log.error("Draft has invalid page ID: " + draft);
            return null;
        }
    }

    public void setDraftDao(DraftDao draftDao) {
        this.draftDao = draftDao;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public void setMergerManager(MergerManager mergerManager) {
        this.mergerManager = mergerManager;
    }

    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }
}

