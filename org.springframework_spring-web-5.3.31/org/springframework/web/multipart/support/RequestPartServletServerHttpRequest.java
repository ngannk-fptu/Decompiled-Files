/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.multipart.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public class RequestPartServletServerHttpRequest
extends ServletServerHttpRequest {
    private final MultipartHttpServletRequest multipartRequest;
    private final String requestPartName;
    private final HttpHeaders multipartHeaders;

    public RequestPartServletServerHttpRequest(HttpServletRequest request, String requestPartName) throws MissingServletRequestPartException {
        super(request);
        this.multipartRequest = MultipartResolutionDelegate.asMultipartHttpServletRequest(request);
        this.requestPartName = requestPartName;
        HttpHeaders multipartHeaders = this.multipartRequest.getMultipartHeaders(requestPartName);
        if (multipartHeaders == null) {
            throw new MissingServletRequestPartException(requestPartName);
        }
        this.multipartHeaders = multipartHeaders;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.multipartHeaders;
    }

    @Override
    public InputStream getBody() throws IOException {
        Part part;
        Part part2;
        boolean servletParts = this.multipartRequest instanceof StandardMultipartHttpServletRequest;
        if (servletParts && (part2 = this.retrieveServletPart()) != null) {
            return part2.getInputStream();
        }
        MultipartFile file = this.multipartRequest.getFile(this.requestPartName);
        if (file != null) {
            return file.getInputStream();
        }
        String paramValue = this.multipartRequest.getParameter(this.requestPartName);
        if (paramValue != null) {
            return new ByteArrayInputStream(paramValue.getBytes(this.determineCharset()));
        }
        if (!servletParts && (part = this.retrieveServletPart()) != null) {
            return part.getInputStream();
        }
        throw new IllegalStateException("No body available for request part '" + this.requestPartName + "'");
    }

    @Nullable
    private Part retrieveServletPart() {
        try {
            return this.multipartRequest.getPart(this.requestPartName);
        }
        catch (Exception ex) {
            throw new MultipartException("Failed to retrieve request part '" + this.requestPartName + "'", ex);
        }
    }

    private Charset determineCharset() {
        Charset charset;
        MediaType contentType = this.getHeaders().getContentType();
        if (contentType != null && (charset = contentType.getCharset()) != null) {
            return charset;
        }
        String encoding = this.multipartRequest.getCharacterEncoding();
        return encoding != null ? Charset.forName(encoding) : FORM_CHARSET;
    }
}

