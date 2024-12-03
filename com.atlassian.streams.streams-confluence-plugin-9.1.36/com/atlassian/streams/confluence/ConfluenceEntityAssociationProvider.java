/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.streams.api.common.Option
 *  com.atlassian.streams.api.common.uri.Uris
 *  com.atlassian.streams.spi.EntityIdentifier
 *  com.atlassian.streams.spi.StreamsEntityAssociationProvider
 *  com.atlassian.user.User
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.streams.confluence;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.api.common.uri.Uris;
import com.atlassian.streams.confluence.ConfluenceActivityObjectTypes;
import com.atlassian.streams.spi.EntityIdentifier;
import com.atlassian.streams.spi.StreamsEntityAssociationProvider;
import com.atlassian.user.User;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfluenceEntityAssociationProvider
implements StreamsEntityAssociationProvider {
    private static final String URL_PREFIX = "/display/";
    private static final Pattern SPACE_PATTERN = Pattern.compile("([A-Za-z0-9]+)([/#?].*)?");
    private final ApplicationProperties applicationProperties;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PermissionManager permissionManager;

    public ConfluenceEntityAssociationProvider(ApplicationProperties applicationProperties, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager, PermissionManager permissionManager) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
        this.spaceManager = (SpaceManager)Preconditions.checkNotNull((Object)spaceManager, (Object)"spaceManager");
        this.spacePermissionManager = (SpacePermissionManager)Preconditions.checkNotNull((Object)spacePermissionManager, (Object)"spacePermissionManager");
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager, (Object)"permissionManager");
    }

    public Iterable<EntityIdentifier> getEntityIdentifiers(URI target) {
        String targetStr = target.toString();
        if (target.isAbsolute()) {
            if (!targetStr.startsWith(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + URL_PREFIX)) {
                return ImmutableList.of();
            }
            return this.matchEntities(targetStr.substring(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL).length() + URL_PREFIX.length()));
        }
        return this.matchEntities(targetStr);
    }

    public Option<URI> getEntityURI(EntityIdentifier identifier) {
        if (identifier.getType().equals(ConfluenceActivityObjectTypes.space().iri())) {
            return Option.some((Object)URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + URL_PREFIX + Uris.encode((String)identifier.getValue())));
        }
        return Option.none();
    }

    public Option<String> getFilterKey(EntityIdentifier identifier) {
        if (identifier.getType().equals(ConfluenceActivityObjectTypes.space().iri())) {
            return Option.some((Object)"key");
        }
        return Option.none();
    }

    public Option<Boolean> getCurrentUserViewPermission(EntityIdentifier identifier) {
        return this.getCurrentUserPermission(identifier, "VIEWSPACE");
    }

    public Option<Boolean> getCurrentUserEditPermission(EntityIdentifier identifier) {
        return this.getCurrentUserPermission(identifier, "COMMENT");
    }

    public Optional<Boolean> getCurrentUserViewPermissionForTargetlessEntity() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return Optional.of(this.permissionManager.hasPermission((User)user, Permission.VIEW, PermissionManager.TARGET_APPLICATION));
    }

    private Option<Boolean> getCurrentUserPermission(EntityIdentifier identifier, String permission) {
        Space space;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (identifier.getType().equals(ConfluenceActivityObjectTypes.space().iri()) && (space = this.spaceManager.getSpace(identifier.getValue())) != null) {
            return Option.some((Object)this.spacePermissionManager.hasPermission(permission, space, (User)user));
        }
        return Option.none();
    }

    private Iterable<EntityIdentifier> matchEntities(String input) {
        String spaceKey;
        Space space;
        Matcher spaceMatcher = SPACE_PATTERN.matcher(input);
        if (spaceMatcher.matches() && (space = this.spaceManager.getSpace(spaceKey = spaceMatcher.group(1))) != null) {
            URI canonicalUri = URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL) + URL_PREFIX + Uris.encode((String)spaceKey));
            return ImmutableList.of((Object)new EntityIdentifier(ConfluenceActivityObjectTypes.space().iri(), spaceKey, canonicalUri));
        }
        return ImmutableList.of();
    }
}

