/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.legacyapi.model.content.Permission
 *  com.atlassian.confluence.legacyapi.model.people.Anonymous
 *  com.atlassian.confluence.legacyapi.model.people.KnownUser
 *  com.atlassian.confluence.legacyapi.model.people.Person
 *  com.atlassian.confluence.legacyapi.model.people.UnknownUser
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ui.rest.builder;

import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.legacyapi.model.content.Permission;
import com.atlassian.confluence.legacyapi.model.people.Anonymous;
import com.atlassian.confluence.legacyapi.model.people.KnownUser;
import com.atlassian.confluence.legacyapi.model.people.Person;
import com.atlassian.confluence.legacyapi.model.people.UnknownUser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public class LegacyPersonBuilder {
    private final UserAccessor userAccessor;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final PermissionManager permissionManager;
    private static final int DEFAULT_ICON_HEIGHT = 48;
    private static final int DEFAULT_ICON_WIDTH = 48;

    @Autowired
    public LegacyPersonBuilder(@ComponentImport UserAccessor userAccessor, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport PermissionManager permissionManager) {
        this.userAccessor = userAccessor;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.permissionManager = permissionManager;
    }

    public Person forUsername(String username) {
        if (username == null) {
            return this.anonymous();
        }
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null || !this.canView((User)user)) {
            return this.unknownUser(username);
        }
        return this.knownUser((User)user);
    }

    public Anonymous anonymous() {
        return new Anonymous(new Icon(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/anonymous.svg", 48, 48, true));
    }

    private Person unknownUser(String username) {
        return new UnknownUser(new Icon(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/default.svg", 48, 48, true), username);
    }

    private Person knownUser(User user) {
        ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture(user);
        return new KnownUser(new Icon(userProfilePicture.getUriReference(), 48, 48, userProfilePicture.isDefault()), user.getName(), user.getFullName(), Collections.singletonMap(Permission.VIEW_PROFILE, this.canView(user)));
    }

    private boolean canView(User user) {
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), com.atlassian.confluence.security.Permission.VIEW, (Object)user);
    }
}

