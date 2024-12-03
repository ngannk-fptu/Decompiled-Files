/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;

public interface LinksUpdater {
    public String rewriteLinks(String var1, ContentEntityObject var2, CopySpaceContext var3);
}

