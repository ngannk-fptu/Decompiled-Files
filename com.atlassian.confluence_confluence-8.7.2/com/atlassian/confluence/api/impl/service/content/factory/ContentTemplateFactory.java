/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.pages.templates.PageTemplate;

@Deprecated
public interface ContentTemplateFactory {
    public ContentTemplate buildFrom(PageTemplate var1, Expansions var2);
}

