/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.SubRenderer
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import org.apache.commons.lang3.StringUtils;

public class ContentMacrosHelper {
    private ContentPropertyManager contentPropertyManager;
    private SubRenderer subRenderer;

    public String getExcerpt(ContentEntityObject content, PageContext pageContext) {
        String excerpt = this.contentPropertyManager.getTextProperty(content, "confluence.excerpt");
        if (!StringUtils.isNotEmpty((CharSequence)excerpt)) {
            return null;
        }
        int i = (excerpt = excerpt.trim()).indexOf("\n");
        if (i > 0) {
            excerpt = excerpt.substring(0, i);
        }
        return this.subRenderer.render(excerpt, (RenderContext)pageContext, RenderMode.INLINE);
    }

    public void setContentPropertyManager(ContentPropertyManager contentPropertyManager) {
        this.contentPropertyManager = contentPropertyManager;
    }

    public void setSubRenderer(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }
}

