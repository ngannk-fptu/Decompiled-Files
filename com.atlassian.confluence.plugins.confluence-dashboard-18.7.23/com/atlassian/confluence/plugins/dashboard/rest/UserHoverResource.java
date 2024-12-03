/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.follow.FollowManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UnknownUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserDetailsManager
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.dashboard.rest;

import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/user-hover")
@AnonymousAllowed
public class UserHoverResource {
    private final PermissionManager permissionManager;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final FollowManager followManager;
    private final UserDetailsManager userDetailsManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;

    public UserHoverResource(PermissionManager permissionManager, ConfluenceAccessManager confluenceAccessManager, UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, FollowManager followManager, UserDetailsManager userDetailsManager, SpaceManager spaceManager, PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.confluenceAccessManager = confluenceAccessManager;
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.followManager = followManager;
        this.userDetailsManager = userDetailsManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
    }

    private boolean currentUserCanFollowOthers() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        return currentUser != null && this.confluenceAccessManager.getUserAccessStatus((User)currentUser).hasLicensedAccess();
    }

    private boolean isPermittedToView(User user) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)user);
    }

    private User getUser(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null && username != null) {
            user = UnknownUser.unknownUser((String)username, (I18NBean)this.i18NBeanFactory.getI18NBean());
        }
        return user;
    }

    private boolean canFollowUser(User followee) {
        return this.currentUserCanFollowOthers() && this.isPermittedToView(followee) && followee != null && this.confluenceAccessManager.getUserAccessStatus(followee).hasLicensedAccess();
    }

    private boolean isFollowing(User user) {
        return this.followManager.isUserFollowing((User)AuthenticatedUserThreadLocal.get(), user);
    }

    private Map<String, String> getUserDetails(User user, String profileGroup) {
        HashMap<String, String> userDetails = new HashMap<String, String>();
        if (this.isPermittedToView(user)) {
            List profileKeys = this.userDetailsManager.getProfileKeys(profileGroup);
            if (profileKeys == null) {
                return userDetails;
            }
            profileKeys.forEach(key -> {
                String value = this.userDetailsManager.getStringProperty(user, key);
                if (value != null) {
                    userDetails.put((String)key, value);
                }
            });
        }
        return userDetails;
    }

    private boolean hasPersonalSpace(User user) {
        Space personalSpace = this.spaceManager.getPersonalSpace(FindUserHelper.getUser((User)user));
        return personalSpace != null && this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)personalSpace);
    }

    private boolean hasBlog(User user) {
        Space space = this.spaceManager.getPersonalSpace(FindUserHelper.getUser((User)user));
        if (space == null) {
            return false;
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)space)) {
            return false;
        }
        List blogs = this.pageManager.getBlogPosts(space, false);
        return !blogs.isEmpty();
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/")
    public Response userHoverData(@QueryParam(value="username") String username) {
        User user = this.getUser(username);
        JSONObject data = new JSONObject();
        data.put("userName", (Object)username);
        data.put("userDetails", this.getUserDetails(user, "business"));
        data.put("canFollow", this.canFollowUser(user));
        data.put("isFollowing", this.isFollowing(user));
        data.put("hasPersonalSpace", this.hasPersonalSpace(user));
        data.put("hasBlog", this.hasBlog(user));
        return Response.ok((Object)data.toString()).build();
    }
}

