/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.util.FilesystemUtils;
import com.atlassian.confluence.util.HtmlUtil;

public class ExportUtils {
    public static final String FILENAME_EXTENSION = ".html";
    public static final String PROP_BUILD_NUMBER = "buildNumber";
    public static final String PROP_EXPORT_TYPE = "exportType";
    public static final String PROP_BACKUP_ATTACHMENTS = "backupAttachments";
    public static final String PROP_SUPPORT_ENTITLEMENT_NUMBER = "supportEntitlementNumber";
    public static final String PROP_EXPORTED_SPACEKEY = "spaceKey";
    public static final String PROP_EXPORTED_SPACEKEYS = "spaceKeys";
    public static final String PROP_CREATED_BY_BUILD_NUMBER = "createdByBuildNumber";
    public static final String PROP_DEFAULT_USERS_GROUP = "defaultUsersGroup";
    public static final String PROP_PLUGIN_CREATED_BY_VERSION = "ao.data.version.";
    public static final String PROP_PLUGIN_EARLIEST_VERSION = "ao.data.version.min.";
    public static final String PROP_PLUGINS_EXPORTING_DATA = "ao.data.list";

    public static String getTitleAsFilename(ContentEntityObject ceo) {
        return ExportUtils.getTitleAsFilename(ceo.getDisplayTitle(), ceo.getIdAsString());
    }

    public static String getTitleAsFilename(SearchResult searchResult) {
        if (searchResult.getHandle() instanceof HibernateHandle) {
            return ExportUtils.getTitleAsFilename(searchResult.getDisplayTitle(), Long.toString(((HibernateHandle)searchResult.getHandle()).getId()));
        }
        return ExportUtils.getTitleAsFilename(searchResult.getDisplayTitle(), searchResult.getHandle().toString());
    }

    private static String getTitleAsFilename(String title, String id) {
        String encodedTitle = HtmlUtil.urlEncode(title).replace('+', '-') + "_" + id;
        if (FilesystemUtils.isSafeTitleForFilesystem(encodedTitle)) {
            return encodedTitle + FILENAME_EXTENSION;
        }
        return id + FILENAME_EXTENSION;
    }

    public static String getTitleAsFilename(String title) {
        String encoded = HtmlUtil.urlEncode(title) + FILENAME_EXTENSION;
        return encoded.replace("*", "%2A");
    }

    public static String getTitleAsHref(ContentEntityObject ceo) {
        return HtmlUtil.urlEncode(ExportUtils.getTitleAsFilename(ceo));
    }

    public static String getTitleAsHref(SearchResult searchResult) {
        return HtmlUtil.urlEncode(ExportUtils.getTitleAsFilename(searchResult));
    }
}

