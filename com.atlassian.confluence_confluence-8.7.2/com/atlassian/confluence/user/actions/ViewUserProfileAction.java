/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.SpaceComparator;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiresAnyConfluenceAccess
public class ViewUserProfileAction
extends AbstractUserProfileAction
implements FormAware,
UserAware {
    private static final Logger log = LoggerFactory.getLogger(ViewUserProfileAction.class);
    private PageManager pageManager;
    private ContentEntityManager contentEntityManager;
    private String username;
    private List recentlyUpdatedContent;
    private List favouriteSpaces;
    private static final int MAX_RECENTLY_UPDATED_CONTENT = 20;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        if (this.getUser() == null) {
            return "pagenotfound";
        }
        if (this.getPersonalInformationEntity() != null) {
            this.addToHistory(this.getPersonalInformationEntity());
        }
        return "success";
    }

    public String getPathToProfilePicture() {
        String filename = this.getUserPreferences().getString("confluence.user.profile.picture");
        if (this.getPersonalInformationEntity() != null) {
            for (Attachment o : this.getPersonalInformationEntity().getAttachments()) {
                Attachment attachment = o;
                if (!attachment.getFileName().equals(filename)) continue;
                return attachment.getDownloadPath();
            }
        }
        return null;
    }

    public List<String> getUserDetailsKeys(String groupKey) {
        return this.userDetailsManager.getProfileKeys(groupKey);
    }

    public List<String> getUserDetailsGroups() {
        return this.userDetailsManager.getProfileGroups();
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public String getUsername() {
        if ((this.username == null || StringUtils.isEmpty((CharSequence)this.username)) && this.getAuthenticatedUser() != null) {
            this.username = this.getAuthenticatedUser().getName();
        }
        return this.username;
    }

    public void setUsername(String username) {
        if (HtmlUtil.shouldUrlDecode(username)) {
            username = HtmlUtil.urlDecode(username);
        }
        this.username = username;
    }

    public Date getSignupDate() {
        try {
            return this.getDateProperty("confluence.user.signup.date");
        }
        catch (Exception e) {
            log.warn("An error occurred trying to retrieve signup date: " + e, (Throwable)e);
            return null;
        }
    }

    public Date getLastLoginDate() {
        return this.getPreviousLoginDate();
    }

    private Date getDateProperty(String propertyKey) {
        PropertySet propertySet = this.userAccessor.getPropertySet(this.getUser());
        if (propertySet.exists(propertyKey)) {
            try {
                long aLong = propertySet.getLong(propertyKey);
                if (aLong > 0L) {
                    return new Date(aLong);
                }
                return propertySet.getDate(propertyKey);
            }
            catch (PropertyException e) {
                return propertySet.getDate(propertyKey);
            }
        }
        return null;
    }

    public int getAuthoredPagesCount() {
        return this.pageManager.getAuthoredPagesCountByUser(this.username);
    }

    public List getRecentlyUpdatedContent() {
        if (this.recentlyUpdatedContent == null) {
            ArrayList<PermissionManager.Criterion> criteria = new ArrayList<PermissionManager.Criterion>(1);
            criteria.add((alreadyChosenEntities, entity) -> {
                if (entity instanceof ContentEntityObject) {
                    ContentEntityObject ceo = (ContentEntityObject)entity;
                    long ceoId = ((ContentEntityObject)ceo.getLatestVersion()).getId();
                    for (Object alreadyChosenEntity : alreadyChosenEntities) {
                        ContentEntityObject ceo2 = (ContentEntityObject)alreadyChosenEntity;
                        long ceo2Id = ((ContentEntityObject)ceo2.getLatestVersion()).getId();
                        if (ceoId != ceo2Id) continue;
                        return false;
                    }
                    return true;
                }
                return false;
            });
            this.recentlyUpdatedContent = this.getPermittedEntitiesOf(this.contentEntityManager.getRecentlyModifiedEntitiesForUser(this.username), 20, criteria);
        }
        return this.recentlyUpdatedContent;
    }

    public List getAuthoredSpaces() {
        return this.getPermittedEntitiesOf(this.spaceManager.getAuthoredSpacesByUser(this.username));
    }

    @Override
    public boolean isEmailVisible() {
        return this.isMyProfile() || super.isEmailVisible();
    }

    public String renderEmail(String email) {
        return GeneralUtil.maskEmail(email);
    }

    public List getFavouriteSpaces() {
        if (this.favouriteSpaces == null) {
            this.favouriteSpaces = AuthenticatedUserThreadLocal.get() == null ? Collections.EMPTY_LIST : this.getPermittedEntitiesOf(this.labelManager.getFavouriteSpaces(this.getAuthenticatedUser().getName()));
            Collections.sort(this.favouriteSpaces, new SpaceComparator());
        }
        return this.favouriteSpaces;
    }

    @Override
    public boolean isEditMode() {
        return false;
    }

    @Override
    protected List<String> getPermissionTypes() {
        List<String> permissionTypes = super.getPermissionTypes();
        if (this.shouldCheckBrowseUsersPermission(this.getAuthenticatedUser())) {
            this.addPermissionTypeTo("VIEWUSERPROFILES", permissionTypes);
        }
        return permissionTypes;
    }

    private boolean shouldCheckBrowseUsersPermission(ConfluenceUser user) {
        return user == null || !this.getConfluenceAccessManager().getUserAccessStatus(user).hasLicensedAccess();
    }

    @Override
    public String getEmail() {
        return super.getEmail() == null ? null : GeneralUtil.maskEmail(super.getEmail().toString());
    }
}

