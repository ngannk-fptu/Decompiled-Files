/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.rss.AbstractRenderSupport;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import io.atlassian.util.concurrent.Timeout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttachmentRenderSupport
extends AbstractRenderSupport<Attachment> {
    private AttachmentManager attachmentManager;

    private Attachment getAttachment(RssRenderItem<? extends Attachment> item) {
        return item.getEntity();
    }

    @Override
    public String getTitle(RssRenderItem<? extends Attachment> item) {
        Attachment attachment = this.getAttachment(item);
        ContentEntityObject content = attachment.getContainer();
        String containerTitle = content != null ? content.getTitle() : "<global>";
        return containerTitle + " > " + attachment.getFileName();
    }

    @Override
    public String getLink(RssRenderItem<? extends Attachment> item) {
        return this.getAttachment(item).getDownloadPath();
    }

    public List<String> getCategoryNames(RssRenderItem<? extends Attachment> item) {
        ArrayList<String> categories = new ArrayList<String>();
        Attachment attachment = this.getAttachment(item);
        ContentEntityObject container = attachment.getContainer();
        if (container == null) {
            return categories;
        }
        for (Label label : container.getLabels()) {
            categories.add(label.getName());
        }
        return categories;
    }

    @Override
    public String renderedContext(RssRenderItem<? extends Attachment> item, Timeout timeout) {
        Attachment attachment = this.getAttachment(item);
        ContentEntityObject content = attachment.getContainer();
        List<Attachment> historyList = this.attachmentManager.getPreviousVersions(attachment);
        Map<String, Object> contextMap = this.contextMap(item, timeout);
        contextMap.put("attachment", attachment);
        contextMap.put("page", attachment.getContainer());
        contextMap.put("contentObject", content);
        contextMap.put("historyList", historyList);
        return VelocityUtils.getRenderedTemplate("templates/rss/attachment-rss-content.vm", contextMap);
    }

    @Override
    protected RenderContext renderContext(Attachment attachment, Timeout timeout) {
        return null;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }
}

