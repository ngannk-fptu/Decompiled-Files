/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.people.Anonymous
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.people.KnownUser$Builder
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UnknownUser
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.confluence.api.model.people.UserStatus
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.people.Anonymous;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UnknownUser;
import com.atlassian.confluence.api.model.people.User;
import com.atlassian.confluence.api.model.people.UserStatus;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PersonFactory
extends ModelFactory<ConfluenceUser, User> {
    private final UserAccessor userAccessor;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final I18NBeanFactory i18nBeanFactory;
    private static final int DEFAULT_ICON_HEIGHT = 48;
    private static final int DEFAULT_ICON_WIDTH = 48;

    public PersonFactory(UserAccessor userAccessor, WebResourceUrlProvider webResourceUrlProvider, I18NBeanFactory i18nBeanFactory) {
        this.userAccessor = Objects.requireNonNull(userAccessor);
        this.webResourceUrlProvider = Objects.requireNonNull(webResourceUrlProvider);
        this.i18nBeanFactory = Objects.requireNonNull(i18nBeanFactory);
    }

    public Person forUsername(String username) {
        if (username == null) {
            return this.anonymous();
        }
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            return this.unknownUser(username);
        }
        return this.knownUser(user);
    }

    public Person forUser(ConfluenceUser user, Expansions expansions) {
        if (user == null) {
            return this.anonymous();
        }
        return this.knownUser(user, expansions);
    }

    public Person forUser(ConfluenceUser user) {
        return this.forUser(user, Expansions.EMPTY);
    }

    public @NonNull Person forCurrentUser(Expansions expansions) {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        return this.forUser(confluenceUser, expansions);
    }

    public @NonNull Person forCurrentUser() {
        return this.forCurrentUser(Expansions.EMPTY);
    }

    public User fromUser(@NonNull ConfluenceUser user, Expansions expansions) {
        return this.knownUser((ConfluenceUser)Preconditions.checkNotNull((Object)user), expansions);
    }

    public User fromUser(@NonNull ConfluenceUser user) {
        return this.fromUser(user, Expansions.EMPTY);
    }

    public Anonymous anonymous() {
        return new Anonymous(new Icon(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/anonymous.svg", 48, 48, true), this.lookupAnonymousDisplayName());
    }

    private Person unknownUser(String username) {
        return new UnknownUser(new Icon(this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + "/images/icons/profilepics/default.svg", 48, 48, true), username, this.lookupFullNameForUnknownUser(username), null);
    }

    private User knownUser(ConfluenceUser user) {
        return this.knownUser(user, Expansions.EMPTY);
    }

    private User knownUser(ConfluenceUser user, Expansions expansions) {
        ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture(user);
        Icon profilePicture = new Icon(userProfilePicture.getUriReference(), 48, 48, userProfilePicture.isDefault());
        KnownUser.Builder builder = KnownUser.builder().userKey(user.getKey()).username(user.getName()).displayName(user.getFullName()).profilePicture(profilePicture);
        if (expansions.canExpand("status")) {
            UserStatus status = this.userAccessor.isDeactivated(user) ? UserStatus.DEACTIVATED : UserStatus.CURRENT;
            builder.status(status);
        }
        return builder.build();
    }

    private String lookupFullNameForUnknownUser(String userName) {
        return this.i18nBeanFactory.getI18NBean().getText("unknown.name", new Object[]{userName});
    }

    private String lookupAnonymousDisplayName() {
        return this.i18nBeanFactory.getI18NBean().getText("anonymous.name");
    }

    @Override
    public User buildFrom(ConfluenceUser hibernateObject, Expansions expansions) {
        return this.fromUser(hibernateObject, expansions);
    }
}

