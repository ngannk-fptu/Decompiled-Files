/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.impl;

import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultAuditHelper
implements AuditHelper {
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final SpaceManagerInternal spaceManager;
    private final ConfluenceUserResolver userResolver;

    public DefaultAuditHelper(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, SpaceManagerInternal spaceManager, ConfluenceUserResolver userResolver) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.spaceManager = spaceManager;
        this.userResolver = userResolver;
    }

    @Override
    public String translate(String key) {
        return this.getI18NBean().getText(key);
    }

    private I18NBean getI18NBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
    }

    @Override
    public @Nullable String fetchSpaceId(@Nullable String spaceKey) {
        return Optional.ofNullable(spaceKey).map(this.spaceManager::getSpace).map(EntityObject::getId).map(String::valueOf).orElse(null);
    }

    @Override
    public @Nullable String fetchSpaceDisplayName(@Nullable String spaceKey) {
        return Optional.ofNullable(spaceKey).map(this.spaceManager::getSpace).map(Space::getName).filter(StringUtils::isNotBlank).orElse(spaceKey);
    }

    @Override
    public @Nullable String fetchUserKey(@Nullable ConfluenceUser user) {
        return Optional.ofNullable(user).map(ConfluenceUser::getKey).map(UserKey::getStringValue).orElse(null);
    }

    @Override
    public @Nullable String fetchUserKey(@Nullable com.atlassian.crowd.model.user.User user) {
        return this.fetchUserKey((String)Optional.ofNullable(user).map(DirectoryEntity::getName).orElse(null));
    }

    @Override
    public @Nullable String fetchUserFullName(@Nullable ConfluenceUser user) {
        return Optional.ofNullable(user).map(User::getFullName).orElse(Optional.ofNullable(user).map(ConfluenceUser::getLowerName).orElse(null));
    }

    @Override
    public @Nullable String fetchUserFullName(@Nullable com.atlassian.crowd.model.user.User user) {
        return this.fetchUserFullName((String)Optional.ofNullable(user).map(DirectoryEntity::getName).orElse(null));
    }

    @Override
    public @Nullable String fetchUserKey(@Nullable String username) {
        return this.fetchUserKey((ConfluenceUser)Optional.ofNullable(username).map(this.userResolver::getUserByName).orElse(null));
    }

    @Override
    public String fetchUserFullName(@Nullable String username) {
        return this.fetchUserFullName((ConfluenceUser)Optional.ofNullable(username).map(this.userResolver::getUserByName).orElse(null));
    }
}

