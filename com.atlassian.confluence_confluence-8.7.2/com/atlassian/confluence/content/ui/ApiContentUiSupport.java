/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.search.v2.SearchResult;

public interface ApiContentUiSupport<T extends Container> {
    public String getIconFilePath(T var1, int var2);

    public String getIconPath(T var1, int var2);

    public String getLegacyIconPath(String var1, int var2);

    public String getIconCssClass(T var1);

    public String getContentCssClass(T var1);

    public String getContentCssClass(String var1, String var2);

    public String getIconCssClass(SearchResult var1);

    public String getContentTypeI18NKey(T var1);

    public String getContentTypeI18NKey(SearchResult var1);
}

