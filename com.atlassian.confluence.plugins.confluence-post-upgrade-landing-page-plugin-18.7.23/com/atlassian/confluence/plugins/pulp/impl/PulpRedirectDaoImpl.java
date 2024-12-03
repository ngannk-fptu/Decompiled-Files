/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.annotations.VisibleForTesting
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.plugins.pulp.impl;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.plugins.pulp.PulpRedirectDao;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.annotations.VisibleForTesting;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Named
@ParametersAreNonnullByDefault
public class PulpRedirectDaoImpl
implements PulpRedirectDao {
    private final PluginSettings pluginSettings;
    private final UserIdSerializer userIdSerializer;

    @Inject
    public PulpRedirectDaoImpl(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this(pluginSettingsFactory, new UserIdSerializer());
    }

    @VisibleForTesting
    PulpRedirectDaoImpl(PluginSettingsFactory pluginSettingsFactory, UserIdSerializer userIdSerializer) {
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.userIdSerializer = Objects.requireNonNull(userIdSerializer);
    }

    @VisibleForTesting
    static String getApplicationPropertyKey(String confluenceVersionNumber) {
        return "com.atlassian.confluence.pulp.viewers-" + confluenceVersionNumber;
    }

    @Override
    public boolean addRedirect(@Nullable ConfluenceUser user, String confluenceVersionNumber) {
        if (user == null) {
            return false;
        }
        String propertyKey = PulpRedirectDaoImpl.getApplicationPropertyKey(confluenceVersionNumber);
        Collection pulpViewerIds = this.getUserIds(propertyKey).collect(Collectors.toCollection(HashSet::new));
        boolean addedUserId = pulpViewerIds.add(user.getKey().getStringValue());
        if (addedUserId) {
            String newPropertyValue = this.userIdSerializer.formatUserIds(pulpViewerIds);
            this.pluginSettings.put(propertyKey, (Object)newPropertyValue);
        }
        return addedUserId;
    }

    @Override
    public int getRedirectCount(String confluenceVersionNumber) {
        String propertyKey = PulpRedirectDaoImpl.getApplicationPropertyKey(confluenceVersionNumber);
        Stream<String> pulpViewerIds = this.getUserIds(propertyKey);
        return (int)pulpViewerIds.count();
    }

    @Override
    public boolean hasBeenRedirected(ConfluenceUser user, String confluenceVersionNumber) {
        String propertyKey = PulpRedirectDaoImpl.getApplicationPropertyKey(confluenceVersionNumber);
        return this.getUserIds(propertyKey).anyMatch(userId -> user.getKey().getStringValue().equals(userId));
    }

    private Stream<String> getUserIds(String propertyKey) {
        Object propertyValue = this.pluginSettings.get(propertyKey);
        return this.userIdSerializer.parseUserIds(propertyValue == null ? "" : propertyValue.toString());
    }

    static class UserIdSerializer {
        private static final String USER_ID_DELIMITER = ";";

        UserIdSerializer() {
        }

        @NonNull String formatUserIds(Collection<String> userIds) {
            return String.join((CharSequence)USER_ID_DELIMITER, userIds);
        }

        @NonNull Stream<String> parseUserIds(@Nullable String applicationProperty) {
            return Arrays.stream(StringUtils.split((String)StringUtils.trimToEmpty((String)applicationProperty), (String)USER_ID_DELIMITER));
        }
    }
}

