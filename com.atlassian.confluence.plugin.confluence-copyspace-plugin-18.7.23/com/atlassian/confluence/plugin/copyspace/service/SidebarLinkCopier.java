/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import java.util.List;

public interface SidebarLinkCopier {
    public void copyNonRewritableLinks(String var1, String var2);

    public void checkAndCopyRewritableSidebarLink(long var1, ContentEntityObject var3, String var4, String var5);

    public void checkAndCopyRewritableAttachmentSidebarLink(List<Attachment> var1, ContentEntityObject var2, String var3, String var4);
}

