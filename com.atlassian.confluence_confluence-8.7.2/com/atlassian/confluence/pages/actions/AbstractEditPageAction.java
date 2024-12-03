/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.opensymphony.xwork2.Action
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.diff.DiffException;
import com.atlassian.confluence.diff.Differ;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import com.atlassian.confluence.pages.actions.AbstractCreateAndEditPageAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb;
import com.atlassian.confluence.util.diffs.MergeResult;
import com.atlassian.confluence.util.diffs.MergerManager;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.opensymphony.xwork2.Action;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEditPageAction
extends AbstractCreateAndEditPageAction
implements BreadcrumbAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractEditPageAction.class);
    public static final String EDITED_PAGE_CRUMB_CSS = "edited-page-title";
    private AbstractPage originalPage;
    private int originalVersion;
    private int conflictingVersion;
    private String oldContent;
    private String overwrite;
    private String notifyWatchers;
    private String versionComment;
    private boolean conflictFound;
    private boolean mergeRequired;
    private MergerManager mergerManager;
    private StorageFormatCleaner storageFormatCleaner;
    private boolean viewConflict;
    private Differ differ;
    private String diff;
    private ConfluenceUser lastConflictingUser;
    private BreadcrumbGenerator breadcrumbGenerator;

    @Deprecated
    public boolean isVersionMismatch() {
        return this.getConflictingVersion() > 0;
    }

    @Deprecated
    public void setVersionMismatch(boolean versionMismatch) {
    }

    @Deprecated
    public String getOldContent() {
        return this.oldContent;
    }

    @Deprecated
    public void setOldContent(String oldContent) {
        this.oldContent = oldContent;
    }

    @Override
    public void setPage(AbstractPage page) {
        super.setPage(page);
        this.originalPage = (AbstractPage)page.clone();
    }

    @Override
    public Page getParentPage() {
        this.parentPage = super.getParentPage();
        if (this.parentPage == null && this.getPage() instanceof Page) {
            this.parentPage = ((Page)this.getPage()).getParent();
            if (this.parentPage != null) {
                this.parentPageTitle = this.parentPage.getTitle();
                this.parentPageId = this.parentPage.getId();
            }
        }
        return this.parentPage;
    }

    @Override
    public void validate() {
        super.validate();
        if (this.isTitleModified()) {
            this.validateDuplicatePageTitle();
        }
    }

    @Override
    public String doDefault() throws Exception {
        this.setTitle(this.getPage().getTitle());
        String result = super.doDefault();
        if ("locked".equals(result) || "activity-unavailable".equals(result)) {
            return result;
        }
        this.setOriginalVersion(this.getPage().getVersion());
        this.setLabelsString(LabelUtil.convertToDelimitedString(this.getPage(), this.getAuthenticatedUser()));
        this.setNewSpaceKey(this.getPage().getSpaceKey());
        if (this.getDraft() != null) {
            int draftsOriginalVersion = this.getDraft().getPageVersion();
            if (this.draftManager.isMergeRequired(this.getDraft())) {
                MergeResult mergeResult = this.draftManager.mergeContent(this.getDraft());
                if (mergeResult.hasConflicts()) {
                    if (this.isViewConflict()) {
                        this.setOriginalVersion(draftsOriginalVersion);
                        this.setWysiwygContent(this.getEditorFormattedContent(this.getDraft().getBodyAsString()));
                        this.handleVersionConflict();
                        this.setShowDraftMessage(false);
                    }
                    this.conflictFound = true;
                } else {
                    this.mergeRequired = true;
                    if (this.isUseDraft()) {
                        this.setWysiwygContent(this.getEditorFormattedContent(mergeResult.getMergedContent()));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public final boolean isEditPermissionRequired() {
        return true;
    }

    public String doEdit() {
        if (!this.handleVersionConflict()) {
            return "error";
        }
        return this.doSaveEditPageBean();
    }

    protected String afterEdit() {
        this.bean.put("redirectUrl", this.getPage().getUrlPath());
        return "success";
    }

    private String doSaveEditPageBean() {
        try {
            AbstractPage sharedDraft;
            SaveContext saveContext = ((DefaultSaveContext.Builder)DefaultSaveContext.builder().updateLastModifier(true).updateTrigger(PageUpdateTrigger.EDIT_PAGE)).build();
            AbstractPage page = this.getPage();
            boolean isModified = false;
            String contentForSaving = this.getContentForSaving();
            if (this.isContentModified()) {
                page.setBodyAsString(contentForSaving);
                page.setSynchronyRevision(this.getSyncRev());
                if (StringUtils.isBlank((CharSequence)this.getNotifyWatchers())) {
                    saveContext = DefaultSaveContext.SUPPRESS_NOTIFICATIONS;
                }
                isModified = true;
            }
            if (this.isTitleModified()) {
                page.setTitle(this.getTitle());
                isModified = true;
            }
            if (isModified) {
                page.setVersionComment(this.getVersionComment());
                this.pageManager.saveContentEntity(page, this.originalPage, saveContext);
            }
            if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(page.getSpaceKey()) && (sharedDraft = this.getContentDraft()) != null) {
                sharedDraft.setBodyAsString(contentForSaving);
                sharedDraft.setTitle(this.getTitle());
                this.pageManager.saveContentEntity(sharedDraft, DefaultSaveContext.DRAFT);
            }
            this.draftService.removeDraft(this.getPageId(), Content.UNSET);
            try {
                this.heartbeatManager.stopActivity(this.getPageId() + this.getContentType(), this.getAuthenticatedUser());
            }
            catch (Exception e) {
                log.error("Error stopping heartbeat activity", (Throwable)e);
            }
            return this.afterEdit();
        }
        catch (XhtmlException e) {
            this.addActionError("content.xhtml.editor.conversion.failed");
            log.warn("XhtmlException converting editor format to storage format. Turn on debug level logging to see editor format data.", (Throwable)e);
            log.debug("The editor data that could not be converted\n: {}", (Object)this.wysiwygContent);
            return "error";
        }
        catch (StaleObjectStateException e) {
            this.addActionError(this.getText("editing.an.outdated.page.version", new String[]{this.originalPage.getLastModifier().getName()}));
            log.debug("Could not save the object to the DB, old version detected. ", (Throwable)e);
            return "error";
        }
        catch (Exception e) {
            log.error("An error occured while storing the requested page!", (Throwable)e);
            this.addActionError(this.getText("saving.the.page.failed"));
            return "error";
        }
    }

    protected boolean isContentModified() {
        String newContent;
        try {
            newContent = StringUtils.stripToEmpty((String)this.getStorageFormat());
        }
        catch (XhtmlException ex) {
            return true;
        }
        return this.getPage().getBodyAsString() != null && !StringUtils.stripToEmpty((String)this.getPage().getBodyAsString()).equals(newContent);
    }

    protected boolean isTitleModified() {
        if (StringUtils.isNotEmpty((CharSequence)this.getTitle())) {
            return !this.getPage().getTitle().equals(this.getTitle());
        }
        return true;
    }

    private boolean handleVersionConflict() {
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getSpaceKey())) {
            return true;
        }
        if (this.isEditingLatestVersion() || this.isResolvingConflictWithLatestVersion()) {
            return true;
        }
        AbstractPage originalPage = (AbstractPage)this.pageManager.getOtherVersion(this.getPage(), this.getOriginalVersion());
        String contentForSaving = null;
        try {
            contentForSaving = this.getContentForSavingAndClean();
        }
        catch (XhtmlParsingException ex) {
            this.addActionError(this.getText("content.xhtml.parse.failed", new Object[]{ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage()}));
            return false;
        }
        catch (XhtmlException ex) {
            this.addActionError(this.getText("content.xhtml.editor.conversion.failed"));
            log.warn("XhtmlException converting editor format to storage format. Turn on debug level logging to see editor format data.", (Throwable)ex);
            log.debug("The editor data that could not be converted\n: {}", (Object)this.wysiwygContent);
            return false;
        }
        if (originalPage != null) {
            MergeResult mergeResult = this.mergerManager.getMerger().mergeContent(originalPage.getBodyAsString(), this.getPage().getBodyAsString(), contentForSaving);
            if (!mergeResult.hasConflicts()) {
                String mergedStorageFormat = mergeResult.getMergedContent();
                this.storageFormat = this.storageFormatCleaner.cleanQuietly(mergedStorageFormat);
                this.setWysiwygContent(this.getEditorFormattedContent(mergedStorageFormat));
                return true;
            }
            this.lastConflictingUser = originalPage.getLastModifier();
        }
        this.setConflictingVersion(this.getPage().getVersion());
        this.diffWithCurrentVersion(originalPage, contentForSaving);
        return false;
    }

    private void diffWithCurrentVersion(AbstractPage originalPage, String contentForSaving) {
        ContentEntityObject currentPage = (ContentEntityObject)this.getPage().clone();
        currentPage.setBodyAsString(contentForSaving);
        currentPage.setVersion(this.getPage().getVersion() + 1);
        try {
            this.diff = this.differ.diff(this.getPage(), currentPage);
        }
        catch (DiffException e) {
            this.addActionError("diff.pages.error.diffing", new Object[0]);
            log.error("Error generating diff for page: " + originalPage, (Throwable)e);
        }
        log.debug("Editing an outdated version of the page!");
    }

    private boolean isResolvingConflictWithLatestVersion() {
        return this.getConflictingVersion() == this.getPage().getVersion();
    }

    private boolean isEditingLatestVersion() {
        return this.getOriginalVersion() == this.getPage().getVersion();
    }

    private String getContentForSavingAndClean() throws XhtmlException {
        return this.formatConverter.convertToStorageFormat(this.wysiwygContent, this.getRenderContext());
    }

    @Deprecated
    public AbstractPage getOriginalPage() {
        return (AbstractPage)this.pageManager.getOtherVersion(this.getPage(), this.getOriginalVersion());
    }

    @Override
    public String getCancelResult() {
        try {
            this.heartbeatManager.stopActivity(this.getPageId() + this.getContentType(), this.getAuthenticatedUser());
        }
        catch (Exception e) {
            log.error("Error stopping heartbeat activity", (Throwable)e);
        }
        AbstractPage page = this.getPage();
        this.bean.put("redirectUrl", page.getUrlPath());
        return super.getCancelResult();
    }

    public int getOriginalVersion() {
        return this.originalVersion;
    }

    public void setOriginalVersion(int originalVersion) {
        this.originalVersion = originalVersion;
    }

    public int getConflictingVersion() {
        return this.conflictingVersion;
    }

    public void setConflictingVersion(int conflictingVersion) {
        this.conflictingVersion = conflictingVersion;
    }

    @HtmlSafe
    public String getDiff() {
        return this.diff;
    }

    public ConfluenceUser getLastConflictingUser() {
        return this.lastConflictingUser;
    }

    public String getOverwrite() {
        return this.overwrite;
    }

    public void setOverwrite(String overwrite) {
        this.overwrite = overwrite;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermissionNoExemptions(this.getAuthenticatedUser(), Permission.EDIT, this.getPage()) && this.hasDraftPermission();
    }

    public String getNotifyWatchers() {
        return this.notifyWatchers;
    }

    public void setNotifyWatchers(String notifyWatchers) {
        this.notifyWatchers = notifyWatchers;
    }

    public String getVersionComment() {
        return this.versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = StringUtils.isNotEmpty((CharSequence)versionComment) && versionComment.trim().length() > 0 ? versionComment : null;
    }

    public boolean isConflictFound() {
        return this.conflictFound;
    }

    public boolean isMergeRequired() {
        return this.mergeRequired;
    }

    public void setViewConflict(boolean viewConflict) {
        this.viewConflict = viewConflict;
    }

    public boolean isViewConflict() {
        return this.viewConflict;
    }

    @Override
    public String getContentType() {
        return this.getPage().getType();
    }

    public void setHtmlDiffer(Differ differ) {
        this.differ = differ;
    }

    public void setMergerManager(MergerManager mergerManager) {
        this.mergerManager = mergerManager;
    }

    public void setStorageFormatCleaner(StorageFormatCleaner storageFormatCleaner) {
        this.storageFormatCleaner = storageFormatCleaner;
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    @Override
    public Breadcrumb getBreadcrumb() {
        Breadcrumb breadcrumb = this.breadcrumbGenerator.getContentActionBreadcrumb((Action)this, this.getSpace(), this.getPage(), new GlobalHelper(this).getLabel());
        breadcrumb.setCssClass(EDITED_PAGE_CRUMB_CSS);
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return new SpaceBreadcrumb(this.getSpace()).concatWith(breadcrumb);
    }
}

