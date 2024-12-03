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
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.content.ui.SimpleUiSupport;
import com.atlassian.confluence.mail.archive.content.MailContentEntityAdapter;
import com.atlassian.confluence.mail.archive.content.MailPermissionsDelegate;
import com.atlassian.confluence.security.PermissionDelegate;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;

public class MailContentType
implements ContentType {
    private static final MailContentEntityAdapter CONTENT_ADAPTER = new MailContentEntityAdapter();
    private final PermissionDelegate permissionDelegate;
    private final ContentUiSupport contentUiSupport;

    public MailContentType(@ComponentImport WebResourceUrlProvider webResourceUrlProvider, MailPermissionsDelegate permissionDelegate) {
        this.permissionDelegate = permissionDelegate;
        this.contentUiSupport = new SimpleUiSupport(webResourceUrlProvider, "/images/icons/contenttypes/mail_16.png", "icon-mail", "content-type-mail", "mail.name");
    }

    public ContentEntityAdapter getContentAdapter() {
        return CONTENT_ADAPTER;
    }

    public PermissionDelegate getPermissionDelegate() {
        return this.permissionDelegate;
    }

    public ContentUiSupport getContentUiSupport() {
        return this.contentUiSupport;
    }
}

