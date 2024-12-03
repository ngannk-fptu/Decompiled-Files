/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.FormattedBody
 *  com.atlassian.confluence.api.model.content.FormattedBody$FormattedBodyBuilder
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.reference.BuilderUtils
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.user.User
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.factory.Fauxpansions;
import com.atlassian.confluence.api.impl.service.content.factory.ModelFactory;
import com.atlassian.confluence.api.impl.service.content.factory.SpaceMetadataFactory;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.FormattedBody;
import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.model.content.webresource.WebResourceDependencies;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.reference.BuilderUtils;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.retention.SpaceRetentionPolicy;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.retention.SpaceRetentionPolicyService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.user.User;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class SpaceFactory
extends ModelFactory<Space, com.atlassian.confluence.api.model.content.Space> {
    public static final ContentRepresentation DEFAULT_DESCRIPTION_REPRESENTATION = ContentRepresentation.PLAIN;
    private final SpaceLogoManager spaceLogoManager;
    private final SpaceManager spaceManager;
    private final Supplier<ContentFactory> contentFactorySupplier;
    private final SpaceMetadataFactory spaceMetadataFactory;
    private final PermissionManager permissionManager;
    private final SpaceRetentionPolicyService spaceRetentionPolicyService;
    private static final int DEFAULT_ICON_HEIGHT = 48;
    private static final int DEFAULT_ICON_WIDTH = 48;
    private static final Collection<ContentRepresentation> DESCRIPTION_REPRESENTATIONS = ImmutableSet.of((Object)ContentRepresentation.PLAIN, (Object)ContentRepresentation.VIEW);

    public SpaceFactory(SpaceLogoManager spaceLogoManager, SpaceManager spaceManager, Supplier<ContentFactory> contentFactorySupplier, SpaceMetadataFactory spaceMetadataFactory, PermissionManager permissionManager, SpaceRetentionPolicyService spaceRetentionPolicyService) {
        this.spaceLogoManager = spaceLogoManager;
        this.spaceManager = spaceManager;
        this.contentFactorySupplier = contentFactorySupplier;
        this.spaceMetadataFactory = spaceMetadataFactory;
        this.permissionManager = permissionManager;
        this.spaceRetentionPolicyService = spaceRetentionPolicyService;
    }

    @Override
    public com.atlassian.confluence.api.model.content.Space buildFrom(String spaceKey, Expansions expansions) {
        Space space = this.spaceManager.getSpace(spaceKey);
        return this.buildFrom(space, expansions);
    }

    @Override
    public com.atlassian.confluence.api.model.content.Space buildFrom(Space space, Expansions expansions) {
        Objects.requireNonNull(space);
        Reference<Icon> iconIfExpanded = this.getIconIfExpanded(space, expansions);
        Map<ContentRepresentation, FormattedBody> description = this.buildDescription(space, expansions);
        Reference<Content> homepageRef = this.getHomepageRef(space.getHomePage(), expansions);
        Map<String, Object> metadata = this.spaceMetadataFactory.makeMetadata(space, Fauxpansions.fauxpansions(expansions, "metadata"));
        Reference<SpaceRetentionPolicy> retentionPolicyIfExpanded = this.getRetentionPolicyIfExpanded(space, expansions);
        com.atlassian.confluence.spaces.SpaceType dbSpaceType = space.getSpaceType();
        return com.atlassian.confluence.api.model.content.Space.builder().id(space.getId()).key(space.getKey()).name(space.getName()).type(dbSpaceType == null ? null : SpaceType.forName((String)dbSpaceType.toString())).icon(iconIfExpanded).description(description).homepage(homepageRef).metadata(metadata).addLink(LinkType.WEB_UI, space.getUrlPath()).retentionPolicy(retentionPolicyIfExpanded).build();
    }

    private Reference<Content> getHomepageRef(Page homepage, Expansions expansions) {
        if (homepage == null || !this.canView(homepage)) {
            return Reference.empty(Content.class);
        }
        if (!expansions.canExpand("homepage")) {
            return Content.buildReference((ContentSelector)homepage.getSelector());
        }
        ContentFactory contentFactory = (ContentFactory)this.contentFactorySupplier.get();
        Content homepageContent = contentFactory.buildFrom(homepage, expansions.getSubExpansions("homepage"));
        return Reference.to((Object)homepageContent);
    }

    private Reference<Icon> getIconIfExpanded(Space space, Expansions expansions) {
        if (!expansions.canExpand("icon")) {
            return Reference.collapsed(Icon.class);
        }
        String path = this.spaceLogoManager.getLogoDownloadPath(space, AuthenticatedUserThreadLocal.get());
        Icon icon = new Icon(path, 48, 48, false);
        return Reference.to((Object)icon);
    }

    private Reference<SpaceRetentionPolicy> getRetentionPolicyIfExpanded(Space space, Expansions expansions) {
        Optional<SpaceRetentionPolicy> spaceRetentionPolicy;
        if (expansions.canExpand("retentionPolicy") && (spaceRetentionPolicy = this.spaceRetentionPolicyService.getPolicy(space.getKey())).isPresent()) {
            return Reference.to((Object)spaceRetentionPolicy.get());
        }
        return Reference.collapsed(SpaceRetentionPolicy.class);
    }

    private Map<ContentRepresentation, FormattedBody> buildDescription(Space space, Expansions expansions) {
        if (!expansions.canExpand("description")) {
            return BuilderUtils.collapsedMap();
        }
        String description = space.getDescription().getBodyAsString();
        Expansions subExpansions = expansions.getSubExpansions("description");
        ModelMapBuilder mapBuilder = ModelMapBuilder.newInstance();
        for (ContentRepresentation representation : DESCRIPTION_REPRESENTATIONS) {
            if (subExpansions.canExpand(representation.getRepresentation())) {
                mapBuilder.put((Object)representation, (Object)this.buildFormattedBody(representation, description));
                continue;
            }
            mapBuilder.addCollapsedEntry((Object)representation);
        }
        return mapBuilder.build();
    }

    private FormattedBody buildFormattedBody(ContentRepresentation representation, String value) {
        String formattedValue;
        if (representation == DEFAULT_DESCRIPTION_REPRESENTATION) {
            formattedValue = value;
        } else if (representation == ContentRepresentation.VIEW) {
            formattedValue = PlainTextToHtmlConverter.toHtml(value);
        } else {
            throw new BadRequestException("Only accepted representations for Space Description are: " + StringUtils.join(DESCRIPTION_REPRESENTATIONS, (String)","));
        }
        return ((FormattedBody.FormattedBodyBuilder)((FormattedBody.FormattedBodyBuilder)((FormattedBody.FormattedBodyBuilder)FormattedBody.builder().representation(representation)).value(formattedValue)).webresource(Reference.empty(WebResourceDependencies.class))).build();
    }

    private boolean canView(ContentEntityObject entity) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, entity);
    }
}

