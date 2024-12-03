/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import java.util.List;

public interface CommentService {
    public void copyComments(ContentEntityObject var1, List<Comment> var2, CopySpaceContext var3);

    public void copyFileComments(ContentEntityObject var1, ContentEntityObject var2);
}

