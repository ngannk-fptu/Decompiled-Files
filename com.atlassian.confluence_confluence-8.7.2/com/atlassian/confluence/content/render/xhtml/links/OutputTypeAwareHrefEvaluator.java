/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import java.util.Map;

public class OutputTypeAwareHrefEvaluator
implements HrefEvaluator {
    private final Map<String, HrefEvaluator> outputTypeEvaluators;
    private final HrefEvaluator defaultEvaluator;

    public OutputTypeAwareHrefEvaluator(Map<String, HrefEvaluator> outputTypeEvaluators, HrefEvaluator defaultEvaluator) {
        this.outputTypeEvaluators = outputTypeEvaluators;
        this.defaultEvaluator = defaultEvaluator;
    }

    @Override
    public String createHref(ConversionContext context, Object entity, String anchor) {
        HrefEvaluator hrefEvaluator = null;
        if (context != null) {
            String outputType = context.getOutputType();
            hrefEvaluator = this.outputTypeEvaluators.get(outputType);
        }
        if (hrefEvaluator == null) {
            hrefEvaluator = this.defaultEvaluator;
        }
        return hrefEvaluator.createHref(context, entity, anchor);
    }
}

