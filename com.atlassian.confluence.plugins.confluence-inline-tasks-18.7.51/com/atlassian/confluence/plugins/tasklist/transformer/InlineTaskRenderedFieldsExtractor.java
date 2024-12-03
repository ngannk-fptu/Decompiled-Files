/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;

@Deprecated
public interface InlineTaskRenderedFieldsExtractor {
    public String renderTaskBody(String var1, ConversionContext var2);

    public String stripTagsFromRenderedBody(String var1);

    public String buildDescription(String var1);
}

