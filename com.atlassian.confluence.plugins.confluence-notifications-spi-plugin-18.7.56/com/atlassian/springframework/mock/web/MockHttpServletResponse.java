/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedCaseInsensitiveMap
 */
package com.atlassian.springframework.mock.web;

import com.atlassian.springframework.mock.web.DelegatingServletOutputStream;
import com.atlassian.springframework.mock.web.HeaderValueHolder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class MockHttpServletResponse
implements HttpServletResponse {
    private static final String CHARSET_PREFIX = "charset=";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private final ByteArrayOutputStream content = new ByteArrayOutputStream(1024);
    private final ServletOutputStream outputStream = new ResponseServletOutputStream(this.content);
    private final List<Cookie> cookies = new ArrayList<Cookie>();
    private final Map<String, HeaderValueHolder> headers = new LinkedCaseInsensitiveMap();
    private final List<String> includedUrls = new ArrayList<String>();
    private boolean outputStreamAccessAllowed = true;
    private boolean writerAccessAllowed = true;
    private String characterEncoding = "ISO-8859-1";
    private boolean charset = false;
    private PrintWriter writer;
    private long contentLength = 0L;
    private String contentType;
    private int bufferSize = 4096;
    private boolean committed;
    private Locale locale = Locale.getDefault();
    private int status = 200;
    private String errorMessage;
    private String forwardedUrl;

    public boolean isOutputStreamAccessAllowed() {
        return this.outputStreamAccessAllowed;
    }

    public void setOutputStreamAccessAllowed(boolean outputStreamAccessAllowed) {
        this.outputStreamAccessAllowed = outputStreamAccessAllowed;
    }

    public boolean isWriterAccessAllowed() {
        return this.writerAccessAllowed;
    }

    public void setWriterAccessAllowed(boolean writerAccessAllowed) {
        this.writerAccessAllowed = writerAccessAllowed;
    }

    private void updateContentTypeHeader() {
        if (this.contentType != null) {
            StringBuilder sb = new StringBuilder(this.contentType);
            if (!this.contentType.toLowerCase().contains(CHARSET_PREFIX) && this.charset) {
                sb.append(";").append(CHARSET_PREFIX).append(this.characterEncoding);
            }
            this.doAddHeaderValue(CONTENT_TYPE_HEADER, sb.toString(), true);
        }
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
        this.charset = true;
        this.updateContentTypeHeader();
    }

    public ServletOutputStream getOutputStream() {
        if (!this.outputStreamAccessAllowed) {
            throw new IllegalStateException("OutputStream access not allowed");
        }
        return this.outputStream;
    }

    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (!this.writerAccessAllowed) {
            throw new IllegalStateException("Writer access not allowed");
        }
        if (this.writer == null) {
            OutputStreamWriter targetWriter = this.characterEncoding != null ? new OutputStreamWriter((OutputStream)this.content, this.characterEncoding) : new OutputStreamWriter(this.content);
            this.writer = new ResponsePrintWriter(targetWriter);
        }
        return this.writer;
    }

    public byte[] getContentAsByteArray() {
        this.flushBuffer();
        return this.content.toByteArray();
    }

    public String getContentAsString() throws UnsupportedEncodingException {
        this.flushBuffer();
        return this.characterEncoding != null ? this.content.toString(this.characterEncoding) : this.content.toString();
    }

    public int getContentLength() {
        return (int)this.contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
        this.doAddHeaderValue(CONTENT_LENGTH_HEADER, contentLength, true);
    }

    public long getContentLengthLong() {
        return this.contentLength;
    }

    public void setContentLengthLong(long contentLength) {
        this.contentLength = contentLength;
        this.doAddHeaderValue(CONTENT_LENGTH_HEADER, contentLength, true);
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (contentType != null) {
            int charsetIndex = contentType.toLowerCase().indexOf(CHARSET_PREFIX);
            if (charsetIndex != -1) {
                this.characterEncoding = contentType.substring(charsetIndex + CHARSET_PREFIX.length());
                this.charset = true;
            }
            this.updateContentTypeHeader();
        }
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void flushBuffer() {
        this.setCommitted(true);
    }

    public void resetBuffer() {
        if (this.isCommitted()) {
            throw new IllegalStateException("Cannot reset buffer - response is already committed");
        }
        this.content.reset();
    }

    private void setCommittedIfBufferSizeExceeded() {
        int bufSize = this.getBufferSize();
        if (bufSize > 0 && this.content.size() > bufSize) {
            this.setCommitted(true);
        }
    }

    public boolean isCommitted() {
        return this.committed;
    }

    public void setCommitted(boolean committed) {
        this.committed = committed;
    }

    public void reset() {
        this.resetBuffer();
        this.characterEncoding = null;
        this.contentLength = 0L;
        this.contentType = null;
        this.locale = null;
        this.cookies.clear();
        this.headers.clear();
        this.status = 200;
        this.errorMessage = null;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void addCookie(Cookie cookie) {
        Assert.notNull((Object)cookie, (String)"Cookie must not be null");
        this.cookies.add(cookie);
    }

    public Cookie[] getCookies() {
        return this.cookies.toArray(new Cookie[this.cookies.size()]);
    }

    public Cookie getCookie(String name) {
        Assert.notNull((Object)name, (String)"Cookie name must not be null");
        for (Cookie cookie : this.cookies) {
            if (!name.equals(cookie.getName())) continue;
            return cookie;
        }
        return null;
    }

    public boolean containsHeader(String name) {
        return HeaderValueHolder.getByName(this.headers, name) != null;
    }

    public Collection<String> getHeaderNames() {
        return this.headers.keySet();
    }

    public String getHeader(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return header != null ? header.getStringValue() : null;
    }

    public List<String> getHeaders(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        if (header != null) {
            return header.getStringValues();
        }
        return Collections.emptyList();
    }

    public Object getHeaderValue(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        return header != null ? header.getValue() : null;
    }

    public List<Object> getHeaderValues(String name) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        if (header != null) {
            return header.getValues();
        }
        return Collections.emptyList();
    }

    public String encodeURL(String url) {
        return url;
    }

    public String encodeRedirectURL(String url) {
        return this.encodeURL(url);
    }

    @Deprecated
    public String encodeUrl(String url) {
        return this.encodeURL(url);
    }

    @Deprecated
    public String encodeRedirectUrl(String url) {
        return this.encodeRedirectURL(url);
    }

    public void sendError(int status, String errorMessage) throws IOException {
        if (this.isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        this.status = status;
        this.errorMessage = errorMessage;
        this.setCommitted(true);
    }

    public void sendError(int status) throws IOException {
        if (this.isCommitted()) {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }
        this.status = status;
        this.setCommitted(true);
    }

    public void sendRedirect(String url) throws IOException {
        if (this.isCommitted()) {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }
        Assert.notNull((Object)url, (String)"Redirect URL must not be null");
        this.setHeader(LOCATION_HEADER, url);
        this.setStatus(302);
        this.setCommitted(true);
    }

    public String getRedirectedUrl() {
        return this.getHeader(LOCATION_HEADER);
    }

    public void setDateHeader(String name, long value) {
        this.setHeaderValue(name, value);
    }

    public void addDateHeader(String name, long value) {
        this.addHeaderValue(name, value);
    }

    public void setHeader(String name, String value) {
        this.setHeaderValue(name, value);
    }

    public void addHeader(String name, String value) {
        this.addHeaderValue(name, value);
    }

    public void setIntHeader(String name, int value) {
        this.setHeaderValue(name, value);
    }

    public void addIntHeader(String name, int value) {
        this.addHeaderValue(name, value);
    }

    private void setHeaderValue(String name, Object value) {
        if (this.setSpecialHeader(name, value)) {
            return;
        }
        this.doAddHeaderValue(name, value, true);
    }

    private void addHeaderValue(String name, Object value) {
        if (this.setSpecialHeader(name, value)) {
            return;
        }
        this.doAddHeaderValue(name, value, false);
    }

    private boolean setSpecialHeader(String name, Object value) {
        if (CONTENT_TYPE_HEADER.equalsIgnoreCase(name)) {
            this.setContentType((String)value);
            return true;
        }
        if (CONTENT_LENGTH_HEADER.equalsIgnoreCase(name)) {
            this.setContentLength(Integer.parseInt((String)value));
            return true;
        }
        return false;
    }

    private void doAddHeaderValue(String name, Object value, boolean replace) {
        HeaderValueHolder header = HeaderValueHolder.getByName(this.headers, name);
        Assert.notNull((Object)value, (String)"Header value must not be null");
        if (header == null) {
            header = new HeaderValueHolder();
            this.headers.put(name, header);
        }
        if (replace) {
            header.setValue(value);
        } else {
            header.addValue(value);
        }
    }

    @Deprecated
    public void setStatus(int status, String errorMessage) {
        if (!this.isCommitted()) {
            this.status = status;
            this.errorMessage = errorMessage;
        }
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        if (!this.isCommitted()) {
            this.status = status;
        }
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getForwardedUrl() {
        return this.forwardedUrl;
    }

    public void setForwardedUrl(String forwardedUrl) {
        this.forwardedUrl = forwardedUrl;
    }

    public String getIncludedUrl() {
        int count = this.includedUrls.size();
        if (count > 1) {
            throw new IllegalStateException("More than 1 URL included - check getIncludedUrls instead: " + this.includedUrls);
        }
        return count == 1 ? this.includedUrls.get(0) : null;
    }

    public void setIncludedUrl(String includedUrl) {
        this.includedUrls.clear();
        if (includedUrl != null) {
            this.includedUrls.add(includedUrl);
        }
    }

    public void addIncludedUrl(String includedUrl) {
        Assert.notNull((Object)includedUrl, (String)"Included URL must not be null");
        this.includedUrls.add(includedUrl);
    }

    public List<String> getIncludedUrls() {
        return this.includedUrls;
    }

    private class ResponsePrintWriter
    extends PrintWriter {
        public ResponsePrintWriter(Writer out) {
            super(out, true);
        }

        @Override
        public void write(char[] buf, int off, int len) {
            super.write(buf, off, len);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void write(String s, int off, int len) {
            super.write(s, off, len);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void write(int c) {
            super.write(c);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void flush() {
            super.flush();
            MockHttpServletResponse.this.setCommitted(true);
        }
    }

    private class ResponseServletOutputStream
    extends DelegatingServletOutputStream {
        public ResponseServletOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void write(int b) throws IOException {
            super.write(b);
            super.flush();
            MockHttpServletResponse.this.setCommittedIfBufferSizeExceeded();
        }

        @Override
        public void flush() throws IOException {
            super.flush();
            MockHttpServletResponse.this.setCommitted(true);
        }
    }
}

