/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserImpl
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.avatar.AvatarProviderAccessor
 *  com.atlassian.confluence.user.avatar.ConfluenceAvatarOwner
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  com.atlassian.plugins.avatar.AbstractAvatar
 *  com.atlassian.plugins.avatar.Avatar
 *  com.atlassian.plugins.avatar.AvatarOwner
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.WikiLinkableContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.avatar.AvatarProviderAccessor;
import com.atlassian.confluence.user.avatar.ConfluenceAvatarOwner;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.core.util.thumbnail.Thumbnail;
import com.atlassian.plugins.avatar.AbstractAvatar;
import com.atlassian.plugins.avatar.Avatar;
import com.atlassian.plugins.avatar.AvatarOwner;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalInformationContentEntityBuilder
extends WikiLinkableContentEntityBuilder<PersonalInformation> {
    private static final String PROFILE_PICTURE_BUILTIN_PATH = "/images/icons/profilepics/";
    private static final Logger log = LoggerFactory.getLogger(PersonalInformationContentEntityBuilder.class);
    static final String USER = "user";
    private final UserAccessor userAccessor;
    private final AvatarProviderAccessor avatarProviderAccessor;
    private final AttachmentManager attachmentManager;

    public PersonalInformationContentEntityBuilder(SettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserAccessor userAccessor, UserEntityHelper userEntityHelper, AvatarProviderAccessor avatarProviderAccessor, AttachmentManager attachmentManager) {
        super((GlobalSettingsManager)settingsManager, dateEntityFactory, userEntityHelper);
        this.userAccessor = userAccessor;
        this.avatarProviderAccessor = avatarProviderAccessor;
        this.attachmentManager = attachmentManager;
    }

    @Override
    public ContentEntity build(PersonalInformation personalInfo) {
        ContentEntity contentEntity = super.build(personalInfo);
        String username = personalInfo.getUsername();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            return null;
        }
        contentEntity.setTitle(user.getFullName());
        String profilePictureUrl = this.userAccessor.getUserProfilePicture((User)user).getUriReference();
        contentEntity.setThumbnailLink(this.getThumbnailLink(profilePictureUrl));
        contentEntity.setUsername(username);
        contentEntity.setUserKey(user.getKey());
        contentEntity.setLastModifiedDate(null);
        return contentEntity;
    }

    private Link getThumbnailLink(String imageUrl) {
        try {
            return Link.link((URI)new URI(imageUrl), (String)"thumbnail", (String)Thumbnail.MimeType.PNG.toString());
        }
        catch (URISyntaxException uRISyntaxException) {
            return null;
        }
    }

    @Override
    public SearchResultEntity build(SearchResult result) {
        ContentEntity entity = (ContentEntity)super.build(result);
        String username = result.getPreviewKey();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user == null) {
            return null;
        }
        this.setUserProperties(entity, user);
        return entity;
    }

    private void setWikiLink(String username, ContentEntity entity) {
        if (username != null) {
            entity.setWikiLink("[~" + username + "]");
        } else {
            entity.setWikiLink(null);
        }
    }

    @Override
    public SearchResultEntity build(com.atlassian.confluence.search.v2.SearchResult result) {
        if ("true".equalsIgnoreCase(result.getField(SearchFieldNames.IS_DEACTIVATED_USER)) || "true".equalsIgnoreCase(result.getField(SearchFieldNames.IS_EXTERNALLY_DELETED_USER)) || "true".equalsIgnoreCase(result.getField(SearchFieldNames.IS_SHADOWED_USER))) {
            return null;
        }
        ContentEntity entity = (ContentEntity)super.build(result);
        String username = result.getField("username");
        entity.setType(USER);
        entity.setUsername(username);
        entity.setUserKey(new UserKey(result.getField(SearchFieldNames.USER_KEY)));
        entity.setTitle(result.getField("fullName"));
        this.setWikiLink(username, entity);
        entity.setThumbnailLink(this.getThumbnailLink(this.getAvatar(result).getUrl()));
        entity.setLastModifiedDate(null);
        return entity;
    }

    private Avatar getAvatar(com.atlassian.confluence.search.v2.SearchResult sr) {
        return this.avatarProviderAccessor.getAvatarProvider().getAvatar((AvatarOwner)new ConfluenceAvatarOwner((User)new ConfluenceUserImpl(sr.getField("username"), sr.getField("fullName"), sr.getField("email"))), avatarOwner -> new AvatarImpl(avatarOwner.getIdentifier(), sr.getField(SearchFieldNames.PROFILE_PICTURE_URL)), 48);
    }

    private void setUserProperties(ContentEntity entity, ConfluenceUser user) {
        String name = user.getName();
        entity.setTitle(user.getFullName());
        entity.setThumbnailLink(this.getThumbnailLink(this.userAccessor.getUserProfilePicture((User)user).getUriReference()));
        entity.setType(USER);
        entity.setUsername(name);
        entity.setUserKey(user.getKey());
        this.setWikiLink(name, entity);
        entity.setLastModifiedDate(null);
    }

    private class AvatarImpl
    extends AbstractAvatar {
        private final String url;

        AvatarImpl(String ownerId, String url) {
            super(ownerId, "image/png", 48);
            this.url = url;
        }

        public String getUrl() {
            return this.url;
        }

        public boolean isExternal() {
            return false;
        }

        public InputStream getBytes() throws IOException {
            String url = this.getUrl();
            if (url.startsWith(PersonalInformationContentEntityBuilder.PROFILE_PICTURE_BUILTIN_PATH)) {
                return ServletContextThreadLocal.getContext().getResourceAsStream(url);
            }
            Optional attachment = PersonalInformationContentEntityBuilder.this.attachmentManager.findAttachmentForDownloadPath(url);
            if (!attachment.isPresent()) {
                log.warn("Unable to get bytes of profile picture [{}] for the user [{}]", (Object)url, (Object)this.getOwnerId());
                throw new IOException("Unable to get bytes for " + url);
            }
            return PersonalInformationContentEntityBuilder.this.attachmentManager.getAttachmentData((Attachment)attachment.get());
        }

        public Avatar atSize(int size) {
            return this;
        }
    }
}

