/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelling;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rss.AbstractContentEntityRenderSupport;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import io.atlassian.util.concurrent.Timeout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommentRenderSupport
extends AbstractContentEntityRenderSupport<Comment> {
    @Override
    public String getTitle(RssRenderItem<? extends Comment> item) {
        return item.getEntity().getDisplayTitle();
    }

    @Override
    public List<String> getCategoryNames(RssRenderItem<? extends Comment> item) {
        ArrayList<String> categories = new ArrayList<String>();
        ContentEntityObject owningPage = item.getEntity().getContainer();
        if (owningPage != null) {
            for (Labelling labelling : owningPage.getLabellings()) {
                Label label = labelling.getLabel();
                categories.add(label.getName());
            }
        }
        return categories;
    }

    @Override
    public String renderedContext(RssRenderItem<? extends Comment> item, Timeout timeout) {
        Comment comment = item.getEntity();
        ContentEntityObject commentOwner = comment.getContainer();
        Map<String, Object> contextMap = this.contextMap(item, timeout);
        contextMap.put("commentOwner", commentOwner);
        contextMap.put("comment", comment);
        if (comment.getParent() != null) {
            PageContext parentContext = comment.getParent().toPageContext();
            parentContext.setOutputType("feed");
            contextMap.put("parentConversionContext", new DefaultConversionContext(parentContext));
        }
        return VelocityUtils.getRenderedTemplate("templates/rss/comment-rss-content.vm", contextMap);
    }
}

