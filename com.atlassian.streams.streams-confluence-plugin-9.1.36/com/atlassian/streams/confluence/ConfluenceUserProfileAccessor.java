/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.streams.api.UserProfile
 *  com.atlassian.streams.api.UserProfile$Builder
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.StreamsI18nResolver
 *  com.atlassian.streams.spi.UserProfileAccessor
 *  com.atlassian.user.User
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.UserProfile;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.spi.StreamsI18nResolver;
import com.atlassian.streams.spi.UserProfileAccessor;
import com.atlassian.user.User;
import java.net.URI;

public class ConfluenceUserProfileAccessor
implements UserProfileAccessor {
    private final UserManager userManager;
    private final ApplicationProperties applicationProperties;
    private final StreamsI18nResolver i18nResolver;
    private final SpacePermissionManager spacePermissionManager;

    public ConfluenceUserProfileAccessor(UserManager userManager, ApplicationProperties applicationProperties, StreamsI18nResolver i18nResolver, SpacePermissionManager spacePermissionManager) {
        this.userManager = userManager;
        this.applicationProperties = applicationProperties;
        this.i18nResolver = i18nResolver;
        this.spacePermissionManager = spacePermissionManager;
    }

    private URI getUserProfileUri(URI baseUri, String username) {
        return URI.create(baseUri.toASCIIString() + "/display/~" + Uris.encode((String)username));
    }

    private URI getProfilePictureUri(URI baseUri, com.atlassian.sal.api.user.UserProfile user) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        boolean viewUserProfiles = this.spacePermissionManager.hasPermission("VIEWUSERPROFILES", null, (User)currentUser);
        if (currentUser == null && !viewUserProfiles) {
            return this.getDefaultProfilePicture(baseUri);
        }
        URI profilePictureUri = user.getProfilePictureUri();
        if (profilePictureUri == null) {
            return this.getDefaultProfilePicture(baseUri);
        }
        return profilePictureUri.isAbsolute() ? profilePictureUri : URI.create(baseUri.toASCIIString() + profilePictureUri);
    }

    private URI getDefaultProfilePicture(URI baseUri) {
        return URI.create(baseUri.toASCIIString() + "/images/icons/profilepics/default.png");
    }

    public UserProfile getAnonymousUserProfile(URI baseUri) {
        return new UserProfile.Builder(this.i18nResolver.getText("streams.confluence.authors.unknown.username")).fullName(this.i18nResolver.getText("streams.confluence.authors.unknown.fullname")).profilePictureUri(Option.some((Object)this.getAnonymousProfilePictureUri(baseUri))).build();
    }

    private URI getAnonymousProfilePictureUri(URI baseUri) {
        return URI.create(baseUri + "/images/icons/profilepics/anonymous.png");
    }

    public UserProfile getUserProfile(String username) {
        return this.getUserProfile(URI.create(this.applicationProperties.getBaseUrl()), username);
    }

    public UserProfile getAnonymousUserProfile() {
        return this.getAnonymousUserProfile(URI.create(this.applicationProperties.getBaseUrl()));
    }

    public UserProfile getUserProfile(URI baseUri, String username) {
        if (username == null) {
            return this.getAnonymousUserProfile(baseUri);
        }
        com.atlassian.sal.api.user.UserProfile user = this.userManager.getUserProfile(username);
        if (user == null) {
            return new UserProfile.Builder(username).profilePictureUri(Option.some((Object)this.getAnonymousProfilePictureUri(baseUri))).build();
        }
        return new UserProfile.Builder(username).fullName(user.getFullName()).email(Option.option((Object)user.getEmail())).profilePageUri(Option.option((Object)this.getUserProfileUri(baseUri, username))).profilePictureUri(Option.option((Object)this.getProfilePictureUri(baseUri, user))).build();
    }
}

