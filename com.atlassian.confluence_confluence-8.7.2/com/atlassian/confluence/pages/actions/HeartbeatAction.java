/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor
 *  com.atlassian.confluence.api.model.relations.Relatable
 *  com.atlassian.confluence.api.model.relations.RelationDescriptor
 *  com.atlassian.confluence.api.service.relations.RelationService
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.struts2.ServletActionContext
 *  org.joda.time.DateTime
 *  org.joda.time.Hours
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.relations.CollaboratorRelationDescriptor;
import com.atlassian.confluence.api.model.relations.Relatable;
import com.atlassian.confluence.api.model.relations.RelationDescriptor;
import com.atlassian.confluence.api.service.relations.RelationService;
import com.atlassian.confluence.core.ActivityAjaxResponse;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.HeartbeatManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.struts2.ServletActionContext;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiresAnyConfluenceAccess
public class HeartbeatAction
extends ConfluenceActionSupport
implements Beanable {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatAction.class);
    private static final long DRAFT_EDIT_PERIOD = Long.getLong("confluence.collab.draft.inactivity.period.update.hours", 12L);
    @VisibleForTesting
    protected static final String DISABLE_UNACTIVE_DRAFTS_AUTOSAVE = "collab.drafts.inactive.autosave.disabled";
    private HeartbeatManager heartbeatManager;
    private DraftManager draftManager;
    private I18NBeanFactory i18NBeanFactory;
    private LocaleManager localeManager;
    private FormatSettingsManager formatSettingsManager;
    private DateFormatter dateFormatter;
    private XsrfTokenGenerator tokenGenerator;
    private ContentEntityManager contentEntityManager;
    private CollaborativeEditingHelper collaborativeEditingHelper;
    private RelationService relationService;
    private DarkFeaturesManager darkFeaturesManager;
    private Long contentId;
    private String draftType;
    private Object bean;
    private String spaceKey;
    private String contributorsHash;

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public void setDraftType(String draftType) {
        this.draftType = draftType;
    }

    public void setContributorsHash(String contributorsHash) {
        this.contributorsHash = contributorsHash;
    }

    public void setXsrfTokenGenerator(XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    public void setHeartbeatManager(HeartbeatManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    public void setDraftManager(DraftManager draftManager) {
        this.draftManager = draftManager;
    }

    @Override
    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    @Override
    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setCollaborativeEditingHelper(CollaborativeEditingHelper collaborativeEditingHelper) {
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    public void setApiRelationService(RelationService relationService) {
        this.relationService = relationService;
    }

    public void setDarkFeaturesManager(DarkFeaturesManager darkFeaturesManager) {
        this.darkFeaturesManager = darkFeaturesManager;
    }

    public String stopActivity() {
        log.debug("stopping for key [" + this.contentId + this.draftType + "], user [" + AuthenticatedUserThreadLocal.get() + "]");
        this.heartbeatManager.stopActivity(this.contentId + this.draftType, AuthenticatedUserThreadLocal.get());
        return "success";
    }

    public String startActivity() {
        List<ActivityAjaxResponse> activityResponses;
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ContentEntityObject content = this.contentId != null ? this.contentEntityManager.getById(this.contentId) : null;
        String activityKey = this.contentId + this.draftType;
        if (content == null || !this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, content)) {
            log.error("No content found or no permission to view content with contentId: " + this.contentId);
            this.bean = Collections.singletonMap("message", "No content found or no permission to view content with contentId: " + this.contentId);
            return "error";
        }
        this.updateDraftLastModificationDateIfNeeded();
        if (currentUser != null) {
            log.debug("starting for key [" + activityKey + "], user [" + currentUser + "]");
            try {
                this.heartbeatManager.startActivity(activityKey, currentUser);
            }
            catch (RuntimeException e) {
                log.error("Failed to start activity for key {}", (Object)(this.contentId + this.draftType));
                this.bean = Collections.singletonMap("message", "Failed to start activity");
                return "error";
            }
        }
        try {
            activityResponses = this.getActivityResponses(content, activityKey, currentUser);
        }
        catch (RuntimeException e) {
            this.bean = Collections.singletonMap("message", "Failed to get users currently in the session.");
            return "error";
        }
        String labelHash = LabelUtil.getLabelsHash(content.getVisibleLabels(currentUser));
        String restrictionsHash = PermissionUtils.getRestrictionsHash(content);
        Boolean hasViewRestrictions = content.hasPermissions("View");
        log.debug("found " + activityResponses.size() + " responses: " + activityResponses);
        log.debug("found labels with hash: " + labelHash);
        log.debug("found restrictions with hash: " + restrictionsHash);
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("atlToken", this.tokenGenerator.generateToken(ServletContextThreadLocal.getRequest()));
        result.put("activityResponses", activityResponses);
        result.put("labelsHash", labelHash);
        result.put("restrictionsHash", restrictionsHash);
        result.put("hasViewRestrictions", hasViewRestrictions);
        result.put("editMode", this.collaborativeEditingHelper.getEditMode(this.spaceKey));
        if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.spaceKey)) {
            List<Map<String, String>> contributors = this.getContributors();
            String currentContributorsHash = DigestUtils.md5Hex((String)contributors.stream().map(user -> (String)user.get("name")).sorted().collect(Collectors.joining(" ")));
            result.put("contributorsHash", currentContributorsHash);
            if (!currentContributorsHash.equals(this.contributorsHash)) {
                result.put("contributors", contributors);
            }
        }
        this.bean = result;
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            result.put("reason", AccessMode.READ_ONLY.name());
            ServletActionContext.getResponse().setStatus(405);
            return "error";
        }
        return "success";
    }

    @VisibleForTesting
    void updateDraftLastModificationDateIfNeeded() {
        try {
            Object draft;
            if (this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.spaceKey) && !this.darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(DISABLE_UNACTIVE_DRAFTS_AUTOSAVE) && (draft = ((ContentDraftManagerInternal)((Object)this.contentEntityManager)).findDraftFor(this.contentId)) != null) {
                int hoursDiff;
                Date now = new Date();
                Date draftDate = draft.getLastModificationDate();
                int n = hoursDiff = draftDate == null ? 0 : Hours.hoursBetween((ReadableInstant)new DateTime((Object)draftDate), (ReadableInstant)new DateTime((Object)now)).getHours();
                if (draftDate == null || (long)Math.abs(hoursDiff) > DRAFT_EDIT_PERIOD) {
                    draft.setLastModificationDate(now);
                    if (hoursDiff < 0) {
                        log.warn("Last modification date for the draft {} is far ahead from now. Draft modification lade is set to \"NOW\", but if it keeps happening please check clocks synchronisation between the servers", (Object)((ContentEntityObject)draft).getContentId());
                    }
                }
            }
        }
        catch (Exception e) {
            log.warn("Failed to update draft last modification date: {}", (Object)e.toString());
            log.debug("Failed to update draft last modification date", (Throwable)e);
        }
    }

    private List<Map<String, String>> getContributors() {
        try {
            Content draft = Content.builder().id(ContentId.deserialise((String)this.contentId.toString())).status(ContentStatus.DRAFT).build();
            return this.relationService.findSources((Relatable)draft, (RelationDescriptor)CollaboratorRelationDescriptor.COLLABORATOR).fetchMany((PageRequest)new SimplePageRequest(0, 50), new Expansion[0]).getResults().stream().map(this::getContributorData).collect(Collectors.toList());
        }
        catch (Exception e) {
            log.error("Error occurred while getting the contributors: {}", (Object)e.getMessage());
            return Collections.EMPTY_LIST;
        }
    }

    private Map<String, String> getContributorData(com.atlassian.confluence.api.model.people.User user) {
        HashMap<String, String> contributorsData = new HashMap<String, String>();
        contributorsData.put("fullname", user.getDisplayName());
        contributorsData.put("name", user.getUsername());
        contributorsData.put("avatarURL", user.getProfilePicture().getPath());
        return contributorsData;
    }

    private String getLastEdit(Draft d, I18NBean i18NBean) {
        if (d == null) {
            return null;
        }
        FriendlyDateFormatter friendlyDateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), this.getUserDateFormatter());
        Message message = friendlyDateFormatter.getFormatMessage(d.getLastModificationDate());
        String lastEditFriendlyDate = i18NBean.getText(message.getKey(), message.getArguments());
        return i18NBean.getText("heartbeat.last.edit", new String[]{lastEditFriendlyDate});
    }

    private DateFormatter getUserDateFormatter() {
        if (this.dateFormatter == null) {
            UserAccessor userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            this.dateFormatter = userAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
        }
        return this.dateFormatter;
    }

    private List<ActivityAjaxResponse> getActivityResponses(ContentEntityObject content, String activityKey, User currentUser) {
        ArrayList<ActivityAjaxResponse> activityResponses = new ArrayList<ActivityAjaxResponse>();
        if (!content.isDraft() && !this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(this.spaceKey)) {
            List<User> users;
            try {
                users = this.heartbeatManager.getUsersForActivity(activityKey);
            }
            catch (RuntimeException e) {
                log.error("Failed to get users for activity with key {}", (Object)(this.contentId + this.draftType));
                throw e;
            }
            I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(currentUser));
            for (User user : users) {
                if (currentUser != null && user.equals(currentUser)) continue;
                Draft draft = this.draftManager.findDraft(content.getId(), user.getName(), content.getType(), this.spaceKey);
                activityResponses.add(new ActivityAjaxResponse(user.getFullName(), this.getLastEdit(draft, i18NBean), user.getName()));
            }
        }
        return activityResponses;
    }

    @Override
    public Object getBean() {
        return this.bean;
    }
}

