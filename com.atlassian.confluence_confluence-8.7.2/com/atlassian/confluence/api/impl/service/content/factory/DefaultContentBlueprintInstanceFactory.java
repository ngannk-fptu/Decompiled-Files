/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.impl.service.content.factory.ContentBlueprintInstanceFactory;
import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.core.ContentEntityObject;

public class DefaultContentBlueprintInstanceFactory
implements com.atlassian.confluence.pages.templates.ContentBlueprintInstanceFactory,
ContentBlueprintInstanceFactory {
    private final ContentFactory contentFactory;

    public DefaultContentBlueprintInstanceFactory(ContentFactory contentFactory) {
        this.contentFactory = contentFactory;
    }

    @Override
    public ContentBlueprintInstance convertToInstance(ContentEntityObject ceo, ContentBlueprintInstance contentBlueprintInstance, Expansion ... expansions) {
        return ContentBlueprintInstance.builder().content(this.contentFactory.buildFrom(ceo, new Expansions(expansions))).contentBlueprintSpec(contentBlueprintInstance.getContentBlueprintSpec()).build();
    }
}

