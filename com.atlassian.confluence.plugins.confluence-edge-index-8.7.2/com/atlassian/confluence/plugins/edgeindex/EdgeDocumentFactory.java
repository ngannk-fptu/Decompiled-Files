/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.search.v2.AtlassianDocument
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;

public interface EdgeDocumentFactory {
    public AtlassianDocument buildDocument(String var1, UserKey var2, ContentEntityObject var3, Date var4, String var5);
}

