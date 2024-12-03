/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.service.web;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.web.WebView;
import java.util.Map;

@Internal
public interface WebViewService {
    @Deprecated
    public WebView forContent(String var1);

    public WebView forContent(ContentId var1);

    public WebView forSpace(String var1);

    public WebView forContent(ContentId var1, Map<String, Object> var2);

    public WebView forGeneric();
}

