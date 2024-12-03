/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentTypeManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.HasLinkWikiMarkup
 *  com.atlassian.confluence.core.persistence.AnyTypeDao
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.avatar.AvatarProviderAccessor
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.rest.entities.builders.AttachmentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.CommentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.ContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.CustomContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.DefaultContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.EntityBuilderFactory;
import com.atlassian.confluence.plugins.rest.entities.builders.PageContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.PersonalInformationContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.SearchEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.SpaceEntityBuilder;
import com.atlassian.confluence.plugins.rest.entities.builders.WikiLinkableContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.DefaultRestAttachmentManager;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.avatar.AvatarProviderAccessor;

public class DefaultEntityBuilderFactory
implements EntityBuilderFactory {
    private final UserEntityHelper userEntityHelper;
    private final SettingsManager settingsManager;
    private final DateEntityFactory dateEntityFactory;
    private final DefaultRestAttachmentManager restAttachmentManager;
    private final AnyTypeDao anyTypeDao;
    private final UserAccessor userAccessor;
    private final ContentTypeManager contentTypeManager;
    private final AvatarProviderAccessor avatarProviderAccessor;
    private final AttachmentManager attachmentManager;

    public DefaultEntityBuilderFactory(SettingsManager settingsManager, DateEntityFactory dateEntityFactory, DefaultRestAttachmentManager restAttachmentManager, AnyTypeDao anyTypeDao, UserAccessor userAccessor, UserEntityHelper userEntityHelper, ContentTypeManager contentTypeManager, AvatarProviderAccessor avatarProviderAccessor, AttachmentManager attachmentManager) {
        this.settingsManager = settingsManager;
        this.dateEntityFactory = dateEntityFactory;
        this.restAttachmentManager = restAttachmentManager;
        this.anyTypeDao = anyTypeDao;
        this.userAccessor = userAccessor;
        this.userEntityHelper = userEntityHelper;
        this.contentTypeManager = contentTypeManager;
        this.avatarProviderAccessor = avatarProviderAccessor;
        this.attachmentManager = attachmentManager;
    }

    @Override
    public SearchEntityBuilder createBuilder(String type) {
        ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation((String)type);
        if (contentType == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        switch (contentType) {
            case PERSONAL_SPACE_DESCRIPTION: 
            case SPACE_DESCRIPTION: {
                return new SpaceEntityBuilder(this.dateEntityFactory, this.settingsManager, this.anyTypeDao);
            }
            case ATTACHMENT: {
                return new AttachmentEntityBuilder(this.anyTypeDao, this.restAttachmentManager);
            }
            case BLOG: 
            case PAGE: {
                return new PageContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
            }
            case COMMENT: {
                return new CommentEntityBuilder((GlobalSettingsManager)this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
            }
            case PERSONAL_INFORMATION: {
                return new PersonalInformationContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userAccessor, this.userEntityHelper, this.avatarProviderAccessor, this.attachmentManager);
            }
            case CUSTOM: {
                return new CustomContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userEntityHelper, this.contentTypeManager);
            }
        }
        return new DefaultContentEntityBuilder((GlobalSettingsManager)this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
    }

    @Override
    public <T extends ContentEntityObject> ContentEntityBuilder<? super T> createContentEntityBuilder(Class<? extends T> clazz) {
        if (PersonalInformation.class.isAssignableFrom(clazz)) {
            return new PersonalInformationContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userAccessor, this.userEntityHelper, this.avatarProviderAccessor, this.attachmentManager);
        }
        if (Page.class.isAssignableFrom(clazz)) {
            return new PageContentEntityBuilder(this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
        }
        if (Comment.class.isAssignableFrom(clazz)) {
            return new CommentEntityBuilder((GlobalSettingsManager)this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
        }
        if (HasLinkWikiMarkup.class.isAssignableFrom(clazz)) {
            return new WikiLinkableContentEntityBuilder((GlobalSettingsManager)this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
        }
        return new DefaultContentEntityBuilder((GlobalSettingsManager)this.settingsManager, this.dateEntityFactory, this.userEntityHelper);
    }
}

