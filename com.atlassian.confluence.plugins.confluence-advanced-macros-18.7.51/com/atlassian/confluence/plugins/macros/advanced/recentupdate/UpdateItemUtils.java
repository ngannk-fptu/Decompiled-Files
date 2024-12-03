/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateItemUtils {
    private static final Logger log = LoggerFactory.getLogger(UpdateItemUtils.class);

    public static int getContentVersion(SearchResult searchResult) {
        String contentVersion = (String)searchResult.getExtraFields().get(SearchFieldNames.CONTENT_VERSION);
        int version = -1;
        if (StringUtils.isNotBlank((CharSequence)contentVersion)) {
            try {
                version = Integer.parseInt(contentVersion);
            }
            catch (NumberFormatException e) {
                log.debug("Invalid content-version: " + contentVersion);
            }
        }
        return version;
    }
}

