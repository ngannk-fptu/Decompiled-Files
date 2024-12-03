/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.follow.FollowManager
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UserDetailsManager
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.user.actions.UserAware
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.confluence.userstatus.StatusTextRenderer
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.UserProfileActionBreadcrumb
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.user.profile;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.userstatus.StatusTextRenderer;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.UserProfileActionBreadcrumb;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractUserProfileAction
extends ConfluenceActionSupport
implements BreadcrumbAware,
UserAware {
    public static final String HTML_FRIENDLY_NAME = "HTML";
    public static final String TEXT_FRIENDLY_NAME = "Text";
    public static final String HTML_MIME_TYPE = "text/html";
    public static final String TEXT_MIME_TYPE = "text/plain";
    public static final String HOMEPAGE_DASHBOARD = "dashboard";
    public static final String HOMEPAGE_SITEHOME = "siteHomepage";
    public static final String HOMEPAGE_PROFILE = "profile";
    private ConfluenceUser user;
    private PersonalInformation personalInformationEntity;
    private String personalInformation;
    private UserPreferences userPreferences;
    private List attachments;
    protected StatusTextRenderer statusTextRenderer;
    protected WikiStyleRenderer wikiStyleRenderer;
    protected FollowManager followManager;
    protected FavouriteManager favouriteManager;
    protected NotificationManager notificationManager;
    protected PersonalInformationManager personalInformationManager;
    protected AttachmentManager attachmentManager;
    protected SpaceManager spaceManager;
    protected UserDetailsManager userDetailsManager;

    public ConfluenceUser getUser() {
        if (this.user == null && StringUtils.isNotEmpty((CharSequence)this.getUsername())) {
            this.user = this.userAccessor.getUserByName(this.getUsername());
        }
        return this.user;
    }

    public Object getFullName() {
        return this.getUser() == null ? null : this.getUser().getFullName();
    }

    public Object getEmail() {
        return this.getUser() == null ? null : this.getUser().getEmail();
    }

    protected UserPreferences getUserPreferences() {
        if (this.userPreferences == null) {
            this.userPreferences = new UserPreferences(this.userAccessor.getPropertySet(this.getUser()));
        }
        return this.userPreferences;
    }

    public PersonalInformation getPersonalInformationEntity() {
        ConfluenceUser user = this.getUser();
        if (this.personalInformationEntity == null && user != null) {
            this.personalInformationEntity = this.personalInformationManager.getOrCreatePersonalInformation((User)user);
        }
        return this.personalInformationEntity;
    }

    public String getPersonalInformation() {
        PersonalInformation pi;
        if (this.personalInformation == null && (pi = this.getPersonalInformationEntity()) != null) {
            this.personalInformation = pi.getBodyAsString();
        }
        return this.personalInformation;
    }

    public void setPersonalInformation(String personalInformation) {
        this.personalInformation = personalInformation;
    }

    public String getUsername() {
        if (this.getAuthenticatedUser() != null) {
            return this.getAuthenticatedUser().getName();
        }
        return null;
    }

    public boolean isMyProfile() {
        return this.getPersonalInformationEntity().belongsTo((User)this.getAuthenticatedUser());
    }

    public ProfilePictureInfo getUserProfilePicture() {
        return this.userAccessor.getUserProfilePicture((User)this.getAuthenticatedUser());
    }

    public List getAttachments() {
        if (this.attachments == null) {
            if (this.getPersonalInformationEntity() == null) {
                return null;
            }
            this.attachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)this.getPersonalInformationEntity());
        }
        return this.attachments;
    }

    public final void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public final void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public final void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public final void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    protected String getPersonalSpaceKey(User user) {
        return "~" + user.getName();
    }

    public String getPageTitle() {
        return this.getUser().getFullName();
    }

    public WebInterfaceContext getWebInterfaceContext() {
        DefaultWebInterfaceContext result = DefaultWebInterfaceContext.copyOf((WebInterfaceContext)super.getWebInterfaceContext());
        result.setPersonalInformation(this.getPersonalInformationEntity());
        return result;
    }

    public boolean isHasAboutMe() {
        return StringUtils.isNotBlank((CharSequence)this.getPersonalInformation());
    }

    public Object getRenderedAboutMe() {
        String pi = this.getPersonalInformation();
        if (pi == null) {
            return null;
        }
        return new HtmlFragment((Object)this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)this.getPersonalInformationEntity().toPageContext(), pi));
    }

    public Breadcrumb getBreadcrumb() {
        return new UserProfileActionBreadcrumb((ConfluenceActionSupport)this);
    }

    public Object getUserProperty(String key) {
        return new HtmlFragment((Object)this.userDetailsManager.getStringProperty((User)this.getUser(), key));
    }

    public final void setUserDetailsManager(UserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    public final void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public final void setFollowManager(FollowManager followManager) {
        this.followManager = followManager;
    }

    public final void setFavouriteManager(FavouriteManager favouriteManager) {
        this.favouriteManager = favouriteManager;
    }

    public StatusTextRenderer getStatusTextRenderer() {
        return this.statusTextRenderer;
    }

    public void setStatusTextRenderer(StatusTextRenderer statusTextRenderer) {
        this.statusTextRenderer = statusTextRenderer;
    }

    public boolean isFollowing() {
        return this.followManager.isUserFollowing((User)this.getAuthenticatedUser(), (User)this.getUser());
    }

    public boolean isFavourite() {
        Space personalSpace = this.spaceManager.getPersonalSpace(this.getUser());
        return this.favouriteManager.isUserFavourite((User)this.getAuthenticatedUser(), personalSpace);
    }

    public boolean hasPersonalSpace() {
        return this.spaceManager.getPersonalSpace(this.getUser()) != null;
    }

    public boolean currentUserHasLicensedAccess() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return currentUser != null && this.getConfluenceAccessManager().getUserAccessStatus((User)currentUser).hasLicensedAccess();
    }

    public final boolean isUserRequired() {
        return true;
    }

    public final boolean isViewPermissionRequired() {
        return true;
    }

    public Map<String, Object> getContext() {
        Map map = super.getContext();
        map.put("user", this.getUser());
        map.put("personalInformationEntity", this.getPersonalInformationEntity());
        map.put("statusTextRenderer", this.getStatusTextRenderer());
        return map;
    }
}

