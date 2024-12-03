/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.core.ContentEntityObject;

public interface ContentBlueprintInstanceFactory {
    public ContentBlueprintInstance convertToInstance(ContentEntityObject var1, ContentBlueprintInstance var2, Expansion ... var3);
}

