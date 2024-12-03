/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentType
 *  com.atlassian.confluence.content.ContentTypeModuleDescriptor
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.persistence.AnyTypeDao
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.PersonalInformation
 *  com.atlassian.confluence.util.actions.ContentTypesDisplayMapper
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ContentTypeModuleDescriptor;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AbstractUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.AttachmentUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.CommentUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.ContentUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.ProfilePictureUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.ProfileUpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItem;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItemFactory;
import com.atlassian.confluence.plugins.macros.advanced.recentupdate.UpdateItemUtils;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.util.actions.ContentTypesDisplayMapper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUpdateItemFactory
implements UpdateItemFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultUpdateItemFactory.class);
    private final DateFormatter dateFormatter;
    private final ContentTypesDisplayMapper contentTypesDisplayMapper;
    private final I18NBean i18n;
    private PluginAccessor pluginAccessor;

    public DefaultUpdateItemFactory(DateFormatter dateFormatter, I18NBean i18n, ContentTypesDisplayMapper contentTypesDisplayMapper, PluginAccessor pluginAccessor) {
        if (i18n == null) {
            throw new IllegalArgumentException("i18n is required.");
        }
        this.contentTypesDisplayMapper = contentTypesDisplayMapper;
        this.dateFormatter = dateFormatter;
        this.i18n = i18n;
        this.pluginAccessor = pluginAccessor;
    }

    private String getIconClassForSearchResult(SearchResult searchResult) {
        return this.contentTypesDisplayMapper.getClassName(searchResult);
    }

    @Override
    public UpdateItem get(SearchResult searchResult) {
        AbstractUpdateItem result = null;
        String type = searchResult.getType();
        AnyTypeDao anyTypeDao = (AnyTypeDao)ContainerManager.getComponent((String)"anyTypeDao");
        if ("comment".equals(type)) {
            result = new CommentUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult));
        } else if ("attachment".equals(type)) {
            Attachment attachment = (Attachment)anyTypeDao.findByHandle(searchResult.getHandle());
            if (attachment != null) {
                ContentEntityObject content = attachment.getContainer();
                result = content instanceof PersonalInformation ? new ProfilePictureUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult)) : new AttachmentUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult));
            }
        } else if ("userinfo".equals(type)) {
            int version = UpdateItemUtils.getContentVersion(searchResult);
            if (version > 1) {
                result = new ProfileUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult));
            }
        } else if ("custom".equals(type)) {
            CustomContentEntityObject ccoeObject = (CustomContentEntityObject)anyTypeDao.findByHandle(searchResult.getHandle());
            String pluginModuleKey = ccoeObject.getPluginModuleKey();
            PermissionDelegate permissionDelegate = this.getContentTypeModuleDescriptorByPluginKey(pluginModuleKey).get();
            if (permissionDelegate != null && permissionDelegate.canView((User)AuthenticatedUserThreadLocal.get(), (Object)ccoeObject)) {
                result = new ContentUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult));
            }
        } else {
            result = new ContentUpdateItem(searchResult, this.dateFormatter, this.i18n, this.getIconClassForSearchResult(searchResult));
        }
        return result;
    }

    private Optional<PermissionDelegate> getContentTypeModuleDescriptorByPluginKey(String pluginModuleKey) {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(ContentTypeModuleDescriptor.class).stream().filter(moduleDescriptor -> moduleDescriptor.getContentType().equals(pluginModuleKey)).flatMap(moduleDescriptor -> {
            try {
                ContentType contentType = moduleDescriptor.getModule();
                return Stream.of(contentType.getPermissionDelegate());
            }
            catch (Exception e) {
                log.debug("Error creating module: " + pluginModuleKey, (Throwable)e);
                return Stream.empty();
            }
        }).findFirst();
    }
}

