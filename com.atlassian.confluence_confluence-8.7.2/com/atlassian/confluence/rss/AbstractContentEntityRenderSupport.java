/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.spring.container.ContainerManager
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rss.AbstractRenderSupport;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.spring.container.ContainerManager;
import io.atlassian.util.concurrent.Timeout;
import java.util.List;

public abstract class AbstractContentEntityRenderSupport<T extends ContentEntityObject>
extends AbstractRenderSupport<T> {
    protected WikiStyleRenderer wikiStyleRenderer;

    public List<String> getCategoryNames(RssRenderItem<? extends T> item) {
        ContentEntityObject contentEntityObject = (ContentEntityObject)item.getEntity();
        List<Label> labels = contentEntityObject.getLabels();
        return LabelUtil.getVisibleLabelNames(labels, AuthenticatedUserThreadLocal.getUsername());
    }

    @Override
    public String getLink(RssRenderItem<? extends T> item) {
        return ((ContentEntityObject)item.getEntity()).getUrlPath();
    }

    protected String getText(String key) {
        ConfluenceActionSupport dummy = new ConfluenceActionSupport();
        ContainerManager.autowireComponent((Object)dummy);
        return dummy.getText(key);
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    protected RenderContext renderContext(T entity, Timeout timeout) {
        PageContext context = PageContext.newContextWithTimeout(entity, timeout);
        context.setOutputType(ConversionContextOutputType.FEED.value());
        return context;
    }
}

