/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.ui;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.content.ui.ApiContentUiSupport;
import com.atlassian.confluence.content.ui.AttachmentUiSupport;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import org.apache.commons.lang3.StringUtils;

public class AttachmentApiContentUiSupport
implements ApiContentUiSupport<Content> {
    private final WebResourceUrlProvider webResourceUrlProvider;

    public AttachmentApiContentUiSupport(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public String getIconFilePath(Content content, int size) {
        return this.getPathImpl(content);
    }

    @Override
    public String getIconPath(Content content, int size) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + this.getPathImpl(content);
    }

    private String getPathImpl(Content content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMetadata().get("mediaType").toString();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getIconFilePath();
    }

    @Override
    public String getLegacyIconPath(String contentType, int size) {
        return this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.RELATIVE) + AttachmentUiSupport.AttachmentInfo.UNKNOWN.getIconFilePath();
    }

    @Override
    public String getIconCssClass(Content content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMetadata().get("mediaType").toString();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getCssClass();
    }

    @Override
    public String getContentCssClass(String contentType, String contentPluginKey) {
        return AttachmentUiSupport.AttachmentInfo.UNKNOWN.getCssClass();
    }

    @Override
    public String getContentCssClass(Content content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMetadata().get("mediaType").toString();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getContentCssClass();
    }

    @Override
    public String getIconCssClass(SearchResult result) {
        String fileExtension = this.getFileExtension(result.getDisplayTitle());
        String mimeType = result.getField("attachment-mime-type");
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getCssClass();
    }

    @Override
    public String getContentTypeI18NKey(Content content) {
        String fileExtension = this.getFileExtension(content.getTitle());
        String mimeType = content.getMetadata().get("mediaType").toString();
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getI18nKey();
    }

    @Override
    public String getContentTypeI18NKey(SearchResult result) {
        String fileExtension = this.getFileExtension(result.getDisplayTitle());
        String mimeType = result.getField("attachment-mime-type");
        return AttachmentUiSupport.getAttachmentInfo(mimeType, fileExtension).getI18nKey();
    }

    private String getFileExtension(String filename) {
        return StringUtils.substringAfterLast((String)filename, (String)".");
    }
}

