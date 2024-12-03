/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.links.linktypes.UserProfileLink
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.PersonalInformationManager
 *  com.atlassian.confluence.user.UnknownUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserDetailsManager
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.rest.dto;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.links.linktypes.UserProfileLink;
import com.atlassian.confluence.plugins.rest.dto.UserDto;
import com.atlassian.confluence.plugins.rest.dto.UserDtoFactory;
import com.atlassian.confluence.plugins.rest.entities.UserPreferencesDto;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserDetailsManager;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserDtoFactoryImpl
implements UserDtoFactory {
    static final String ANON_PROFILE_PIC_PATH = "/images/icons/profilepics/anonymous.svg";
    private final UserAccessor userAccessor;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final ContextPathHolder contextPathHolder;
    private final UserDetailsManager userDetailsManager;
    private final WikiStyleRenderer wikiStyleRenderer;
    private final PersonalInformationManager personalInformationManager;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final PermissionManager permissionManager;
    private final GlobalSettingsManager settingsManager;

    public UserDtoFactoryImpl(UserAccessor userAccessor, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, ContextPathHolder contextPathHolder, UserDetailsManager userDetailsManager, WikiStyleRenderer wikiStyleRenderer, PersonalInformationManager personalInformationManager, WebResourceUrlProvider webResourceUrlProvider, PermissionManager permissionManager, GlobalSettingsManager settingsManager) {
        this.userAccessor = userAccessor;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.contextPathHolder = contextPathHolder;
        this.userDetailsManager = userDetailsManager;
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.personalInformationManager = personalInformationManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.permissionManager = permissionManager;
        this.settingsManager = settingsManager;
    }

    @Override
    public UserDto getUserDto(@Nullable ConfluenceUser targetUser) {
        return this.getUserDto(targetUser, AuthenticatedUserThreadLocal.get());
    }

    UserDto getUserDto(ConfluenceUser targetUser, ConfluenceUser viewingUser) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)viewingUser));
        if (targetUser != null) {
            if (UnknownUser.isUnknownUser((User)targetUser)) {
                return this.unknown(targetUser);
            }
            String phone = this.userDetailsManager.getStringProperty((User)targetUser, "phone");
            String email = GeneralUtil.maskEmail((String)targetUser.getEmail(), (Settings)this.settingsManager.getGlobalSettings());
            String position = this.userDetailsManager.getStringProperty((User)targetUser, "position");
            String department = this.userDetailsManager.getStringProperty((User)targetUser, "department");
            String location = this.userDetailsManager.getStringProperty((User)targetUser, "location");
            String aboutMe = this.renderAboutMe(targetUser);
            String fullName = targetUser.getFullName();
            String avatarUrl = this.getAvatarUrl(targetUser, viewingUser);
            String url = this.contextPathHolder.getContextPath() + UserProfileLink.getLinkPath((String)targetUser.getName());
            UserPreferencesDto userPreferences = this.getUserPreferences(targetUser);
            boolean anonymous = false;
            return new UserDto(targetUser.getName(), fullName, avatarUrl, url, phone, email, position, department, location, aboutMe, userPreferences, false, false);
        }
        return this.anonymous(i18NBean);
    }

    private UserDto unknown(ConfluenceUser targetUser) {
        return new UserDto(targetUser.getName(), targetUser.getFullName(), this.getAnonymousProfilePicUrl(), "", "", "", "", "", "", "", new UserPreferencesDto(), false, true);
    }

    private UserDto anonymous(I18NBean i18NBean) {
        return new UserDto(i18NBean.getText("anonymous.name"), i18NBean.getText("anonymous.name"), this.getAnonymousProfilePicUrl(), "", "", "", "", "", "", "", new UserPreferencesDto(), true, false);
    }

    private String renderAboutMe(ConfluenceUser targetUser) {
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation((User)targetUser);
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)personalInformation.toPageContext(), personalInformation.getBodyAsString());
    }

    private UserPreferencesDto getUserPreferences(ConfluenceUser user) {
        UserPreferences userPreferences = new UserPreferences(this.userAccessor.getPropertySet(user));
        return new UserPreferencesDto(userPreferences.getBoolean("confluence.prefs.watch.my.own.content"));
    }

    private String getAvatarUrl(ConfluenceUser targetUser, ConfluenceUser viewingUser) {
        ProfilePictureInfo userProfilePicture = this.userAccessor.getUserProfilePicture((User)targetUser);
        if (userProfilePicture.isAnonymousPicture() || !this.permissionManager.hasPermission((User)viewingUser, Permission.VIEW, (Object)targetUser)) {
            return this.getAnonymousProfilePicUrl();
        }
        return userProfilePicture.getUriReference();
    }

    private String getAnonymousProfilePicUrl() {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + ANON_PROFILE_PIC_PATH;
    }
}

