/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.actions;

import com.atlassian.confluence.content.ui.AttachmentUiSupport;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.UserProfilePictureAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.util.actions.DisplayMapper;
import com.atlassian.core.filters.ServletContextThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentTypesDisplayMapper
implements DisplayMapper {
    public static final Logger log = LoggerFactory.getLogger(ContentTypesDisplayMapper.class);
    public static final String CSS_CLASS_PREFIX = "content-type-";
    public static final String DEFAULT_CLASS_NAME = "content-type-file";
    public static final String DEFAULT_ATTACHMENT_CLASS_NAME = "content-type-attachment-unknown";
    private final UserProfilePictureAccessor userProfilePictureAccessor;
    private final ConfluenceUserResolver userResolver;
    private final ContentUiSupport<?> contentUiSupport;

    public ContentTypesDisplayMapper(UserProfilePictureAccessor userProfilePictureAccessor, ConfluenceUserResolver userResolver, ContentUiSupport<?> contentUiSupport) {
        this.userProfilePictureAccessor = userProfilePictureAccessor;
        this.userResolver = userResolver;
        this.contentUiSupport = contentUiSupport;
    }

    public String getClassName(SearchResult result) {
        return this.getClassName(result.getContentType(), result.getPreviewKey(), result.getContentPluginKey(), result.getName());
    }

    @Override
    public String getClassName(com.atlassian.confluence.search.v2.SearchResult result) {
        return this.getClassName(result.getType(), result.getExtraFields().get("attachmentMimeType"), result.getExtraFields().get(SearchFieldNames.CONTENT_PLUGIN_KEY), result.getDisplayTitle());
    }

    private String getClassName(String contentType, String previewKey, String contentPluginKey, String filename) {
        String className = DEFAULT_CLASS_NAME;
        if ("attachment".equals(contentType)) {
            className = ContentTypesDisplayMapper.getIconForAttachment(previewKey, filename);
        } else if (StringUtils.isNotBlank((CharSequence)contentType)) {
            className = this.contentUiSupport.getContentCssClass(contentType, contentPluginKey);
        }
        return className;
    }

    public static String getIconForAttachment(String contentType, String fileName) {
        String extension = StringUtils.substringAfterLast((String)fileName, (String)".");
        AttachmentUiSupport.AttachmentInfo attachmentInfo = AttachmentUiSupport.getAttachmentInfo(contentType, extension);
        return "content-type-attachment-" + attachmentInfo.getIdentifier();
    }

    @Deprecated
    public String getIconUrlForUsername(String username) {
        ConfluenceUser user = this.userResolver.getUserByName(username);
        if (user == null) {
            return "/images/icons/profilepics/default.svg";
        }
        ProfilePictureInfo info = this.userProfilePictureAccessor.getUserProfilePicture(user);
        if (info.isExternal()) {
            log.warn("getDownloadPath method is deprecated and is not supposed to be used with external avatars. Please use getLogoUriReference instead. Falling back to default avatar. Real avatar url is [{}]", (Object)info.getUriReference());
            return "/images/icons/profilepics/default.svg";
        }
        return info.getDownloadPath();
    }

    public String getIconUriReferenceForUsername(String username) {
        ConfluenceUser user = this.userResolver.getUserByName(username);
        if (user == null) {
            return ServletContextThreadLocal.getRequest().getContextPath() + "/images/icons/profilepics/default.svg";
        }
        return this.userProfilePictureAccessor.getUserProfilePicture(user).getUriReference();
    }
}

