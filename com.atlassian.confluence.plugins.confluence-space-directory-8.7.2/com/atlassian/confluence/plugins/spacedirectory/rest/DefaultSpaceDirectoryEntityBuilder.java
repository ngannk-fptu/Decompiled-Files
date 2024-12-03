/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.datetime.DateFormatterFactory
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.plugins.rest.entities.DateEntity
 *  com.atlassian.confluence.plugins.rest.entities.LabelEntityBuilder
 *  com.atlassian.confluence.plugins.rest.entities.LabelEntityList
 *  com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.userstatus.FavouriteManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.spacedirectory.rest;

import com.atlassian.confluence.core.datetime.DateFormatterFactory;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.plugins.rest.entities.DateEntity;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.LabelEntityList;
import com.atlassian.confluence.plugins.rest.manager.RequestContextThreadLocal;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryEntity;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryEntityBuilder;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.userstatus.FavouriteManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.user.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DefaultSpaceDirectoryEntityBuilder
implements SpaceDirectoryEntityBuilder {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    private static final String ANONYMOUS_USERNAME = "null";
    private final I18NBeanFactory i18nBeanFactory;
    private final GlobalSettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final FavouriteManager favouriteManager;
    private final SpaceLogoManager spaceLogoManager;
    private final DateFormatterFactory dateFormatterFactory;

    public DefaultSpaceDirectoryEntityBuilder(I18NBeanFactory i18NBeanFactory, GlobalSettingsManager settingsManager, FavouriteManager favouriteManager, SpaceManager spaceManager, SpaceLogoManager spaceLogoManager, DateFormatterFactory dateFormatterFactory) {
        this.i18nBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
        this.favouriteManager = favouriteManager;
        this.spaceManager = spaceManager;
        this.spaceLogoManager = spaceLogoManager;
        this.dateFormatterFactory = dateFormatterFactory;
    }

    @Override
    public SpaceDirectoryEntity build(SearchResult searchResult) {
        Space space = this.spaceManager.getSpace(searchResult.getSpaceKey());
        if (space == null) {
            return null;
        }
        User user = RequestContextThreadLocal.get().getUser();
        SpaceDirectoryEntity entity = new SpaceDirectoryEntity();
        entity.setName(space.getName());
        entity.setKey(space.getKey());
        entity.addLink(Link.self((URI)RequestContextThreadLocal.get().getUriBuilder("space").build(new Object[]{space.getKey()})));
        entity.setCreatedDate(this.convertToDateEntity(space.getCreationDate()));
        entity.setLastModifiedDate(this.convertToDateEntity(space.getLastModificationDate()));
        entity.setWikiLink("[" + space.getKey() + ":]");
        entity.setSummaryPath("/spaces/viewspacesummary.action?key=" + space.getKey());
        try {
            entity.addLink(Link.link((URI)new URI(this.settingsManager.getGlobalSettings().getBaseUrl() + space.getDeepLinkUri()), (String)"alternate", (String)"text/html"));
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        entity.setDescription(GeneralUtil.plain2html((String)space.getDescription().getBodyAsString()));
        if (user != null) {
            entity.setEffectiveUser(user.getName());
        } else {
            entity.setEffectiveUser(ANONYMOUS_USERNAME);
        }
        entity.setFavourite(this.favouriteManager.isUserFavourite(user, space));
        SpaceDescription spaceDescription = space.getDescription();
        LabelEntityList labels = this.convertToLabelEntityList(spaceDescription.getVisibleLabels(user));
        entity.setLabels(labels);
        try {
            String logo = this.spaceLogoManager.getLogoUriReference(space, user);
            entity.setLogo(Link.link((URI)new URI(logo), (String)"logo", (String)Thumbnail.MimeType.PNG.toString()));
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

    private DateEntity convertToDateEntity(Date date) {
        if (date == null) {
            return null;
        }
        DateEntity entity = new DateEntity();
        FriendlyDateFormatter friendlyDateFormatter = this.dateFormatterFactory.createFriendlyForUser();
        SimpleDateFormat s = new SimpleDateFormat(DATE_FORMAT);
        Message msg = friendlyDateFormatter.getFormatMessage(date);
        entity.setFriendly(this.i18nBeanFactory.getI18NBean().getText(msg));
        entity.setDate(s.format(date));
        return entity;
    }
}

