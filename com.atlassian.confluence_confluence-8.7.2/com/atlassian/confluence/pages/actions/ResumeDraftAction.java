/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.http.client.utils.URLEncodedUtils
 *  org.apache.http.message.BasicNameValuePair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResumeDraftAction
extends ConfluenceActionSupport {
    static final String RESUMENEWPAGESHARED = "resumenewpageshared";
    static final String RESUMENEWPAGE = "resumenewpage";
    static final String VIEWCONFLICT = "viewconflict";
    static final String RESUME = "resume";
    static final String SHARED = "shared";
    static final String NOT_FOUND = "notfound";
    static final String LEGACY = "legacy";
    static final List<String> INVITE_TO_EDIT_QUERY_PARAMS = Arrays.asList("shared", "username", "userFullName", "accessType", "grantAccess");
    private static final Logger log = LoggerFactory.getLogger(ResumeDraftAction.class);
    private DraftManager draftManager;
    private long draftId;
    private String draftShareId;
    private ContentEntityManager contentEntityManager;
    private CollaborativeEditingHelper collaborativeEditingHelper;
    private ContentEntityObject draft;
    private PageManagerInternal pageManager;
    private DraftsTransitionHelper draftsTransitionHelper;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        if (this.getDraft() == null) {
            return NOT_FOUND;
        }
        if (DraftsTransitionHelper.isLegacyDraft(this.getDraft()) && this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getSpaceKey())) {
            Draft personalDraft = (Draft)this.getDraft();
            if (personalDraft.isUnpublished()) {
                AbstractPage sharedDraft = this.pageManager.createDraft(this.getType(), this.getSpaceKey());
                sharedDraft.setBodyAsString(this.draft.getBodyAsString());
                sharedDraft.setTitle(this.draft.getTitle());
                sharedDraft.setCreationDate(this.draft.getCreationDate());
                sharedDraft.setContentPropertiesFrom(this.draft);
                this.pageManager.saveContentEntity(sharedDraft, DefaultSaveContext.DRAFT);
                this.draftsTransitionHelper.transitionContentObjects(personalDraft, sharedDraft);
                this.draftManager.removeDraft(personalDraft);
                this.setDraftId(sharedDraft.getId());
                this.setDraftShareId(sharedDraft.getShareId());
                log.info("Performed personal to shared migration:  {} -> {}", (Object)personalDraft.getId(), (Object)sharedDraft.getId());
                return RESUMENEWPAGESHARED;
            }
            return LEGACY;
        }
        if (this.getDraft().isUnpublished()) {
            return this.getResult(RESUMENEWPAGE);
        }
        if (DraftsTransitionHelper.isLegacyDraft(this.getDraft()) && this.draftManager.isMergeRequired((Draft)this.getDraft())) {
            if (this.draftManager.mergeContent((Draft)this.getDraft()).hasConflicts()) {
                return this.getResult(VIEWCONFLICT);
            }
            return RESUME;
        }
        return RESUME;
    }

    private String getResult(String result) {
        return result + (DraftsTransitionHelper.isLegacyDraft(this.getDraft()) ? "" : SHARED);
    }

    public long getDraftId() {
        return this.draftId;
    }

    public void setDraftId(long draftId) {
        this.draftId = draftId;
    }

    public String getSpaceKey() {
        return DraftsTransitionHelper.getSpaceKey(this.getDraft());
    }

    public String getPageId() {
        return DraftsTransitionHelper.getContentId(this.getDraft()).toString();
    }

    public String getType() {
        return DraftsTransitionHelper.getContentType(this.getDraft());
    }

    public String getDraftShareId() {
        return this.draftShareId;
    }

    public void setDraftShareId(String draftShareId) {
        this.draftShareId = draftShareId;
    }

    public ContentEntityManager getContentEntityManager() {
        return this.contentEntityManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public ContentEntityObject getDraft() {
        if (this.draft == null) {
            this.draft = this.contentEntityManager.getById(this.draftId);
        }
        return this.draft;
    }

    public String getInviteToEditParams() {
        Map parameterMap = this.getCurrentRequest().getParameterMap();
        List nameValuePair = parameterMap.keySet().stream().filter(INVITE_TO_EDIT_QUERY_PARAMS::contains).map(key -> Arrays.stream((String[])parameterMap.get(key)).map(value -> new BasicNameValuePair(key, value))).flatMap(result -> result).collect(Collectors.toList());
        return "&" + URLEncodedUtils.format(nameValuePair, (String)"UTF-8");
    }

    public String getAnalyticsParameters() {
        Map parameterMap = this.getCurrentRequest().getParameterMap();
        List nameValuePair = parameterMap.keySet().stream().filter(key -> key.startsWith("src")).map(key -> Arrays.stream((String[])parameterMap.get(key)).map(value -> new BasicNameValuePair(key, value))).flatMap(result -> result).collect(Collectors.toList());
        return "&" + URLEncodedUtils.format(nameValuePair, (String)"UTF-8");
    }

    public void setDraftManager(DraftManager draftManager) {
        this.draftManager = draftManager;
    }

    public void setCollaborativeEditingHelper(CollaborativeEditingHelper collaborativeEditingHelper) {
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    public void setPageManager(PageManagerInternal pageManager) {
        this.pageManager = pageManager;
    }

    public void setDraftsTransitionHelper(DraftsTransitionHelper draftsTransitionHelper) {
        this.draftsTransitionHelper = draftsTransitionHelper;
    }
}

