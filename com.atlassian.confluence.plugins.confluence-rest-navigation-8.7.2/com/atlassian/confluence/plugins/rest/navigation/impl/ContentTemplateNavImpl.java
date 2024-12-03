/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.nav.Navigation$ExperimentalContentTemplateNav
 */
package com.atlassian.confluence.plugins.rest.navigation.impl;

import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.plugins.rest.navigation.impl.AbstractNav;
import com.atlassian.confluence.plugins.rest.navigation.impl.DelegatingPathBuilder;

class ContentTemplateNavImpl
extends DelegatingPathBuilder
implements Navigation.ExperimentalContentTemplateNav {
    private static final String PATH_SEPARATOR = "/";

    public ContentTemplateNavImpl(ContentTemplate contentTemplate, AbstractNav baseBuilder) {
        super("/template/" + contentTemplate.getTemplateId().serialise(), baseBuilder);
    }
}

