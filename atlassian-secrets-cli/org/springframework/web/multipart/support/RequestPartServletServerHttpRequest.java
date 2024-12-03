/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.multipart.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

public class RequestPartServletServerHttpRequest
extends ServletServerHttpRequest {
    private final MultipartHttpServletRequest multipartRequest;
    private final String partName;
    private final HttpHeaders headers;

    public RequestPartServletServerHttpRequest(HttpServletRequest request, String partName) throws MissingServletRequestPartException {
        super(request);
        this.multipartRequest = MultipartResolutionDelegate.asMultipartHttpServletRequest(request);
        this.partName = partName;
        HttpHeaders headers = this.multipartRequest.getMultipartHeaders(this.partName);
        if (headers == null) {
            throw new MissingServletRequestPartException(partName);
        }
        this.headers = headers;
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public InputStream getBody() throws IOException {
        if (this.multipartRequest instanceof StandardMultipartHttpServletRequest) {
            try {
                return this.multipartRequest.getPart(this.partName).getInputStream();
            }
            catch (Exception ex) {
                throw new MultipartException("Could not parse multipart servlet request", ex);
            }
        }
        MultipartFile file = this.multipartRequest.getFile(this.partName);
        if (file != null) {
            return file.getInputStream();
        }
        String paramValue = this.multipartRequest.getParameter(this.partName);
        return new ByteArrayInputStream(paramValue.getBytes(this.determineCharset()));
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

