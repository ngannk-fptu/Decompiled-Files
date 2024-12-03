/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class DefaultDraftService
implements DraftService {
    private static final Logger log = LoggerFactory.getLogger(DefaultDraftService.class);
    private PermissionManager permissionManager;
    private PageManager pageManager;
    private DraftManager draftManager;
    private EditorConverter editConverter;
    private ContentEntityManager contentEntityManager;

    DefaultDraftService(PermissionManager permissionManager, PageManager pageManager, DraftManager draftManager, EditorConverter editConverter, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.draftManager = draftManager;
        this.editConverter = editConverter;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public Draft saveDraftFromEditor(Long draftId, Long parentPageId, String title, DraftService.DraftType type, String content, Long contentId, String spaceKey, int pageVersion) throws NotValidException {
        String storageFormat;
        this.assertNotNull(spaceKey, type);
        Draft draft = null;
        if (draftId != null) {
            draft = this.draftManager.getDraft(draftId);
        }
        if (draft == null && !Content.UNSET.equals(contentId)) {
            draft = this.draftManager.findDraft(contentId, AuthenticatedUserThreadLocal.getUsername(), type.toString(), spaceKey);
        }
        if (draft == null) {
            draft = new Draft();
            draft.setTitle(title);
            draft.setDraftType(type.toString());
            draft.setPageId(contentId);
            draft.setDraftSpaceKey(spaceKey);
            draft.setPageVersion(pageVersion);
            draft.setCreator(AuthenticatedUserThreadLocal.get());
        }
        if (DraftService.DraftType.PAGE.toString().equals(draft.getDraftType()) && parentPageId != null) {
            draft.getProperties().setLongProperty("legacy.draft.parent.id", parentPageId);
        }
        if (!StringUtils.equals((CharSequence)title, (CharSequence)draft.getTitle())) {
            draft.setTitle(title);
        }
        try {
            storageFormat = this.editConverter.convert(content, new DefaultConversionContext(this.getContextEntity(draft).toPageContext()));
        }
        catch (XhtmlException ex) {
            throw new NotValidException("The supplied editor content could not be converted to storage format.", ex);
        }
        draft.setBodyAsString(storageFormat);
        try {
            this.draftManager.saveDraft(draft);
        }
        catch (IllegalArgumentException ex) {
            throw new NotValidException(ex.getMessage());
        }
        return draft;
    }

    @Override
    @Deprecated
    public Draft saveDraftFromEditor(Long draftId, String title, DraftService.DraftType type, String content, Long contentId, String spaceKey, int pageVersion) throws NotValidException {
        return this.saveDraftFromEditor(draftId, null, title, type, content, contentId, spaceKey, pageVersion);
    }

    private ContentEntityObject getContextEntity(Draft draft) {
        if (draft.isNew()) {
            return draft;
        }
        return this.contentEntityManager.getById(draft.getPageIdAsLong());
    }

    @Override
    public Draft findDraftForEditor(long contentId, DraftService.DraftType type, String spaceKey) {
        if (type == null) {
            throw new IllegalArgumentException("The draft type is a required parameter.");
        }
        return this.draftManager.findDraft(contentId, AuthenticatedUserThreadLocal.getUsername(), type.toString(), spaceKey);
    }

    @Override
    public Draft createNewContentDraft(String spaceKey, DraftService.DraftType type) {
        return this.draftManager.create(AuthenticatedUserThreadLocal.getUsername(), type, spaceKey);
    }

    private void assertNotNull(String spaceKey, DraftService.DraftType type) throws IllegalArgumentException {
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new IllegalArgumentException("spaceKey is a required parameter for creating a draft.");
        }
        if (type == null) {
            throw new IllegalArgumentException("draftType parameter is required to identify to what content type the draft applies.");
        }
    }

    @Override
    public Long removeDraft(long abstractPageId, long draftId) {
        if (draftId != 0L) {
            return this.removeDraft(draftId);
        }
        AbstractPage abstractPage = this.pageManager.getAbstractPage(abstractPageId);
        if (abstractPage != null) {
            Draft draft = this.draftManager.findDraft(abstractPage.getId(), AuthenticatedUserThreadLocal.getUsername(), abstractPage.getType(), abstractPage.getSpaceKey());
            if (draft != null) {
                this.draftManager.removeDraft(draft);
                return draft.getId();
            }
        } else {
            log.warn("Attempt to remove draft with 0 draftId from unfound content with id: {}", (Object)abstractPageId);
        }
        return null;
    }

    @Override
    public Long removeDraft(long draftId) {
        Draft draft = this.getDraft(draftId);
        if (draft != null) {
            this.draftManager.removeDraft(draft);
            return draftId;
        }
        return null;
    }

    @Override
    public List<Draft> findDrafts(int limit, int offset) throws NotValidException {
        if (limit == 0 || offset < 0) {
            throw new NotValidException("Must provide a limit greater than zero and an offset greater than or equal to zero");
        }
        ConfluenceUser loggedIn = AuthenticatedUserThreadLocal.get();
        List<Draft> drafts = this.draftManager.findDraftsForUser(loggedIn);
        List<Draft> sublist = drafts.size() < limit ? drafts.subList(0, drafts.size()) : (drafts.size() > offset + limit ? drafts.subList(offset, offset + limit) : (drafts.size() < offset ? new ArrayList<Draft>() : drafts.subList(offset, drafts.size())));
        return sublist;
    }

    @Override
    public Draft getDraft(long draftId) throws NotAuthorizedException, NotValidException {
        Draft draft = this.draftManager.getDraft(draftId);
        if (draft == null) {
            throw new NotValidException("No draft found for id : " + draftId);
        }
        if (draft.getCreatorName().equals(AuthenticatedUserThreadLocal.getUsername())) {
            return draft;
        }
        throw new NotAuthorizedException(AuthenticatedUserThreadLocal.getUsername());
    }

    @Override
    @Deprecated
    public boolean isDraftContentChanged(Long draftId, String title, String content, Long contentId) throws NotAuthorizedException, NotValidException {
        Draft draft = this.draftManager.getDraft(draftId);
        if (draft == null) {
            throw new NotValidException("No draft found for draftId : " + draftId);
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, draft)) {
            throw new NotAuthorizedException(AuthenticatedUserThreadLocal.get(), Permission.VIEW, draft);
        }
        if (Content.UNSET.equals(contentId)) {
            return !draft.isBlank();
        }
        AbstractPage page = this.pageManager.getAbstractPage(contentId);
        if (page == null) {
            throw new NotValidException("Page not found with id " + contentId);
        }
        return !StringUtils.equals((CharSequence)content, (CharSequence)page.getBodyAsString()) || !StringUtils.equals((CharSequence)title, (CharSequence)page.getTitle());
    }
}

