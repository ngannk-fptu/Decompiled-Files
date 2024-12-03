/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.servlet.util.LastModifiedHandler
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  com.google.common.base.Stopwatch
 *  com.google.common.io.CountingOutputStream
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.event.events.content.attachment.ProfilePictureViewEvent;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceManager;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.servlet.download.AttachmentUrlParser;
import com.atlassian.confluence.servlet.download.DownloadStreamingAnalyticsEvent;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.servlet.download.ServeAfterTransactionDownload;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.servlet.util.LastModifiedHandler;
import com.atlassian.xwork.XsrfTokenGenerator;
import com.google.common.base.Stopwatch;
import com.google.common.io.CountingOutputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentDownload
extends ServeAfterTransactionDownload {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDownload.class);
    public static final String JWT_REQUEST_USERNAME = "jwt_request_username";
    private DownloadResourceManager downloadResourceManager;
    private AttachmentUrlParser attachmentUrlParser;
    private EventPublisher eventPublisher;
    private XsrfTokenGenerator tokenGenerator;
    private SafeContentHeaderGuesser guesser;

    public boolean matches(String urlPath) {
        return urlPath.contains("download/" + this.getUrlPrefix()) || urlPath.contains("download/token-auth/attachments");
    }

    public void setDownloadResourceManager(DownloadResourceManager downloadResourceManager) {
        this.downloadResourceManager = downloadResourceManager;
    }

    public void setAttachmentUrlParser(AttachmentUrlParser attachmentUrlParser) {
        this.attachmentUrlParser = attachmentUrlParser;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void setTokenGenerator(XsrfTokenGenerator tokenGenerator) {
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    @Nullable
    public InputStream getStreamForDownload(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        DownloadResourceReader resourceReader = this.getResourceReader(httpServletRequest, httpServletResponse);
        if (resourceReader == null) {
            return null;
        }
        if (LastModifiedHandler.checkRequest((HttpServletRequest)httpServletRequest, (HttpServletResponse)httpServletResponse, (Date)resourceReader.getLastModificationDate())) {
            return null;
        }
        InputStream stream = this.getInputStream(resourceReader);
        if (stream == null) {
            this.sendAttachmentStreamNotFoundResponse(httpServletRequest, httpServletResponse);
            return null;
        }
        this.sendResponseHeaders(httpServletRequest, httpServletResponse, resourceReader, stream);
        this.publishDownloadEvent(httpServletRequest);
        return stream;
    }

    private DownloadResourceReader getResourceReader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        try {
            String range = httpServletRequest.getHeader("Range");
            String decodedPath = AttachmentDownload.getDecodedPath(httpServletRequest);
            if (range != null) {
                PartialDownloadResourceManager partialDownloadManager = (PartialDownloadResourceManager)this.downloadResourceManager;
                PartialDownloadResourceReader partialReader = partialDownloadManager.getPartialResourceReader(httpServletRequest.getRemoteUser(), decodedPath, httpServletRequest.getParameterMap(), range);
                RangeRequest servingRange = partialReader.getRequestRange();
                httpServletResponse.setStatus(206);
                httpServletResponse.setHeader("Content-Range", String.format("bytes %d-%d/%d", servingRange.getOffset(), servingRange.getEnd(), servingRange.getContentLength()));
                return partialReader;
            }
            Object JWTUser = httpServletRequest.getAttribute(JWT_REQUEST_USERNAME);
            String username = JWTUser != null ? (String)JWTUser : httpServletRequest.getRemoteUser();
            return this.downloadResourceManager.getResourceReader(username, decodedPath, httpServletRequest.getParameterMap());
        }
        catch (UnauthorizedDownloadResourceException e) {
            this.sendUnauthorizedDownloadResponse(httpServletRequest, httpServletResponse);
            return null;
        }
        catch (DownloadResourceNotFoundException e) {
            httpServletResponse.sendError(404);
            return null;
        }
        catch (RangeNotSatisfiableException e) {
            log.error("User requested range is not satisfiable", (Throwable)e);
            httpServletResponse.setStatus(416);
            return null;
        }
    }

    private void sendResponseHeaders(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DownloadResourceReader resourceReader, InputStream stream) throws IOException {
        httpServletResponse.setHeader("Accept-Ranges", "bytes");
        this.setHeadersForAttachment(stream, resourceReader.getName(), AttachmentDownload.getContentLength(resourceReader, httpServletRequest), resourceReader.getContentType(), httpServletRequest, httpServletResponse);
    }

    private void publishDownloadEvent(HttpServletRequest httpServletRequest) {
        Attachment attachment = this.attachmentUrlParser.getAttachment(AttachmentDownload.getDecodedPath(httpServletRequest), this.getUrlPrefix(), httpServletRequest.getParameterMap());
        boolean isDownload = httpServletRequest.getParameterMap().containsKey("download");
        this.publishEvents(attachment, isDownload);
    }

    private static long getContentLength(DownloadResourceReader resourceReader, HttpServletRequest httpServletRequest) {
        String rangeHeader = httpServletRequest.getHeader("Range");
        return rangeHeader != null ? ((PartialDownloadResourceReader)resourceReader).getRequestRange().getRangeLength() : resourceReader.getContentLength();
    }

    @Nullable
    private InputStream getInputStream(DownloadResourceReader resourceReader) {
        InputStream stream = resourceReader.getStreamForReading();
        if (stream != null && !stream.markSupported()) {
            return new BufferedInputStream(stream);
        }
        return stream;
    }

    private void sendAttachmentStreamNotFoundResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        String decodedPath = AttachmentDownload.getDecodedPath(httpServletRequest);
        long entityId = this.attachmentUrlParser.getEntityId(decodedPath, this.getUrlPrefix());
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/attachmentnotfound.action?pageId=" + entityId);
    }

    private void sendUnauthorizedDownloadResponse(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        if (httpServletRequest.getRemoteUser() == null) {
            httpServletRequest.setAttribute("atlassian.core.seraph.original.url", (Object)GeneralUtil.getOriginalUrl(httpServletRequest));
            httpServletResponse.sendRedirect(SeraphUtils.getLoginURL(httpServletRequest));
        } else {
            httpServletResponse.sendError(404);
        }
    }

    private static String getDecodedPath(HttpServletRequest httpServletRequest) {
        String apiRevision = httpServletRequest.getParameter("api");
        String path = httpServletRequest.getRequestURI();
        if (apiRevision != null && apiRevision.equals("v2")) {
            path = path.replaceAll("\\+", "%2B");
        }
        return HtmlUtil.urlDecode(path);
    }

    protected void publishEvents(Attachment attachment, boolean download) {
        if (attachment.isUserProfilePicture()) {
            this.eventPublisher.publish((Object)new ProfilePictureViewEvent((Object)this, attachment));
        } else {
            this.eventPublisher.publish((Object)new AttachmentViewEvent((Object)this, attachment, download));
        }
    }

    protected String getUrlPrefix() {
        return "attachments";
    }

    protected void setHeadersForAttachment(InputStream contents, String name, long contentLength, String contentType, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
        Map<String, String> headers = this.getHeadersForAttachment(contents, name, contentLength, contentType, httpServletRequest);
        this.setHeaders(httpServletResponse, headers);
    }

    private void setHeaders(HttpServletResponse httpServletResponse, Map<String, String> headers) {
        httpServletResponse.setContentType(headers.get("Content-Type"));
        headers.forEach((key, value) -> {
            if (!"Content-Type".equals(key)) {
                httpServletResponse.setHeader(key, value);
            }
        });
    }

    private Map<String, String> getHeadersForAttachment(InputStream contents, String name, long contentLength, String contentType, HttpServletRequest httpServletRequest) throws IOException {
        String userAgent = httpServletRequest.getHeader("User-Agent");
        return this.guesser.computeAttachmentHeaders(contentType, contents, name, userAgent, contentLength, this.hasValidXsrfToken(httpServletRequest), httpServletRequest.getParameterMap());
    }

    private boolean hasValidXsrfToken(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getParameter(this.tokenGenerator.getXsrfTokenName());
        return this.tokenGenerator.validateToken(httpServletRequest, token);
    }

    public void setAttachmentSafeContentHeaderGuesser(SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser) {
        this.guesser = attachmentSafeContentHeaderGuesser;
    }

    @Override
    protected void streamResponse(InputStream fromStream, OutputStream toStream) throws IOException {
        CountingOutputStream countingOutput = new CountingOutputStream(toStream);
        Stopwatch stopwatch = Stopwatch.createStarted();
        super.streamResponse(fromStream, (OutputStream)countingOutput);
        this.getStreamingAnalyticsEventName().map(eventName -> new DownloadStreamingAnalyticsEvent((String)eventName, countingOutput.getCount(), stopwatch.elapsed())).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    protected Optional<String> getStreamingAnalyticsEventName() {
        return Optional.of("confluence.attachment.download.stream");
    }
}

