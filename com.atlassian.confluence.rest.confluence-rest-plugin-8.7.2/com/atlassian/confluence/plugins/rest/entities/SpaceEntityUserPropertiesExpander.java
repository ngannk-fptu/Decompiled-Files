/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.entities;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserPermissions;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityUserProperties;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

public class SpaceEntityUserPropertiesExpander
extends AbstractRecursiveEntityExpander<SpaceEntityUserProperties> {
    private final FavouriteManager favouriteManager;
    private final SpaceManager spaceManager;
    private final GlobalSettingsManager settingsManager;
    private static final String ANONYMOUS_USERNAME = "null";

    public SpaceEntityUserPropertiesExpander(FavouriteManager favouriteManager, SpaceManager spaceManager, GlobalSettingsManager settingsManager) {
        this.favouriteManager = favouriteManager;
        this.spaceManager = spaceManager;
        this.settingsManager = settingsManager;
    }

    protected SpaceEntityUserProperties expandInternal(SpaceEntityUserProperties entity) {
        User user = RequestContextThreadLocal.get().getUser();
        Space space = Objects.requireNonNull(this.spaceManager.getSpace(entity.getSpaceKey()));
        entity.setFavourite(this.favouriteManager.isUserFavourite(user, space));
        String username = user != null ? user.getName() : ANONYMOUS_USERNAME;
        entity.setEffectiveUser(username);
        SpaceEntityUserPermissions availablePermissions = new SpaceEntityUserPermissions(space.getKey(), username);
        entity.setPermissions(availablePermissions);
        SpaceDescription spaceDescription = space.getDescription();
        LabelEntityList labels = this.convertToLabelEntityList(spaceDescription.getVisibleLabels(user));
        entity.setLabels(labels);
        try {
            SpaceLogo logo = this.spaceManager.getLogoForSpace(space.getKey());
            entity.setLogo(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + logo.getDownloadPath()), (String)"logo", (String)Thumbnail.MimeType.PNG.toString()));
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        return entity;
    }

    private LabelEntityList convertToLabelEntityList(List<Label> labels) {
        LabelEntityList list = new LabelEntityList();
        LabelEntityBuilder builder = new LabelEntityBuilder();
        for (Label label : labels) {
            list.addLabel(builder.build(label));
        }
        return list;
    }
}

