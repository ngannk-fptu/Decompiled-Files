/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapter
 *  com.atlassian.confluence.content.ContentType
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.content.ui.SimpleUiSupport
 *  com.atlassian.confluence.security.PermissionDelegate
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.content.type;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.content.ui.SimpleUiSupport;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.type.CustomEmoticonEntityAdapter;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.type.CustomEmoticonPermissionDelegate;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class CustomEmoticonContentType
implements ContentType {
    public static final String KEY = "com.atlassian.confluence.plugins.confluence-emoticons-plugin:custom-emoticon";
    private final ContentEntityAdapter contentEntityAdapter;
    private final PermissionDelegate permissionDelegate;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public CustomEmoticonContentType(@ComponentImport WebResourceUrlProvider webResourceUrlProvider, CustomEmoticonEntityAdapter contentEntityAdapter, CustomEmoticonPermissionDelegate permissionDelegate) {
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.contentEntityAdapter = contentEntityAdapter;
        this.permissionDelegate = permissionDelegate;
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

