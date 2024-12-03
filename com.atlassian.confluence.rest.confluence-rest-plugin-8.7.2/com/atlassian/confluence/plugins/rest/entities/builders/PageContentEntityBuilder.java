/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.WikiLinkableContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class PageContentEntityBuilder
extends WikiLinkableContentEntityBuilder<Page> {
    public PageContentEntityBuilder(SettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserEntityHelper userEntityHelper) {
        super((GlobalSettingsManager)settingsManager, dateEntityFactory, userEntityHelper);
    }

    @Override
    public ContentEntity build(Page page) {
        ContentEntity entity = super.build(page);
        if (page.getParent() != null) {
            entity.setParentId(String.valueOf(page.getParent().getId()));
        }
        return entity;
    }
}

