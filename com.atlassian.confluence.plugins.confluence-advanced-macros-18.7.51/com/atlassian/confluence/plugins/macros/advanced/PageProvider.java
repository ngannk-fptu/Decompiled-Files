/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.service.NotAuthorizedException
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.xhtml.api.Link;

public interface PageProvider {
    public ContentEntityObject resolve(String var1, ConversionContext var2) throws NotAuthorizedException;

    public ContentEntityObject resolve(Link var1, ConversionContext var2);
}

