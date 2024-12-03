/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.extensions.BlueprintDescriptor;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.spaces.Space;

public interface IndexPageManager {
    @Deprecated
    public Page getOrCreateIndexPage(BlueprintDescriptor var1, Space var2, String var3);

    public Page getOrCreateIndexPage(ContentBlueprint var1, Space var2, String var3);

    public Page findIndexPage(ContentBlueprint var1, Space var2);

    public Page createIndexPage(ContentBlueprint var1, Space var2, String var3);
}

