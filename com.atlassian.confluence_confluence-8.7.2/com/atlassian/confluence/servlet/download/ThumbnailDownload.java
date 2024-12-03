/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumbnail$MimeType
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.event.events.content.attachment.ProfilePictureThumbnailViewEvent;
import com.atlassian.confluence.event.events.content.attachment.ThumbnailViewEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.servlet.download.AttachmentDownload;
import com.atlassian.core.util.thumbnail.Thumbnail;
import java.io.InputStream;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThumbnailDownload
extends AttachmentDownload {
    @Override
    protected void setHeadersForAttachment(InputStream contents, String name, long contentLength, String contentType, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType(Thumbnail.MimeType.PNG.toString());
        httpServletResponse.setHeader("Content-Length", Long.toString(contentLength));
        httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
    }

    @Override
    protected String getUrlPrefix() {
        return "thumbnails";
    }

    @Override
    protected void publishEvents(Attachment attachment, boolean download) {
        if (attachment.isUserProfilePicture()) {
            this.getEventPublisher().publish((Object)new ProfilePictureThumbnailViewEvent((Object)this, attachment));
        } else {
            this.getEventPublisher().publish((Object)new ThumbnailViewEvent((Object)this, attachment));
        }
    }

    @Override
    protected Optional<String> getStreamingAnalyticsEventName() {
        return Optional.empty();
    }
}

