/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import java.awt.AWTError;
import java.awt.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThumbnailInfo {
    private static final Logger log = LoggerFactory.getLogger(ThumbnailInfo.class);
    public static final int THUMBNAIL_DEFAULT_WIDTH = 300;
    private final Attachment attachment;
    private final String attachmentsPath;
    private final ImageDimensions maxDimensions;
    private final ImageDetails imageDetails;

    public static boolean systemSupportsThumbnailing() {
        try {
            Toolkit.getDefaultToolkit();
        }
        catch (AWTError e) {
            log.error("Unable to acquire AWT default toolkit - thumbnails will not be displayed. Check DISPLAY variable or use setting -Djava.awt.headless=true.", (Throwable)e);
            return false;
        }
        return true;
    }

    @Deprecated
    public ThumbnailInfo(ImageDetails imageDetails, Dimensions maxDimensions, String attachmentsPath) {
        this(imageDetails, maxDimensions.getImageDimensions(), attachmentsPath);
    }

    public ThumbnailInfo(ImageDetails imageDetails, ImageDimensions maxDimensions, String attachmentsPath) {
        if (imageDetails == null) {
            throw new IllegalArgumentException("Image Details must not be null");
        }
        if (imageDetails.getAttachment() == null) {
            throw new IllegalArgumentException("Attachment must not be null");
        }
        this.attachment = imageDetails.getAttachment();
        this.imageDetails = imageDetails;
        this.maxDimensions = maxDimensions;
        this.attachmentsPath = attachmentsPath;
    }

    public int getOriginalWidth() {
        return this.imageDetails == null ? -1 : this.imageDetails.getWidth();
    }

    public int getOriginalHeight() {
        return this.imageDetails == null ? -1 : this.imageDetails.getHeight();
    }

    public int getThumbnailWidth() {
        int possibleWidth = (int)Math.round((double)this.getOriginalWidth() * this.getFloorHeightRatio());
        return possibleWidth < this.maxDimensions.getWidth() ? possibleWidth : this.maxDimensions.getWidth();
    }

    public int getThumbnailHeight() {
        int possibleHeight = (int)Math.round((double)this.getOriginalHeight() * this.getFloorWidthRatio());
        return possibleHeight < this.maxDimensions.getHeight() ? possibleHeight : this.maxDimensions.getHeight();
    }

    public String getThumbnailUrlPath() {
        return ThumbnailInfo.createThumbnailUrlPathFromAttachmentUrl(this.getAttachmentUrl());
    }

    public static String createThumbnailUrlPathFromAttachmentUrl(String attachmentUrl) {
        return attachmentUrl.replaceAll("/attachments/", "/thumbnails/");
    }

    @HtmlSafe
    public String getPopupLink(String imageParameters) {
        String originalSize = this.getOriginalWidth() + "x" + this.getOriginalHeight();
        return "<a class=\"confluence-thumbnail-link " + originalSize + "\" href='" + this.getServerAttachmentUrl() + "'>" + this.getThumbnailImageHtml(imageParameters) + "</a>";
    }

    @HtmlSafe
    public String getPopupLinkPrefix() {
        String originalSize = this.getOriginalWidth() + "x" + this.getOriginalHeight();
        return "<a class=\"confluence-thumbnail-link " + originalSize + "\" href='" + this.getServerAttachmentUrl() + "'>";
    }

    @HtmlSafe
    public String getPopupLinkSuffix() {
        return "</a>";
    }

    @HtmlSafe
    public String getThumbnailImageHtml(String imageParameters) {
        Object extraParams = "";
        if (imageParameters == null || !imageParameters.contains("border=")) {
            extraParams = "border='0' ";
        }
        extraParams = (String)extraParams + "draggable='false' ";
        String result = "<img " + (String)extraParams + "src='" + HtmlEscaper.escapeAll((String)this.getThumbnailUrlPath(), (boolean)true) + "' ";
        if (this.getThumbnailWidth() > 0) {
            result = result + "width='" + this.getThumbnailWidth() + "' ";
        }
        if (this.getThumbnailHeight() > 0) {
            result = result + "height='" + this.getThumbnailHeight() + "' ";
        }
        result = result + "data-image-src='" + HtmlEscaper.escapeAll((String)this.getAttachmentUrl(), (boolean)true) + "' ";
        result = result + "class='confluence-embedded-image'";
        result = result + this.mungeParameters(imageParameters) + ">";
        return result;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    private String mungeParameters(String imageParameters) {
        if (imageParameters == null) {
            return "";
        }
        return " " + imageParameters;
    }

    private double getFloorHeightRatio() {
        return this.getFloorRatio(this.getOriginalHeight(), this.maxDimensions.getHeight());
    }

    private double getFloorWidthRatio() {
        return this.getFloorRatio(this.getOriginalWidth(), this.maxDimensions.getWidth());
    }

    private double getFloorRatio(int actual, int desired) {
        if (actual <= desired) {
            return 1.0;
        }
        return (double)desired / (double)actual;
    }

    private String getAttachmentUrl() {
        return this.attachment.getDownloadPath(this.attachmentsPath, true);
    }

    private String getServerAttachmentUrl() {
        String serverPath = ConfluenceRenderUtils.getAbsoluteAttachmentRemotePath(this.attachment);
        return serverPath + "/" + HtmlEscaper.escapeAll((String)this.attachment.getFileName(), (boolean)true);
    }

    public String getImageMimeType() {
        return this.imageDetails == null ? null : this.imageDetails.getMimeType();
    }
}

