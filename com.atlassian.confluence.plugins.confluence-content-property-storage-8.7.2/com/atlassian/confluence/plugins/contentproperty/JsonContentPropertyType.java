/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapter
 *  com.atlassian.confluence.content.ContentType
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.content.ui.SimpleUiSupport
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.content.ui.SimpleUiSupport;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyEntityAdapter;
import com.atlassian.confluence.plugins.contentproperty.JsonPropertyPermissionDelegate;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class JsonContentPropertyType
implements ContentType {
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final ContentEntityAdapter contentEntityAdapter = new JsonPropertyEntityAdapter();
    private final PermissionDelegate<Object> permissionDelegate;

    public JsonContentPropertyType(@ComponentImport PermissionManager permissionManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.permissionDelegate = new JsonPropertyPermissionDelegate(permissionManager);
    }

    public ContentEntityAdapter getContentAdapter() {
        return this.contentEntityAdapter;
    }

    public PermissionDelegate getPermissionDelegate() {
        return this.permissionDelegate;
    }

    public ContentUiSupport getContentUiSupport() {
        return SimpleUiSupport.getUnknown((WebResourceUrlProvider)this.webResourceUrlProvider);
    }
}

