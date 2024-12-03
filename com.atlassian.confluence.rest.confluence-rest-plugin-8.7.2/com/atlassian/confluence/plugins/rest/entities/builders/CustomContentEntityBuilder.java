/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentTypeManager
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.content.ContentTypeManager;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.SearchResultEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.DefaultContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class CustomContentEntityBuilder
extends DefaultContentEntityBuilder {
    private final ContentTypeManager contentTypeManager;

    public CustomContentEntityBuilder(SettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserEntityHelper userEntityHelper, ContentTypeManager contentTypeManager) {
        super((GlobalSettingsManager)settingsManager, dateEntityFactory, userEntityHelper);
        this.contentTypeManager = contentTypeManager;
    }

    @Override
    public SearchResultEntity build(SearchResult result) {
        String contentPluginKey;
        ContentUiSupport contentUiSupport;
        SearchResultEntity searchResultEntity = super.build(result);
        if (!(searchResultEntity instanceof ContentEntity)) {
            return searchResultEntity;
        }
        ContentEntity contentEntity = (ContentEntity)searchResultEntity;
        if ("custom".equals(contentEntity.getType()) && (contentUiSupport = this.contentTypeManager.getContentType(contentPluginKey = result.getField(SearchFieldNames.CONTENT_PLUGIN_KEY)).getContentUiSupport()) != null) {
            contentEntity.setIconClass(contentUiSupport.getIconCssClass(result));
        }
        return contentEntity;
    }
}

