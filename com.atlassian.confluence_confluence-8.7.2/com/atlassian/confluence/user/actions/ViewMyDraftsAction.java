/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.time.DateUtils
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.content.service.DraftService;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.event.events.profile.ViewMyDraftsEvent;
import com.atlassian.confluence.internal.ContentDraftManagerInternal;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.setup.settings.CollaborativeEditingHelper;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public class ViewMyDraftsAction
extends AbstractUserProfileAction
implements Beanable {
    private static final Integer LIMIT = 100;
    private DraftService draftService;
    private EventPublisher eventPublisher;
    private ContentDraftManagerInternal contentEntityManager;
    private CollaborativeEditingHelper collaborativeEditingHelper;
    private String draftId;
    private String editingUser;
    private Map<String, List<ContentEntityObject>> spaceToDrafts;
    private Map<Space, List<ContentEntityObject>> resumableDrafts;
    private Map<Space, List<ContentEntityObject>> nonResumableDrafts;
    private List<ContentEntityObject> unpublishedContentWithUserContributions;

    @Deprecated
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDelete() throws Exception {
        if (StringUtils.isBlank((CharSequence)this.draftId)) {
            this.addActionError("No draft ID found.");
            return "error";
        }
        try {
            this.draftService.removeDraft(Long.parseLong(this.draftId));
        }
        catch (NotValidException e) {
            this.addActionError("No draft ID found for id " + this.draftId);
            return "notfound";
        }
        catch (NotAuthorizedException e) {
            this.addActionError("Not authorized to delete draft " + this.draftId);
            return "notpermitted";
        }
        return "success";
    }

    public List<ContentEntityObject> getContentDrafts() {
        if (this.unpublishedContentWithUserContributions == null) {
            String userName = AuthenticatedUserThreadLocal.getUsername();
            this.unpublishedContentWithUserContributions = this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(null) ? this.filterSavingRaceConditionsForSharedDrafts(this.contentEntityManager.findUnpublishedContentWithUserContributions(userName)) : this.contentEntityManager.findAllDraftsWithUnpublishedChangesForUser(userName);
        }
        return this.unpublishedContentWithUserContributions;
    }

    private List<ContentEntityObject> filterSavingRaceConditionsForSharedDrafts(List<ContentEntityObject> allDraftsWithUnpublishedChangesForUser) {
        return allDraftsWithUnpublishedChangesForUser.stream().filter(content -> content instanceof Draft || !content.isDraft() || content.isUnpublished() || content.getLatestVersion() == null || DateUtils.round((Date)content.getLastModificationDate(), (int)13).compareTo(DateUtils.round((Date)((ContentEntityObject)content.getLatestVersion()).getLastModificationDate(), (int)13)) != 0).collect(Collectors.toList());
    }

    public Map<Space, List<ContentEntityObject>> getResumableDraftsBySpace() {
        if (this.resumableDrafts == null) {
            this.resumableDrafts = this.filterAndGroupDrafts(this::isResumableDraft);
        }
        return this.resumableDrafts;
    }

    public Map<Space, List<ContentEntityObject>> getNonResumableDraftsBySpace() {
        if (this.nonResumableDrafts == null) {
            this.nonResumableDrafts = this.filterAndGroupDrafts(this::isNonResumableDraft);
        }
        return this.nonResumableDrafts;
    }

    private Map<Space, List<ContentEntityObject>> filterAndGroupDrafts(Predicate<ContentEntityObject> filter) {
        return this.getContentDrafts().stream().filter(filter).filter(draft -> Objects.nonNull(DraftsTransitionHelper.getSpaceKey(draft)) && Objects.nonNull(this.spaceManager.getSpace(DraftsTransitionHelper.getSpaceKey(draft)))).sorted(Comparator.comparing(EntityObject::getLastModificationDate).reversed()).collect(Collectors.groupingBy(draft -> this.spaceManager.getSpace(DraftsTransitionHelper.getSpaceKey(draft)), () -> new TreeMap(Comparator.comparing(Space::getName, String.CASE_INSENSITIVE_ORDER)), Collectors.toList()));
    }

    private boolean isResumableDraft(ContentEntityObject draft) {
        if (ContentTypeEnum.CUSTOM.equals((Object)draft.getTypeEnum())) {
            return false;
        }
        boolean sharedDraftsFeatureEnabled = this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(DraftsTransitionHelper.getSpaceKey(draft));
        if (this.isLegacyDraft(draft)) {
            if (sharedDraftsFeatureEnabled) {
                return draft.isUnpublished() && !((Draft)draft).isBlank();
            }
            return !((Draft)draft).isBlank();
        }
        return sharedDraftsFeatureEnabled;
    }

    private boolean isNonResumableDraft(ContentEntityObject draft) {
        return this.isDraftAvailable(draft) && !this.isResumableDraft(draft);
    }

    private boolean isDraftAvailable(ContentEntityObject draft) {
        if (this.isLegacyDraft(draft)) {
            return !((Draft)draft).isBlank();
        }
        return true;
    }

    @Deprecated
    public Map<String, List<ContentEntityObject>> getDraftsBySpace() {
        if (this.spaceToDrafts == null) {
            this.spaceToDrafts = this.getContentDrafts().stream().filter(this::isDraftAvailable).sorted(Comparator.comparing(EntityObject::getLastModificationDate).reversed()).collect(Collectors.groupingBy(DraftsTransitionHelper::getSpaceKey));
        }
        return this.spaceToDrafts;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        User user = (User)FlashScope.get("editingUser");
        if (user != null) {
            this.editingUser = user.getFullName();
        }
        ViewMyDraftsEvent event = new ViewMyDraftsEvent(this);
        this.eventPublisher.publish((Object)event);
        return super.execute();
    }

    @Override
    public Object getBean() {
        HashMap<String, Collection> result = new HashMap<String, Collection>();
        if (this.hasActionErrors()) {
            result.put("actionErrors", this.getActionErrors());
        }
        return result;
    }

    public boolean isLegacyDraft(ContentEntityObject contentEntityObject) {
        return DraftsTransitionHelper.isLegacyDraft(contentEntityObject);
    }

    public boolean isViewableDraft(ContentEntityObject contentEntityObject) {
        return !ContentTypeEnum.CUSTOM.equals((Object)contentEntityObject.getTypeEnum());
    }

    public boolean isDiscardableDraft(ContentEntityObject contentEntityObject) {
        return this.isLegacyDraft(contentEntityObject) || this.collaborativeEditingHelper.isSharedDraftsFeatureEnabled(null);
    }

    @Deprecated
    public List<Draft> getDrafts() {
        return this.draftService.findDrafts(LIMIT, 0);
    }

    public DraftService getDraftService() {
        return this.draftService;
    }

    public void setDraftService(DraftService draftService) {
        this.draftService = draftService;
    }

    public String getDraftId() {
        return this.draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public SpaceManager getSpaceManager() {
        return this.spaceManager;
    }

    @Internal
    public String getEditingUser() {
        return this.editingUser;
    }

    public ContentDraftManagerInternal getContentEntityManager() {
        return this.contentEntityManager;
    }

    public void setContentEntityManager(ContentDraftManagerInternal contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public CollaborativeEditingHelper getCollaborativeEditingHelper() {
        return this.collaborativeEditingHelper;
    }

    public void setCollaborativeEditingHelper(CollaborativeEditingHelper collaborativeEditingHelper) {
        this.collaborativeEditingHelper = collaborativeEditingHelper;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

