/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.http.mime.ContentDispositionHeaderGuesser
 *  com.atlassian.renderer.embedded.EmbeddedFlash
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.io.ByteStreams
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.servlet.download.DispositionType;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.util.AttachmentMimeTypeTranslator;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.http.mime.ContentDispositionHeaderGuesser;
import com.atlassian.renderer.embedded.EmbeddedFlash;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAttachmentSafeContentHeaderGuesser
implements SafeContentHeaderGuesser {
    private static final Logger log = LoggerFactory.getLogger(SafeContentHeaderGuesser.class);
    private AttachmentMimeTypeTranslator mimeTypeTranslator;
    private ContentDispositionHeaderGuesser contentTypeAndDispositionHeaderBlacklist;

    private boolean isInternetExplorer(String userAgent) {
        return userAgent != null && userAgent.contains("MSIE") && !userAgent.contains("Opera");
    }

    private boolean isImageMimeType(String contentType) {
        return StringUtils.isNotEmpty((CharSequence)contentType) && contentType.startsWith("image/");
    }

    private boolean isImage(InputStream contents) throws IOException {
        return this.isImageMimeType(this.guessContentTypeFromStream(contents));
    }

    @Override
    public Map<String, String> computeAttachmentHeaders(String contentType, InputStream contents, String name, String userAgent, long contentLength, boolean hasXsrfToken, Map<String, String[]> httpQueryParams) throws IOException {
        String[] downloadSet;
        DispositionType type;
        if (null != this.mimeTypeTranslator) {
            contentType = this.mimeTypeTranslator.resolveMimeType(name, contentType);
        }
        if (!(type = (downloadSet = httpQueryParams.get("download")) != null && downloadSet[0].equals("true") ? DispositionType.ATTACHMENT : this.guessDispositionType(name, contentType, userAgent)).equals((Object)DispositionType.INLINE) && contentType.equals(EmbeddedFlash.RESOURCE_TYPE) && hasXsrfToken) {
            type = DispositionType.INLINE;
        }
        ImmutableMap.Builder headers = ImmutableMap.builder();
        headers.put((Object)"Content-Type", (Object)contentType).put((Object)"Content-Length", (Object)Long.toString(contentLength));
        if (!this.isImageMimeType(contentType) || !this.isImage(contents)) {
            headers.put((Object)"X-Content-Type-Options", (Object)"nosniff");
        }
        this.guessContentDisposition(name, userAgent, type, (ImmutableMap.Builder<String, String>)headers);
        return headers.build();
    }

    private DispositionType guessDispositionType(String fileName, String contentType, String userAgent) {
        String dispositionTypeString = this.contentTypeAndDispositionHeaderBlacklist.guessContentDispositionHeader(fileName, contentType, userAgent);
        if (dispositionTypeString != null) {
            for (DispositionType candidateDispositionType : DispositionType.values()) {
                if (!dispositionTypeString.toUpperCase(Locale.ENGLISH).equals(candidateDispositionType.name().toUpperCase(Locale.ENGLISH))) continue;
                return candidateDispositionType;
            }
        }
        log.error("The guessed Content-Disposition header [{}] for filename [{}], Content-Type [{}] and User-Agent [{}] does not map to any of these values {}, defaulting to [{}].", new Object[]{dispositionTypeString, fileName, contentType, userAgent, ToStringBuilder.reflectionToString((Object)DispositionType.values(), (ToStringStyle)ToStringStyle.SIMPLE_STYLE), DispositionType.ATTACHMENT});
        return DispositionType.ATTACHMENT;
    }

    private void guessContentDisposition(String downloadFileName, String userAgent, DispositionType type, ImmutableMap.Builder<String, String> headers) {
        if (GeneralUtil.isAllAscii(downloadFileName)) {
            if (this.isInternetExplorer(userAgent) && downloadFileName.indexOf(32) != -1) {
                downloadFileName = downloadFileName.replaceAll("\\s", "%20");
            }
            headers.put((Object)"Content-Disposition", (Object)(type.getValue() + "; filename=\"" + downloadFileName + "\""));
            return;
        }
        if (this.isInternetExplorer(userAgent)) {
            String encodedFileName = HtmlUtil.urlEncode(downloadFileName);
            if (encodedFileName.indexOf(43) != -1) {
                encodedFileName = encodedFileName.replaceAll("\\+", "%20");
            }
            headers.put((Object)"Content-Disposition", (Object)(type.getValue() + "; filename=\"" + encodedFileName + "\""));
            return;
        }
        headers.put((Object)"Content-Disposition", (Object)type.getValue());
    }

    private String guessContentTypeFromStream(InputStream contents) throws IOException {
        Preconditions.checkArgument((boolean)contents.markSupported(), (Object)"InputStream must support resetting");
        String guessedContentType = URLConnection.guessContentTypeFromStream(contents);
        if (guessedContentType != null && !"audio/x-wav".equals(guessedContentType)) {
            return guessedContentType;
        }
        byte[] head = new byte[14];
        contents.mark(head.length);
        int count = ByteStreams.read((InputStream)contents, (byte[])head, (int)0, (int)head.length);
        contents.reset();
        if (head[0] == 0 && head[1] == 0 && (head[2] == 1 || head[2] == 2) && count >= 4 && head[3] == 0) {
            return "image/x-icon";
        }
        if (head[0] == 66 && head[1] == 77) {
            return "image/bmp";
        }
        if (head[0] == 82 && head[1] == 73 && head[2] == 70 && head[3] == 70 && head[8] == 87 && head[9] == 69 && head[10] == 66 && head[11] == 80 && head[12] == 86 && head[13] == 80) {
            return "image/webp";
        }
        return guessedContentType;
    }

    public void setMimeTypeTranslator(AttachmentMimeTypeTranslator mimeTypeTranslator) {
        this.mimeTypeTranslator = mimeTypeTranslator;
    }

    public void setContentTypeAndDispositionHeaderBlacklist(ContentDispositionHeaderGuesser contentTypeAndDispositionHeaderBlacklist) {
        this.contentTypeAndDispositionHeaderBlacklist = contentTypeAndDispositionHeaderBlacklist;
    }
}

