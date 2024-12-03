/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.importexport.impl.ExportUtils;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import java.util.Objects;

public class HtmlExportHrefEvaluator
implements HrefEvaluator {
    private HrefEvaluator defaultHrefEvaluator;
    private HrefEvaluator absoluteHrefEvaluator;

    public HtmlExportHrefEvaluator(HrefEvaluator defaultHrefEvaluator, HrefEvaluator absoluteHrefEvaluator) {
        this.defaultHrefEvaluator = defaultHrefEvaluator;
        this.absoluteHrefEvaluator = absoluteHrefEvaluator;
    }

    @Override
    public String createHref(ConversionContext context, Object entity, String anchor) {
        if (entity instanceof Page) {
            return this.createHref(context, (Page)entity, anchor);
        }
        if (entity instanceof Attachment) {
            return this.createHref(context, (Attachment)entity, anchor);
        }
        if (entity instanceof WebLink) {
            return this.createHref(context, (WebLink)entity, anchor);
        }
        return this.absoluteHrefEvaluator.createHref(context, entity, anchor);
    }

    public String createHref(ConversionContext context, Page page, String anchor) {
        ContentTree contentTree = context.getContentTree();
        String entityPath = this.defaultHrefEvaluator.createHref(context, page, anchor);
        if (contentTree != null && contentTree.getPage(page.getId()) != null) {
            int anchorIndex = entityPath.indexOf("#");
            if (anchorIndex == 0) {
                return entityPath;
            }
            String fileName = ExportUtils.getTitleAsHref(page);
            if (anchorIndex > 0 && anchorIndex < entityPath.length()) {
                return fileName + entityPath.substring(anchorIndex);
            }
            return fileName;
        }
        return this.absoluteHrefEvaluator.createHref(context, page, anchor);
    }

    public String createHref(ConversionContext context, Attachment attachment, String anchor) {
        ContentTree contentTree = context.getContentTree();
        ContentEntityObject container = Objects.requireNonNull(attachment.getContainer());
        if (contentTree != null && contentTree.getPage(container.getId()) != null) {
            return attachment.getExportPath();
        }
        return this.absoluteHrefEvaluator.createHref(context, attachment, anchor);
    }

    public String createHref(ConversionContext context, WebLink link, String anchor) {
        if (!link.isRelative()) {
            return this.defaultHrefEvaluator.createHref(context, link, anchor);
        }
        return this.absoluteHrefEvaluator.createHref(context, link, anchor);
    }
}

