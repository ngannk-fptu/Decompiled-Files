/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.sharelinksbookmarklet.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.user.User;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class BookmarkletAction
extends ConfluenceActionSupport {
    private SpaceManager spaceManager;
    private List<Space> globalSpaces;
    private List<Space> favouriteSpaces;
    private Space personalSpace;
    private String loginURL;
    private String bookmarkedURL;

    public String execute() throws Exception {
        ConfluenceUser authenticatedUser = this.getAuthenticatedUser();
        SpacesQuery globalSpacesListBuilder = SpacesQuery.newQuery().forUser((User)authenticatedUser).withSpaceType(SpaceType.GLOBAL).withPermission("EDITSPACE").build();
        this.globalSpaces = this.spaceManager.getAllSpaces(globalSpacesListBuilder);
        if (authenticatedUser != null) {
            this.personalSpace = this.spaceManager.getPersonalSpace(authenticatedUser);
        }
        this.favouriteSpaces = this.computeFavouriteSpaces(this.globalSpaces);
        this.globalSpaces.removeAll(this.favouriteSpaces);
        this.loginURL = this.computeLoginURL();
        return "success";
    }

    public List<Space> getAvailableGlobalSpaces() {
        return this.globalSpaces;
    }

    public String getPersonalSpaceKey() {
        return this.personalSpace != null ? this.personalSpace.getKey() : null;
    }

    public String getLoginURL() {
        return this.loginURL;
    }

    private String computeLoginURL() throws UnsupportedEncodingException {
        HttpServletRequest request = ServletContextThreadLocal.getRequest();
        String contextPath = request.getContextPath();
        Object currentURLWithoutContextPath = request.getRequestURI().substring(contextPath.length());
        if (request.getQueryString() != null) {
            currentURLWithoutContextPath = (String)currentURLWithoutContextPath + "?" + request.getQueryString();
        }
        return contextPath + "/login.action?os_destination=" + URLEncoder.encode((String)currentURLWithoutContextPath, "UTF-8");
    }

    private List<Space> computeFavouriteSpaces(List<Space> permittedGlobalSpaces) {
        if (this.getAuthenticatedUser() == null) {
            return Collections.emptyList();
        }
        List favouriteSpaces = this.labelManager.getFavouriteSpaces(this.getAuthenticatedUser().getName());
        if (this.personalSpace != null) {
            favouriteSpaces.remove(this.personalSpace);
        }
        if (permittedGlobalSpaces.isEmpty()) {
            favouriteSpaces.removeIf(space -> !this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), space, Page.class));
        } else {
            favouriteSpaces.retainAll(permittedGlobalSpaces);
        }
        return favouriteSpaces;
    }

    public List<Space> getFavouriteSpaces() {
        return this.favouriteSpaces;
    }

    public String getBookmarkedURL() {
        return this.bookmarkedURL;
    }

    public void setBookmarkedURL(String bookmarkedURL) {
        this.bookmarkedURL = bookmarkedURL;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

