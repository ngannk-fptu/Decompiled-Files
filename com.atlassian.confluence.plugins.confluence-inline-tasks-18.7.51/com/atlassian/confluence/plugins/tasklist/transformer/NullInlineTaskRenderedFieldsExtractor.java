/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 */
package com.atlassian.confluence.plugins.tasklist.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.plugins.tasklist.transformer.InlineTaskRenderedFieldsExtractor;

public class NullInlineTaskRenderedFieldsExtractor
implements InlineTaskRenderedFieldsExtractor {
    @Override
    public String renderTaskBody(String taskBody, ConversionContext conversionContext) {
        return null;
    }

    @Override
    public String stripTagsFromRenderedBody(String renderedTask) {
        return null;
    }

    @Override
    public String buildDescription(String renderedTask) {
        return null;
    }
}

