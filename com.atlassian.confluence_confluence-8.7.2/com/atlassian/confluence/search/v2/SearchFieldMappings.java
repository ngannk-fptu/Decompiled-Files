/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.plugins.index.api.ExactAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.ExactFilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.FilenameAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.UnstemmedAnalyzerDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.BooleanFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.DateFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.NestedStringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.TextFieldMapping;

public final class SearchFieldMappings {
    public static final StringFieldMapping ATTACHMENT_DOWNLOAD_PATH = StringFieldMapping.builder("downloadPath").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_FILE_EXTENSION = StringFieldMapping.builder("file.extension").build();
    public static final TextFieldMapping ATTACHMENT_FILE_NAME = TextFieldMapping.builder("filename").analyzer(new FilenameAnalyzerDescriptor()).store(true).build();
    public static final StringFieldMapping ATTACHMENT_FILE_NAME_UNTOKENIZED = StringFieldMapping.builder("filenameuntokenized").store(true).build();
    public static final LongFieldMapping ATTACHMENT_FILE_SIZE = LongFieldMapping.builder("filesize").store(true).build();
    public static final StringFieldMapping ATTACHMENT_MIME_TYPE = StringFieldMapping.builder("attachment-mime-type").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_NICE_FILE_SIZE = StringFieldMapping.builder("niceFileSize").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_NICE_TYPE = StringFieldMapping.builder("niceType").store(true).build();
    public static final StringFieldMapping ATTACHMENT_OWNER_CONTENT_TYPE = StringFieldMapping.builder("attachment-owner-content-type").build();
    public static final StringFieldMapping ATTACHMENT_OWNER_ID = StringFieldMapping.builder("content.id").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_OWNER_REAL_TITLE = StringFieldMapping.builder("content.realTitle").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_OWNER_TYPE = StringFieldMapping.builder("content.type").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_OWNER_URL_PATH = StringFieldMapping.builder("content.urlPath").store(true).index(false).build();
    public static final StringFieldMapping ATTACHMENT_OWNER_USERNAME = StringFieldMapping.builder("content.username").store(true).index(false).build();
    public static final TextFieldMapping ATTACHMENT_VERSION_COMMENT = TextFieldMapping.builder("comment").store(true).build();
    public static final StringFieldMapping CLASS_NAME = StringFieldMapping.builder("classname").build();
    public static final StringFieldMapping CONTAINER_CONTENT_TYPE = StringFieldMapping.builder("container.content.type").store(true).build();
    public static final TextFieldMapping CONTENT = TextFieldMapping.builder("contentBody").build();
    public static final TextFieldMapping CONTENT_STORED = TextFieldMapping.builder("contentBody-stored").store(true).index(false).build();
    public static final TextFieldMapping CONTENT_NAME_UNSTEMMED = TextFieldMapping.builder("content-name-unstemmed").store(true).analyzer(new UnstemmedAnalyzerDescriptor()).build();
    public static final TextFieldMapping EXACT_CONTENT_BODY = TextFieldMapping.builder("exact-contentBody").analyzer(new ExactAnalyzerDescriptor()).searchAnalyzer(new ExactAnalyzerDescriptor()).build();
    public static final StringFieldMapping CONTENT_NAME_UNTOKENIZED = StringFieldMapping.builder("content-name-untokenized").build();
    public static final NestedStringFieldMapping CONTENT_PERMISSION_SETS = NestedStringFieldMapping.builder("permissionSets").build();
    public static final StringFieldMapping CONTENT_PLUGIN_KEY = StringFieldMapping.builder("contentPluginKey").store(true).build();
    public static final StringFieldMapping CONTENT_STATUS = StringFieldMapping.builder("contentStatus").store(true).build();
    public static final StringFieldMapping EXCERPT = StringFieldMapping.builder("excerpt").store(true).build();
    public static final StringFieldMapping CONTENT_VERSION = StringFieldMapping.builder("content-version").store(true).index(false).build();
    public static final DateFieldMapping CREATION_DATE = DateFieldMapping.builder("created").store(true).build();
    public static final StringFieldMapping CREATOR = StringFieldMapping.builder("creatorName").store(true).build();
    @Deprecated
    public static final StringFieldMapping DOCUMENT_TYPE = StringFieldMapping.builder("confluence-document-type").store(true).build();
    public static final StringFieldMapping HANDLE = StringFieldMapping.builder("handle").store(true).build();
    public static final LongFieldMapping CONTENT_ID = LongFieldMapping.builder("content-id").store(true).build();
    public static final BooleanFieldMapping HOME_PAGE = BooleanFieldMapping.builder("homePage").store(true).index(false).build();
    public static final StringFieldMapping INHERITED_LABEL = StringFieldMapping.builder("inheritedLabel").store(true).build();
    public static final TextFieldMapping INHERITED_LABEL_TEXT = TextFieldMapping.builder("inheritedLabelText").store(true).build();
    public static final BooleanFieldMapping IN_SPACE = BooleanFieldMapping.builder("inSpace").build();
    public static final BooleanFieldMapping IS_DEACTIVATED_USER = BooleanFieldMapping.builder("isDeactivatedUser").store(true).build();
    public static final BooleanFieldMapping IS_EXTERNALLY_DELETED_USER = BooleanFieldMapping.builder("isExternallyDeletedUser").store(true).build();
    public static final BooleanFieldMapping IS_LICENSED_USER = BooleanFieldMapping.builder("isLicensedUser").store(true).build();
    public static final BooleanFieldMapping IS_SHADOWED_USER = BooleanFieldMapping.builder("isShadowedUser").store(true).build();
    public static final StringFieldMapping LABEL = StringFieldMapping.builder("label").store(true).build();
    public static final TextFieldMapping LABEL_TEXT = TextFieldMapping.builder("labelText").store(true).build();
    public static final DateFieldMapping LAST_MODIFICATION_DATE = DateFieldMapping.builder("modified").store(true).build();
    public static final StringFieldMapping LAST_MODIFIER = StringFieldMapping.builder("lastModifierName").store(true).build();
    public static final String ANONYMOUS_LAST_MODIFIER_ID = "";
    public static final StringFieldMapping LAST_MODIFIERS = StringFieldMapping.builder("lastModifiers").store(true).build();
    public static final StringFieldMapping LAST_UPDATE_DESCRIPTION = StringFieldMapping.builder("versionComment").store(true).index(false).build();
    public static final StringFieldMapping LATEST_VERSION_ID = StringFieldMapping.builder("latest-version-id").store(true).build();
    public static final StringFieldMapping MENTION = StringFieldMapping.builder("mentions").build();
    public static final StringFieldMapping MACRO_NAME = StringFieldMapping.builder("macroName").store(true).build();
    public static final StringFieldMapping MACRO_STORAGE_VERSION = StringFieldMapping.builder("macroStorageVersion").store(true).build();
    public static final StringFieldMapping PAGE_DISPLAY_TITLE = StringFieldMapping.builder("page.realTitle").store(true).index(false).build();
    public static final StringFieldMapping PAGE_URL_PATH = StringFieldMapping.builder("page.urlPath").store(true).index(false).build();
    public static final TextFieldMapping PARENT_TITLE_UNSTEMMED = TextFieldMapping.builder("parent-title-unstemmed").store(true).analyzer(new UnstemmedAnalyzerDescriptor()).build();
    public static final StringFieldMapping PERSONAL_INFORMATION_HAS_PERSONAL_SPACE = StringFieldMapping.builder("hasPersonalSpace").store(true).build();
    public static final StringFieldMapping PROFILE_PICTURE_URL = StringFieldMapping.builder("profile-picture-url").store(true).build();
    public static final StringFieldMapping RETENTION_POLICY = StringFieldMapping.builder("retentionPolicy").store(true).build();
    public static final StringFieldMapping RETENTION_POLICY_DELETE_TRASH = StringFieldMapping.builder("retention_policy_deleting_trash").store(true).build();
    public static final StringFieldMapping RETENTION_POLICY_DELETE_VERSION = StringFieldMapping.builder("retention_policy_deleting_version").store(true).build();
    public static final StringFieldMapping SPACE_KEY = StringFieldMapping.builder("spacekey").store(true).build();
    public static final StringFieldMapping SPACE_NAME = StringFieldMapping.builder("space-name").store(true).index(false).build();
    public static final StringFieldMapping SPACE_TYPE = StringFieldMapping.builder("space-type").store(true).build();
    public static final TextFieldMapping TITLE = TextFieldMapping.builder("title").store(true).build();
    public static final StringFieldMapping DISPLAY_TITLE = StringFieldMapping.builder("display-title").store(true).index(false).build();
    public static final TextFieldMapping EXACT_TITLE = TextFieldMapping.builder("exact-title").store(true).analyzer(new ExactAnalyzerDescriptor()).searchAnalyzer(new ExactAnalyzerDescriptor()).build();
    public static final TextFieldMapping EXACT_FILENAME = TextFieldMapping.builder("exact-filename").store(true).analyzer(new ExactFilenameAnalyzerDescriptor()).searchAnalyzer(new ExactAnalyzerDescriptor()).build();
    public static final StringFieldMapping TYPE = StringFieldMapping.builder("type").store(true).build();
    public static final StringFieldMapping CONTENT_URL_PATH = StringFieldMapping.builder("urlPath").store(true).build();
    public static final StringFieldMapping CHANGE_URL_PATH = StringFieldMapping.builder("urlPath").store(true).index(false).build();
    public static final StringFieldMapping USER_KEY = StringFieldMapping.builder("userKey").store(true).build();
    public static final TextFieldMapping USER_NAME = TextFieldMapping.builder("username").store(true).build();
    public static final TextFieldMapping FULL_NAME = TextFieldMapping.builder("fullName").store(true).build();
    public static final StringFieldMapping FULL_NAME_UNTOKENIZED = StringFieldMapping.builder("fullNameUntokenized").store(true).build();
    public static final StringFieldMapping FULL_NAME_SORTABLE = StringFieldMapping.builder("fullNameSortable").store(true).build();
    public static final TextFieldMapping EMAIL = TextFieldMapping.builder("email").store(true).build();
    public static final StringFieldMapping VERSION = StringFieldMapping.builder("version").store(true).index(false).build();

    private SearchFieldMappings() {
    }
}

