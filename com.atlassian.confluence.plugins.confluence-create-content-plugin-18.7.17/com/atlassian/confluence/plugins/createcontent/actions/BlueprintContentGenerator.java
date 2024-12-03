/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.spaces.Space;
import java.util.Map;

public interface BlueprintContentGenerator {
    @Deprecated
    public Page generateBlueprintPageObject(PluginTemplateReference var1, Map<String, ?> var2);

    @Deprecated
    public Page createIndexPageObject(PluginTemplateReference var1, Map<String, Object> var2);

    public Page createIndexPageObject(ContentTemplateRef var1, Space var2, Map<String, Object> var3);

    @Deprecated
    public Page generateBlueprintPageObject(ContentTemplateRef var1, Space var2, Map<String, Object> var3);

    public Page generateBlueprintPageObject(CreateBlueprintPageRequest var1);
}

