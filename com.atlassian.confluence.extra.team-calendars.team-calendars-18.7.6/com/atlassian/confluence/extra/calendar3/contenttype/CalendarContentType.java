/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapter
 *  com.atlassian.confluence.content.ContentType
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.security.PermissionDelegate
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.confluence.content.ContentEntityAdapter;
import com.atlassian.confluence.content.ContentType;
import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.extra.calendar3.contenttype.CalendarContentUiSupport;
import com.atlassian.confluence.security.PermissionDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarContentType
implements ContentType {
    private final ContentEntityAdapter contentEntityAdapter;
    private final PermissionDelegate permissionDelegate;
    private final ContentUiSupport contentUiSupport;

    @Autowired
    public CalendarContentType(ContentEntityAdapter contentEntityAdapter, PermissionDelegate permissionDelegate) {
        this.contentEntityAdapter = contentEntityAdapter;
        this.permissionDelegate = permissionDelegate;
        this.contentUiSupport = new CalendarContentUiSupport();
    }

    public ContentEntityAdapter getContentAdapter() {
        return this.contentEntityAdapter;
    }

    public PermissionDelegate getPermissionDelegate() {
        return this.permissionDelegate;
    }

    public ContentUiSupport getContentUiSupport() {
        return this.contentUiSupport;
    }
}

