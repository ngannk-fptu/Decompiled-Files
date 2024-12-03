/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.service.factory;

import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.plugins.mobile.dto.UserDto;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PersonFactory {
    public static final String ANONYMOUS_NAME_KEY = "anonymous.name";
    public static final int DEFAULT_ICON_HEIGHT = 48;
    public static final int DEFAULT_ICON_WIDTH = 48;
    private final UserAccessor userAccessor;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final I18NBeanFactory i18nBeanFactory;

    @Autowired
    public PersonFactory(@ComponentImport UserAccessor userAccessor, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport @Qualifier(value="i18NBeanFactory") I18NBeanFactory i18nBeanFactory) {
        this.userAccessor = userAccessor;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.i18nBeanFactory = i18nBeanFactory;
    }

    public Person forUser(ConfluenceUser user) {
        if (user == null) {
            return this.anonymous();
        }
        return this.knownUser(user);
    }

    public UserDto forUserDto(ConfluenceUser user) {
        return this.userDto(user);
    }

    public Person forUser(String userName) {
        return this.forUser(this.userAccessor.getUserByName(userName));
    }

    public Anonymous anonymous() {
        return new Anonymous(this.getProfilePicture(null), this.lookupAnonymousDisplayName());
    }

    private User knownUser(ConfluenceUser user) {
        return new KnownUser(this.getProfilePicture(user), user.getName(), user.getFullName(), user.getKey());
    }

    private UserDto userDto(ConfluenceUser user) {
        if (user == null) {
            return UserDto.builder().profilePicture(this.getProfilePicture(null)).displayName(this.lookupAnonymousDisplayName()).build();
        }
        return UserDto.builder().profilePicture(this.getProfilePicture(user)).username(user.getName()).displayName(user.getFullName()).userKey(user.getKey().getStringValue()).email(user.getEmail()).build();
    }

    private Icon getProfilePicture(ConfluenceUser user) {
        String path = "/images/icons/profilepics/anonymous.png";
        boolean isDefault = true;
        if (user != null) {
            ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture((com.atlassian.user.User)user);
            path = userProfilePicture.getDownloadPath();
            isDefault = userProfilePicture.isDefault();
        }
        return new Icon(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + path, 48, 48, isDefault);
    }

    private String lookupAnonymousDisplayName() {
        return this.i18nBeanFactory.getI18NBean().getText(ANONYMOUS_NAME_KEY);
    }
}

