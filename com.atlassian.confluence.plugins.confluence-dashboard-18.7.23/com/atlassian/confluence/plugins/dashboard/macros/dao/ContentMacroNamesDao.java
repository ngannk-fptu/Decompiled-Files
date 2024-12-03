/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 */
package com.atlassian.confluence.plugins.dashboard.macros.dao;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNames;
import java.util.List;

public interface ContentMacroNamesDao {
    default public List<ContentMacroNames> getContentMacroNames(Iterable<Content> contents, boolean includeComments) {
        return this.getContentMacroNames(contents, null, includeComments);
    }

    public List<ContentMacroNames> getContentMacroNames(Iterable<Content> var1, List<ContentMacroNames> var2, boolean var3);
}

