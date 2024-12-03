/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.content.service.PageService;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.content.service.page.MovePageCommand;
import com.atlassian.confluence.content.service.page.MovePageCommandHelper;
import com.atlassian.confluence.content.service.page.PageLocator;
import com.atlassian.confluence.content.service.page.SinglePageLocator;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.AbstractTemplatePageAction;
import com.atlassian.confluence.pages.actions.ActionHelper;
import com.atlassian.confluence.pages.exceptions.ExternalChangesException;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCreateAndEditPageAction
extends AbstractTemplatePageAction
implements CaptchaAware {
    private static final Logger log = LoggerFactory.getLogger(AbstractCreateAndEditPageAction.class);
    public static final String LOCKED = "locked";
    public static final String ACTIVITY_UNAVAILABLE = "activity-unavailable";
    public static final String USER_LIMIT_REACHED = "user-limit-reached";
    protected String parentPageTitle;
    protected String parentPageSpaceKey;
    protected Page parentPage;
    protected long parentPageId;
    private String labelsString;
    protected String newSpaceKey;
    protected String parentPageString;
    protected DraftManager draftManager;
    protected DraftService draftService;
    private boolean showDraftMessage = false;
    private String position;
    protected long targetId;
    protected CaptchaManager captchaManager;
    protected NotificationManager notificationManager;
    protected PageService pageService;
    private SpaceService spaceService;
    protected CollaborativeEditingHelper collaborativeEditingHelper;
    protected DraftsTransitionHelper draftsTransitionHelper;
    protected HeartbeatManager heartbeatManager;
    protected MovePageCommandHelper movePageCommandHelper;
    private boolean useDraft = false;
    private String title;
    private ContentEntityObject draft;
    private Draft existingDraft;
    private String draftShareId;
    private long draftIdParameter;
    private String syncRev;

    public void setParentPageId(long parentPageId) {
        this.parentPageId = parentPageId;
        this.parentPage = null;
    }

    public long getParentPageId() {
        Page parentPage = this.getParentPage();
        if (this.parentPageId == 0L && parentPage != null) {
            this.parentPageId = parentPage.getId();
        }
        return this.parentPageId;
    }

    public Page getParentPage() {
        if (this.parentPage == null) {
            if (this.parentPageId != 0L) {
                this.parentPage = this.pageManager.getPage(this.parentPageId);
            } else if (StringUtils.isNotEmpty((CharSequence)this.getParentPageTitle())) {
                this.parentPage = this.pageManager.getPage(this.getParentPageSpaceKey(), this.getParentPageTitle());
            }
        }
        return this.parentPage;
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        this.addPermissionTypeTo("EDITSPACE", permissionTypes);
        return permissionTypes;
    }

    protected void validateDuplicatePageTitle() {
        Page matchingPage;
        if (StringUtils.isNotEmpty((CharSequence)this.getTitle()) && (matchingPage = this.pageManager.getPage(this.getNewSpaceKey(), this.getTitle())) != null && matchingPage.getId() != this.getPage().getId()) {
            this.addActionError("page.title.exists.pagespecific", this.getTitle());
        }
    }

    @Override
    public void validate() {
        String parentPageTitle;
        super.validate();
        if (StringUtils.isNotEmpty((CharSequence)this.getParentPageString())) {
            this.setParentPageTitle(this.parentPageString);
            this.setParentPageSpaceKey(this.getNewSpaceKey());
        }
        if (StringUtils.isNotEmpty((CharSequence)this.getParentPageTitle()) && this.getParentPage() == null) {
            this.addActionError(this.getText("parent.page.doesnt.exist"));
        }
        if (StringUtils.isEmpty((CharSequence)this.getSpaceKey())) {
            this.addActionError(this.getText("space.key.empty"));
        } else if (this.getSpace() == null) {
            this.addActionError(this.getText("space.doesnt.exist"));
        }
        int labelCountWithoutFavourites = LabelUtil.countLabelsWithoutFavourites(this.getLabelsString());
        if (labelCountWithoutFavourites > 500) {
            this.addFieldError("labelsString", "labels.over.max", new Object[]{labelCountWithoutFavourites, 500});
        } else {
            List<String> labelNames = LabelUtil.split(this.getLabelsString());
            for (String labelName : labelNames) {
                if (!LabelUtil.isValidLabelName(labelName)) {
                    this.addFieldError("labelsString", this.getText("page.labels.invalid", new String[]{LabelParser.getInvalidCharactersAsString()}));
                    continue;
                }
                ParsedLabelName parsedLabelName = LabelParser.parse(labelName.trim());
                if (parsedLabelName != null && !LabelParser.isValidLabelLength(parsedLabelName)) {
                    this.addFieldError("labelsString", "page.label.too.long", new Object[]{255});
                    continue;
                }
                if (!LabelParser.isPersonalLabel(labelName) || LabelParser.isLabelOwnedByUser(labelName, this.getAuthenticatedUser())) continue;
                this.addFieldError("labelsString", this.getText("not.permitted.to.add.labels", new Object[]{HtmlUtil.htmlEncode(labelName)}));
            }
        }
        if (StringUtils.isBlank((CharSequence)this.getTitle())) {
            this.addActionError(this.getText("page.title.empty"));
        } else if (!AbstractPage.isValidTitleLength(this.getTitle())) {
            this.addActionError(this.getText("page.title.too.long"));
        }
        if (StringUtils.isNotEmpty((CharSequence)this.getTitle()) && StringUtils.isNotEmpty((CharSequence)this.getParentPageString()) && StringUtils.isNotEmpty((CharSequence)(parentPageTitle = ActionHelper.extractPageTitle(this.getParentPageString()))) && parentPageTitle.equalsIgnoreCase(this.getTitle())) {
            this.addActionError(this.getText("page.cant.be.parent.of.itself"));
        }
    }

    protected MovePageCommand getMovePageCommand() {
        MovePageCommand command;
        SinglePageLocator currentPage = new SinglePageLocator((Page)this.getPage());
        if ("topLevel".equals(this.getPosition())) {
            assert (this.getNewSpaceKey() != null);
            command = this.movePageCommandHelper.newMovePageCommand(currentPage, this.spaceService.getKeySpaceLocator(this.getNewSpaceKey()), MovePageCommandHelper.MovePageMode.LEGACY);
        } else {
            PageLocator target = this.targetId > 0L ? this.pageService.getIdPageLocator(this.targetId) : this.pageService.getTitleAndSpaceKeyPageLocator(this.getNewSpaceKey(), this.getParentPageTitle());
            command = this.movePageCommandHelper.newMovePageCommand(currentPage, target, this.getPosition(), MovePageCommandHelper.MovePageMode.LEGACY);
        }
        return command;
    }

    public void setShowDraftMessage(boolean showDraftMessage) {
        this.showDraftMessage = showDraftMessage;
    }

    public abstract String getContentType();

    public long getFromPageId() {
        return 0L;
    }

    @Internal
    public boolean startHeartbeatOnDoDefault() {
        return true;
    }

    @Override
    public String doDefault() throws Exception {
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getNewSpaceKey())) {
            ContentEntityObject content;
            ContentEntityObject contentEntityObject = content = this.getPage() != null ? this.getPage() : this.getDraftAsCEO();
            if (content != null && this.startHeartbeatOnDoDefault()) {
                List<User> usersForActivity;
                ConfluenceUser user = this.getAuthenticatedUser();
                long contentId = content.getId();
                String contentType = this.getPage() != null ? this.getPage().getType() : DraftsTransitionHelper.getContentType(this.getDraftAsCEO());
                String activityId = contentId + contentType;
                try {
                    this.heartbeatManager.startActivity(activityId, user);
                    usersForActivity = this.heartbeatManager.getUsersForActivity(activityId);
                }
                catch (Exception e) {
                    return ACTIVITY_UNAVAILABLE;
                }
                if (this.collaborativeEditingHelper.isOverLimit(usersForActivity.size())) {
                    try {
                        this.heartbeatManager.stopActivity(activityId, user);
                    }
                    catch (Exception e) {
                        log.error("Error stopping heartbeat activity", (Throwable)e);
                    }
                    return USER_LIMIT_REACHED;
                }
            }
            if (this.draft == null) {
                if (this.isNewAbstractPage()) {
                    this.draft = this.draftsTransitionHelper.createDraft(this.getContentType(), this.getNewSpaceKey(), this.getFromPageId());
                    this.moveTemplateLabelsToDraft();
                } else {
                    try {
                        this.draft = this.draftsTransitionHelper.getDraftForPage(this.getPage());
                    }
                    catch (ExternalChangesException e) {
                        return ACTIVITY_UNAVAILABLE;
                    }
                }
            }
            boolean setBody = this.getPage() == null;
            this.setDraftData(setBody);
        } else {
            Draft draft = this.existingDraft = DraftsTransitionHelper.isLegacyDraft(this.draft) ? (Draft)this.draft : null;
            if (this.existingDraft != null) {
                if (this.useDraft) {
                    this.setDraftData(true);
                } else {
                    this.showDraftMessage = true;
                }
            } else {
                this.processDraftParameters();
            }
        }
        return super.doDefault();
    }

    private boolean resumeDraft() {
        if (this.draft == null) {
            this.draft = this.draftService.findDraftForEditor(this.getPageId(), DraftService.DraftType.getByRepresentation(this.getContentType()), this.getSpaceKey());
        }
        if (this.draft == null) {
            this.addActionError(this.getText("draft.error.resume.notfound"));
            return false;
        }
        this.setDraftData(true);
        return true;
    }

    private void setDraftData(boolean setContent) {
        String spaceKey;
        if (setContent && StringUtils.isNotEmpty((CharSequence)this.draft.getTitle())) {
            this.setTitle(this.draft.getTitle());
        }
        if (StringUtils.isNotEmpty((CharSequence)(spaceKey = DraftsTransitionHelper.getSpaceKey(this.draft)))) {
            this.setNewSpaceKey(spaceKey);
        }
        if (setContent && StringUtils.isNotEmpty((CharSequence)this.draft.getBodyAsString())) {
            this.setWysiwygContent(this.getEditorFormattedContent(this.draft.getBodyAsString()));
        }
    }

    private void processDraftParameters() {
        this.showDraftMessage = false;
        if (this.useDraft && this.resumeDraft()) {
            return;
        }
        this.existingDraft = this.draftService.findDraftForEditor(this.getPageId(), DraftService.DraftType.getByRepresentation(this.getContentType()), this.getSpaceKey());
        if (!this.isNewAbstractPage()) {
            this.draft = this.existingDraft;
        } else {
            this.draft = this.existingDraft != null && this.getAuthenticatedUser() == null ? this.existingDraft : this.draftService.createNewContentDraft(this.getSpaceKey(), DraftService.DraftType.getByRepresentation(this.getContentType()));
            this.moveTemplateLabelsToDraft();
        }
        if (this.existingDraft != null && !this.existingDraft.isBlank()) {
            log.debug("Has existing non blank draft {}", (Object)this.existingDraft);
            this.showDraftMessage = true;
        } else {
            this.existingDraft = null;
        }
        log.debug("Draft is null? {}.  Showing draft message : {}", (Object)(this.draft == null ? 1 : 0), (Object)this.showDraftMessage);
    }

    private void moveTemplateLabelsToDraft() {
        if (this.draft != null && this.getPageTemplate() != null) {
            for (Label label : this.getPageTemplate().getLabels()) {
                this.labelManager.addLabel(this.draft, label);
            }
        }
    }

    @Deprecated
    protected Draft createDraft() {
        return this.draftService.createNewContentDraft(this.getSpaceKey(), DraftService.DraftType.getByRepresentation(this.getContentType()));
    }

    protected boolean hasDraftPermission() {
        ContentEntityObject draft;
        try {
            draft = this.getDraftAsCEO();
        }
        catch (ExternalChangesException e) {
            draft = null;
        }
        if (draft != null) {
            if (!draft.sharedAccessAllowed(this.getAuthenticatedUser()) && this.getAuthenticatedUser() != null && StringUtils.isNotBlank((CharSequence)this.draftShareId) && this.draftShareId.equals(draft.getShareId()) && this.contentPermissionManager.hasContentLevelPermission(this.getAuthenticatedUser(), "Edit", draft)) {
                this.contentPermissionManager.addContentPermission(ContentPermission.createUserPermission("Share", this.getAuthenticatedUser()), draft);
            }
            return this.permissionManager.hasPermissionNoExemptions(this.getAuthenticatedUser(), Permission.EDIT, draft);
        }
        return true;
    }

    public boolean isNewAbstractPage() {
        return Content.UNSET.equals(this.getPageId());
    }

    @Override
    public boolean isCollaborativeContent() {
        return (this.getContentType().equals("page") || this.getContentType().equals("blogpost")) && this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.getSpaceKey());
    }

    public void setTitle(String title) {
        this.title = title.trim();
    }

    public String getParentPageTitle() {
        if (StringUtils.isEmpty((CharSequence)this.parentPageTitle)) {
            this.parentPageTitle = this.getParentPageString();
        }
        return this.parentPageTitle;
    }

    public void setParentPageTitle(String parentPageTitle) {
        this.parentPageTitle = parentPageTitle;
        this.parentPageId = 0L;
    }

    public String getParentPageSpaceKey() {
        if (StringUtils.isNotEmpty((CharSequence)this.parentPageSpaceKey)) {
            return this.parentPageSpaceKey;
        }
        return this.getSpaceKey();
    }

    public void setParentPageSpaceKey(String parentPageSpaceKey) {
        this.parentPageSpaceKey = parentPageSpaceKey;
        this.parentPage = null;
    }

    public ContentPermission getCurrentEditPermission() {
        return this.getPage().getContentPermission("Edit");
    }

    public ContentPermission getCurrentViewPermission() {
        return this.getPage().getContentPermission("View");
    }

    public boolean hasSetPagePermissionsPermission() {
        return this.isSpaceAdmin() || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, this.getPage());
    }

    public List getViewInheritedContentPermissionSets() {
        return this.contentPermissionManager.getContentPermissionSets(this.getParentPage(), "View");
    }

    public void setLabelsString(String labelsString) {
        this.labelsString = labelsString;
    }

    public void setLabelsString(List<Label> labels) {
        StringBuilder newLabelString = new StringBuilder();
        Iterator<Label> labelIt = labels.iterator();
        while (labelIt.hasNext()) {
            newLabelString.append(LabelParser.render(labelIt.next()));
            if (!labelIt.hasNext()) continue;
            newLabelString.append(" ");
        }
        this.labelsString = newLabelString.toString();
    }

    public String getLabelsString() {
        return Objects.requireNonNullElse(this.labelsString, "");
    }

    public String getNewSpaceKey() {
        if (this.newSpaceKey == null) {
            return this.getSpaceKey();
        }
        return this.newSpaceKey;
    }

    public void setNewSpaceKey(String newSpaceKey) {
        this.newSpaceKey = newSpaceKey;
    }

    public String getParentPageString() {
        return this.parentPageString;
    }

    public void setParentPageString(String parentPageString) {
        this.parentPageString = parentPageString;
    }

    public void setDraftManager(DraftManager draftManager) {
        this.draftManager = draftManager;
    }

    public void setDraftService(DraftService draftService) {
        this.draftService = draftService;
    }

    public boolean isShowDraftMessage() {
        return this.showDraftMessage;
    }

    public void setUseDraft(boolean useDraft) {
        this.useDraft = useDraft;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Deprecated
    public Draft getDraft() {
        return DraftsTransitionHelper.isLegacyDraft(this.getDraftAsCEO()) ? (Draft)this.draft : null;
    }

    @Deprecated
    public ContentEntityObject getDraftAsCEO() {
        if (this.draft == null && this.draftIdParameter > -1L) {
            if (this.draftIdParameter == 0L) {
                this.draft = this.draftsTransitionHelper.getDraftForPage(this.getPage());
                this.draftIdParameter = -1L;
            } else {
                this.draft = this.draftsTransitionHelper.getDraft(this.draftIdParameter);
                this.draftIdParameter = -1L;
            }
        }
        return this.draft;
    }

    public AbstractPage getContentDraft() {
        return DraftsTransitionHelper.isLegacyDraft(this.getDraftAsCEO()) ? null : (AbstractPage)this.draft;
    }

    public Draft getExistingDraft() {
        return this.existingDraft;
    }

    public long getExistingDraftId() {
        return this.getExistingDraft() != null ? this.getExistingDraft().getId() : 0L;
    }

    public long getDraftId() {
        return this.draft != null ? this.draft.getId() : 0L;
    }

    public String getDraftShareId() {
        return this.draft != null ? this.draft.getShareId() : null;
    }

    public void setDraftShareId(String draftShareId) {
        this.draftShareId = draftShareId;
    }

    public String getSyncRev() {
        return this.syncRev != null ? this.syncRev : this.getSynchronyRevision();
    }

    private String getSynchronyRevision() {
        AbstractPage contentObject = this.getContentObject();
        String rev = "dummy-sync-rev";
        if (contentObject != null && contentObject.getSynchronyRevision() != null) {
            rev = contentObject.getSynchronyRevision();
        }
        return rev;
    }

    public String getConfluenceRevision() {
        AbstractPage contentObject = this.getContentObject();
        return contentObject == null ? null : contentObject.getConfluenceRevision();
    }

    public String getSynchronyRevisionSource() {
        AbstractPage contentObject = this.getContentObject();
        return contentObject == null ? null : contentObject.getSynchronyRevisionSource();
    }

    protected AbstractPage getContentObject() {
        return this.getPage() != null ? this.getPage() : this.getContentDraft();
    }

    public void setSyncRev(String syncRev) {
        this.syncRev = syncRev;
    }

    public long getEntityId() {
        if (this.getPageId() <= 0L) {
            return this.getDraftId();
        }
        return this.getPageId();
    }

    public void setDraftId(long draftId) {
        this.draftIdParameter = draftId;
    }

    @Override
    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext result = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        try {
            result.setParameter("draft", this.getDraft());
            result.setParameter("contentDraft", this.getDraftAsCEO());
            result.setParameter("parentPage", this.getParentPage());
            result.setIsEditPageRestricted(this.isRestricted());
            if (this.getPage() == null) {
                result.setPage(this.getContentDraft());
            }
        }
        catch (ExternalChangesException externalChangesException) {
            // empty catch block
        }
        return result;
    }

    @Override
    public String getCancelResult() {
        if (this.getAuthenticatedUser() != null) {
            for (Draft potentialBlankDraft : this.draftManager.findDraftsForUser(this.getAuthenticatedUser())) {
                try {
                    boolean currentDraft = this.getDraftId() == potentialBlankDraft.getId();
                    boolean isPageIdNull = potentialBlankDraft.getPageId() == null;
                    boolean hasDraftContentChanged = this.draftService.isDraftContentChanged(potentialBlankDraft.getId(), potentialBlankDraft.getTitle(), potentialBlankDraft.getBodyAsString(), potentialBlankDraft.getPageIdAsLong());
                    if (currentDraft || !isPageIdNull && hasDraftContentChanged) continue;
                    this.draftService.removeDraft(potentialBlankDraft.getId());
                }
                catch (NotValidException nve) {
                    log.warn(String.format("Removing invalid draft: %s, for user : %s ", potentialBlankDraft, this.getAuthenticatedUser()));
                    log.info("Stacktrace: ", (Throwable)nve);
                    this.draftService.removeDraft(potentialBlankDraft.getId());
                }
                catch (NotAuthorizedException nae) {
                    log.error("User has a draft that they are not authorized to access: " + potentialBlankDraft.getId() + "  " + nae.getMessage());
                    log.info("More details", (Throwable)nae);
                }
            }
        }
        return super.getCancelResult();
    }

    protected void setPermissions(List<ContentPermission> permissions) {
        for (ContentPermission permission : permissions) {
            this.getPage().addPermission(permission);
        }
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTargetId(String targetId) {
        try {
            this.targetId = Long.parseLong(targetId);
        }
        catch (NumberFormatException e) {
            this.targetId = -1L;
        }
    }

    protected String getPosition() {
        return this.position;
    }

    protected long getTargetId() {
        return this.targetId;
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public boolean isUseDraft() {
        return this.useDraft;
    }

    public boolean isRestricted() {
        ContentEntityObject content = this.getPage() != null ? this.getPage() : this.getDraftAsCEO();
        return content != null && content.hasContentPermissions();
    }

    @Override
    protected List<Label> getLabels() {
        if (this.getPage() != null) {
            return this.getPage().getVisibleLabels(this.getAuthenticatedUser());
        }
        if (this.getDraftAsCEO() != null) {
            return this.getDraftAsCEO().getVisibleLabels(this.getAuthenticatedUser());
        }
        return Collections.emptyList();
    }

    public void setCollaborativeEditingHelper(CollaborativeEditingHelper collaborativeEditingHelper) {
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    public CollaborativeEditingHelper getCollaborativeEditingHelper() {
        return this.collaborativeEditingHelper;
    }

    public void setDraftsTransitionHelper(DraftsTransitionHelper draftsTransitionHelper) {
        this.draftsTransitionHelper = draftsTransitionHelper;
    }

    public void setHeartbeatManager(HeartbeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    public void setMovePageCommandHelper(MovePageCommandHelper movePageCommandHelper) {
        this.movePageCommandHelper = movePageCommandHelper;
    }
}

