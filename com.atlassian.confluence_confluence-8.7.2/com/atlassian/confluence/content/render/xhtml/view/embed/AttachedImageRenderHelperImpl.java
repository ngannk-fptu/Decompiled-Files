/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageRenderHelper;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageUnresolvedCommentCountAggregator;
import com.atlassian.confluence.pages.CommentManager;

public class AttachedImageRenderHelperImpl
implements AttachedImageRenderHelper {
    private final CommentManager commentManager;

    public AttachedImageRenderHelperImpl(CommentManager commentManager) {
        this.commentManager = commentManager;
    }

    @Override
    public AttachedImageUnresolvedCommentCountAggregator getUnresolvedCommentCountAggregatorFrom(ConversionContext conversionContext) {
        AttachedImageUnresolvedCommentCountAggregator aggregator = (AttachedImageUnresolvedCommentCountAggregator)conversionContext.getProperty("UnresolvedCommentCountAggregator");
        if (aggregator == null) {
            aggregator = new AttachedImageUnresolvedCommentCountAggregator(this.commentManager);
            conversionContext.setProperty("UnresolvedCommentCountAggregator", aggregator);
        }
        return aggregator;
    }
}

