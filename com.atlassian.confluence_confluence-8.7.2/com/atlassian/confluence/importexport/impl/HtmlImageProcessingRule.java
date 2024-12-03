/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.html.BasicRule
 *  com.opensymphony.module.sitemesh.html.CustomTag
 *  com.opensymphony.module.sitemesh.html.Tag
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.impl.ExportImageDescriptor;
import com.atlassian.confluence.importexport.impl.ExportPathUtils;
import com.atlassian.confluence.importexport.impl.ImageProcessingRule;
import com.atlassian.confluence.servlet.download.AttachmentUrlParser;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.opensymphony.module.sitemesh.html.BasicRule;
import com.opensymphony.module.sitemesh.html.CustomTag;
import com.opensymphony.module.sitemesh.html.Tag;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlImageProcessingRule
extends BasicRule
implements ImageProcessingRule {
    private static final Logger log = LoggerFactory.getLogger(HtmlImageProcessingRule.class);
    private final Set<ExportImageDescriptor> urls = new HashSet<ExportImageDescriptor>();
    private SettingsManager settingsManager;
    private AttachmentUrlParser attachmentUrlParser;

    public HtmlImageProcessingRule() {
        super(new String[]{"img"});
    }

    public void process(Tag tag) {
        if (tag.hasAttribute("src", false)) {
            String imageSource = tag.getAttributeValue("src", false);
            if (this.isConfluenceResource(imageSource)) {
                this.currentBuffer().delete(tag.getPosition(), tag.getLength());
                String exportSource = ExportPathUtils.constructRelativeExportPath(this.getBaseUrl(), imageSource, this.attachmentUrlParser);
                ExportImageDescriptor imageDescriptor = new ExportImageDescriptor(imageSource, exportSource);
                CustomTag customTag = new CustomTag(tag);
                customTag.setAttributeValue("src", false, exportSource);
                customTag.writeTo(this.currentBuffer(), tag.getPosition());
                this.urls.add(imageDescriptor);
            } else if (log.isDebugEnabled()) {
                log.debug("not confluence resource: " + imageSource);
            }
        }
    }

    private boolean isConfluenceResource(String imageSource) {
        return imageSource.startsWith("/") || imageSource.startsWith(this.getBaseUrl());
    }

    private String getBaseUrl() {
        return this.settingsManager.getGlobalSettings().getBaseUrl();
    }

    @Override
    public Set<ExportImageDescriptor> getExtractedUrls() {
        return this.urls;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setAttachmentUrlParser(AttachmentUrlParser attachmentUrlParser) {
        this.attachmentUrlParser = attachmentUrlParser;
    }
}

