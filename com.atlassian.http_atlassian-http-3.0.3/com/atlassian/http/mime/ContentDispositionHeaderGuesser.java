/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.tika.detect.TextDetector
 *  org.apache.tika.mime.MediaType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.http.mime;

import com.atlassian.http.mime.BrowserUtils;
import com.atlassian.http.mime.DownloadHeaderHelper;
import com.atlassian.http.mime.DownloadPolicy;
import com.atlassian.http.mime.DownloadPolicyProvider;
import com.atlassian.http.mime.HostileExtensionDetector;
import com.atlassian.http.mime.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.tika.detect.TextDetector;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentDispositionHeaderGuesser {
    private static final Logger log = LoggerFactory.getLogger(ContentDispositionHeaderGuesser.class);
    private static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";
    private static final String CONTENT_DISPOSITION_INLINE = "inline";
    private final DownloadPolicyProvider downloadPolicyProvider;
    private final HostileExtensionDetector hostileExtensionDetector;
    private final TextDetector textDetector;

    @VisibleForTesting
    ContentDispositionHeaderGuesser(DownloadPolicyProvider downloadPolicyProvider, HostileExtensionDetector hostileExtensionDetector, TextDetector textDetector) {
        this.downloadPolicyProvider = downloadPolicyProvider;
        this.hostileExtensionDetector = hostileExtensionDetector;
        this.textDetector = textDetector;
    }

    public ContentDispositionHeaderGuesser(DownloadPolicyProvider downloadPolicyProvider, HostileExtensionDetector hostileExtensionDetector) {
        this(downloadPolicyProvider, hostileExtensionDetector, new TextDetector());
    }

    public String guessContentDispositionHeader(String fileName, String mimeContentType, String userAgent) {
        DownloadPolicy downloadPolicy = this.downloadPolicyProvider.getPolicy();
        boolean forceDownload = false;
        if (downloadPolicy.equals((Object)DownloadPolicy.Insecure)) {
            return CONTENT_DISPOSITION_INLINE;
        }
        if (downloadPolicy.equals((Object)DownloadPolicy.Secure) || downloadPolicy.equals((Object)DownloadPolicy.WhiteList)) {
            return CONTENT_DISPOSITION_ATTACHMENT;
        }
        forceDownload = this.isExecutableContent(fileName, mimeContentType);
        if (BrowserUtils.isIE(userAgent) && this.isTextContentType(mimeContentType)) {
            forceDownload = true;
        }
        if (forceDownload && log.isDebugEnabled()) {
            log.debug("\"" + fileName + "\" (" + mimeContentType + ") presents as executable content, forcing download.");
        }
        return forceDownload ? CONTENT_DISPOSITION_ATTACHMENT : CONTENT_DISPOSITION_INLINE;
    }

    public Map<String, String> determineHeadersFromContent(String fileName, String mimeContentType, String userAgent, InputStream inputStream) {
        DownloadPolicy downloadPolicy = this.downloadPolicyProvider.getPolicy();
        boolean isIEBefore8 = BrowserUtils.isIE(userAgent) && !BrowserUtils.isIE8OrGreater(userAgent);
        boolean safeContentType = this.hostileExtensionDetector.isSafeContentType(mimeContentType);
        if (downloadPolicy.equals((Object)DownloadPolicy.WhiteList)) {
            String contentDisposition;
            String string = contentDisposition = safeContentType ? CONTENT_DISPOSITION_INLINE : CONTENT_DISPOSITION_ATTACHMENT;
            if (isIEBefore8) {
                contentDisposition = CONTENT_DISPOSITION_ATTACHMENT;
            } else if (!this.isBinary(inputStream)) {
                mimeContentType = "text/plain";
                contentDisposition = CONTENT_DISPOSITION_INLINE;
            }
            return new DownloadHeaderHelper(contentDisposition, fileName, mimeContentType).getDownloadHeaders();
        }
        String contentDisposition = this.guessContentDispositionHeader(fileName, mimeContentType, userAgent);
        if (downloadPolicy.equals((Object)DownloadPolicy.Smart) && this.hostileExtensionDetector.isTextContentType(mimeContentType) && !isIEBefore8) {
            contentDisposition = CONTENT_DISPOSITION_INLINE;
        }
        return new DownloadHeaderHelper(contentDisposition, fileName, mimeContentType).getDownloadHeaders();
    }

    public boolean isBinary(InputStream inputStream) {
        try {
            MediaType mediaType = this.textDetector.detect(inputStream, null);
            return mediaType.equals((Object)MediaType.OCTET_STREAM);
        }
        catch (IOException e) {
            return true;
        }
    }

    public String guessMIME(String filename, String mimeContentType, String userAgent) {
        if (CONTENT_DISPOSITION_INLINE.equals(this.guessContentDispositionHeader(filename, mimeContentType, userAgent)) && this.isAllowInlineOverride(filename, mimeContentType, userAgent, this.downloadPolicyProvider.getPolicy())) {
            return "text/plain";
        }
        return mimeContentType;
    }

    public String guessMIME(String mimeContentType, boolean isBinaryFile) {
        boolean inSmartMode = this.downloadPolicyProvider.getPolicy().equals((Object)DownloadPolicy.Smart);
        if (!inSmartMode || isBinaryFile) {
            return mimeContentType;
        }
        return "text/plain";
    }

    private boolean isAllowInlineOverride(String fileName, String mimeContentType, String userAgent, DownloadPolicy downloadPolicy) {
        return downloadPolicy == DownloadPolicy.Smart && !BrowserUtils.isIE(userAgent) && this.isTextContentType(mimeContentType) && !StringUtils.isBlank(mimeContentType);
    }

    private boolean isTextContentType(String mimeContentType) {
        return this.hostileExtensionDetector.isTextContentType(mimeContentType);
    }

    private boolean isExecutableContent(String name, String mimeContentType) {
        return this.hostileExtensionDetector.isExecutableContent(name, mimeContentType);
    }
}

