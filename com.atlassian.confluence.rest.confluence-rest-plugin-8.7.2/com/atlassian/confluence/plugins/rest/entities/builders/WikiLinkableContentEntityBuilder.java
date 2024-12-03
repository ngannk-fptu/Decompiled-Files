/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.HasLinkWikiMarkup
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 */
package com.atlassian.confluence.plugins.rest.entities.builders;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.HasLinkWikiMarkup;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.entities.builders.DefaultContentEntityBuilder;
import com.atlassian.confluence.plugins.rest.manager.DateEntityFactory;
import com.atlassian.confluence.plugins.rest.manager.UserEntityHelper;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;

public class WikiLinkableContentEntityBuilder<T extends ContentEntityObject>
extends DefaultContentEntityBuilder<T> {
    public WikiLinkableContentEntityBuilder(GlobalSettingsManager settingsManager, DateEntityFactory dateEntityFactory, UserEntityHelper userEntityHelper) {
        super(settingsManager, dateEntityFactory, userEntityHelper);
    }

    @Override
    public ContentEntity build(T object) {
        ContentEntity entity = super.build(object);
        entity.setWikiLink(((HasLinkWikiMarkup)object).getLinkWikiMarkup());
        return entity;
    }
}

