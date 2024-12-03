/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.dragdrop.service;

import com.atlassian.confluence.core.ContentEntityObject;

public interface DragAndDropService {
    public String getAttachmentEditorHtml(String var1, ContentEntityObject var2, boolean var3, String var4) throws Exception;

    public String getAttachmentEditorHtml(String var1, ContentEntityObject var2) throws Exception;
}

