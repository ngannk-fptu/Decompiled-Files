/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Map;

public interface BlueprintManager {
    public static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-create-content-plugin";
    public static final String FIRST_BLUEPRINT_FOR_USER = "firstBlueprintForUser";

    public BlueprintDescriptor getBlueprintDescriptor(ModuleCompleteKey var1);

    @Deprecated
    public Page createAndPinIndexPage(BlueprintDescriptor var1, Space var2);

    public Page createAndPinIndexPage(ContentBlueprint var1, Space var2);

    public String getBlueprintKeyForContent(AbstractPage var1);

    @Deprecated
    public String getIndexPageTitle(BlueprintDescriptor var1);

    public String getIndexPageTitle(ContentBlueprint var1);

    public Page createBlueprintPage(ContentBlueprint var1, ConfluenceUser var2, Space var3, Page var4, Map<String, Object> var5);

    public Page createPageFromTemplate(ContentTemplateRef var1, ConfluenceUser var2, Space var3, Page var4, Map<String, Object> var5);

    public Page createPageFromTemplate(ContentTemplateRef var1, ConfluenceUser var2, Space var3, Page var4, Map<String, Object> var5, SaveContext var6);
}

