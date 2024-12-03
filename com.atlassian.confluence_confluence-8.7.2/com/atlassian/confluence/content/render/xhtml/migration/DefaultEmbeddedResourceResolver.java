/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.embedded.EmbeddedResource
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.EmbeddedResourceResolver;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.embedded.EmbeddedResourceUtils;
import com.atlassian.renderer.embedded.EmbeddedResource;

public class DefaultEmbeddedResourceResolver
implements EmbeddedResourceResolver {
    @Override
    public Attachment resolve(EmbeddedResource embeddedResource, PageContext pageContext) {
        return EmbeddedResourceUtils.resolveAttachment(pageContext, embeddedResource);
    }
}

