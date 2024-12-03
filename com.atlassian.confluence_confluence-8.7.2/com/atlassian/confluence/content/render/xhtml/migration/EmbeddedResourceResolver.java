/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.embedded.EmbeddedResource
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.embedded.EmbeddedResource;

public interface EmbeddedResourceResolver {
    public Attachment resolve(EmbeddedResource var1, PageContext var2);
}

