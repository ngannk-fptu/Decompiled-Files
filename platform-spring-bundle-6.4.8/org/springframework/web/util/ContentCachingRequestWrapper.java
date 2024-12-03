/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 */
package org.springframework.web.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

public class ContentCachingRequestWrapper
extends HttpServletRequestWrapper {
    private final ByteArrayOutputStream cachedContent;
    @Nullable
    private final Integer contentCacheLimit;
    @Nullable
    private ServletInputStream inputStream;
    @Nullable
    private BufferedReader reader;

    public ContentCachingRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
        this.contentCacheLimit = null;
    }

    public ContentCachingRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
        this.contentCacheLimit = contentCacheLimit;
    }

    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(this.getRequest().getInputStream());
        }
        return this.inputStream;
    }

    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return enc != null ? enc : "ISO-8859-1";
    }

    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader((InputStream)this.getInputStream(), this.getCharacterEncoding()));
        }
        return this.reader;
    }

    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && this.isFormPost()) {
            this.writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && this.isFormPost()) {
            this.writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && this.isFormPost()) {
            this.writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && this.isFormPost()) {
            this.writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = this.getContentType();
        return contentType != null && contentType.contains("application/x-www-form-urlencoded") && HttpMethod.POST.matches(this.getMethod());
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = this.getCharacterEncoding();
                Map form = super.getParameterMap();
                Iterator nameIterator = form.keySet().iterator();
                while (nameIterator.hasNext()) {
                    String name = (String)nameIterator.next();
                    List<Object> values = Arrays.asList((Object[])form.get(name));
                    Iterator<Object> valueIterator = values.iterator();
                    while (valueIterator.hasNext()) {
                        String value = (String)valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value == null) continue;
                        this.cachedContent.write(61);
                        this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                        if (!valueIterator.hasNext()) continue;
                        this.cachedContent.write(38);
                    }
                    if (!nameIterator.hasNext()) continue;
                    this.cachedContent.write(38);
                }
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    public byte[] getContentAsByteArray() {
        return this.cachedContent.toByteArray();
    }

    protected void handleContentOverflow(int contentCacheLimit) {
    }

    private class ContentCachingInputStream
    extends ServletInputStream {
        private final ServletInputStream is;
        private boolean overflow = false;

        public ContentCachingInputStream(ServletInputStream is) {
            this.is = is;
        }

        public int read() throws IOException {
            int ch = this.is.read();
            if (ch != -1 && !this.overflow) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit != null && ContentCachingRequestWrapper.this.cachedContent.size() == ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
                    this.overflow = true;
                    ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit);
                } else {
                    ContentCachingRequestWrapper.this.cachedContent.write(ch);
                }
            }
            return ch;
        }

        public int read(byte[] b) throws IOException {
            int count = this.is.read(b);
            this.writeToCache(b, 0, count);
            return count;
        }

        private void writeToCache(byte[] b, int off, int count) {
            if (!this.overflow && count > 0) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit != null && count + ContentCachingRequestWrapper.this.cachedContent.size() > ContentCachingRequestWrapper.this.contentCacheLimit) {
                    this.overflow = true;
                    ContentCachingRequestWrapper.this.cachedContent.write(b, off, ContentCachingRequestWrapper.this.contentCacheLimit - ContentCachingRequestWrapper.this.cachedContent.size());
                    ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit);
                    return;
                }
                ContentCachingRequestWrapper.this.cachedContent.write(b, off, count);
            }
        }

        public int read(byte[] b, int off, int len) throws IOException {
            int count = this.is.read(b, off, len);
            this.writeToCache(b, off, count);
            return count;
        }

        public int readLine(byte[] b, int off, int len) throws IOException {
            int count = this.is.readLine(b, off, len);
            this.writeToCache(b, off, count);
            return count;
        }

        public boolean isFinished() {
            return this.is.isFinished();
        }

        public boolean isReady() {
            return this.is.isReady();
        }

        public void setReadListener(ReadListener readListener) {
            this.is.setReadListener(readListener);
        }
    }
}

