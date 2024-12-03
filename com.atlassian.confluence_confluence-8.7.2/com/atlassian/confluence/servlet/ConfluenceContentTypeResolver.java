/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.ContentTypeResolver
 *  com.atlassian.renderer.util.FileTypeUtil
 */
package com.atlassian.confluence.servlet;

import com.atlassian.plugin.servlet.ContentTypeResolver;
import com.atlassian.renderer.util.FileTypeUtil;

public class ConfluenceContentTypeResolver
implements ContentTypeResolver {
    public String getContentType(String requestUrl) {
        return FileTypeUtil.getContentType((String)requestUrl);
    }
}

