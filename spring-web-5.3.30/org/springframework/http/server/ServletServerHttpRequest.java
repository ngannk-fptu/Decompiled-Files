/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedCaseInsensitiveMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpAsyncRequestControl;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

public class ServletServerHttpRequest
implements ServerHttpRequest {
    protected static final Charset FORM_CHARSET = StandardCharsets.UTF_8;
    private final HttpServletRequest servletRequest;
    @Nullable
    private URI uri;
    @Nullable
    private HttpHeaders headers;
    @Nullable
    private ServerHttpAsyncRequestControl asyncRequestControl;

    public ServletServerHttpRequest(HttpServletRequest servletRequest) {
        Assert.notNull((Object)servletRequest, (String)"HttpServletRequest must not be null");
        this.servletRequest = servletRequest;
    }

    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }

    @Override
    @Nullable
    public HttpMethod getMethod() {
        return HttpMethod.resolve(this.servletRequest.getMethod());
    }

    @Override
    public String getMethodValue() {
        return this.servletRequest.getMethod();
    }

    @Override
    public URI getURI() {
        if (this.uri == null) {
            String urlString = null;
            boolean hasQuery = false;
            try {
                StringBuffer url = this.servletRequest.getRequestURL();
                String query = this.servletRequest.getQueryString();
                hasQuery = StringUtils.hasText((String)query);
                if (hasQuery) {
                    url.append('?').append(query);
                }
                urlString = url.toString();
                this.uri = new URI(urlString);
            }
            catch (URISyntaxException ex) {
                if (!hasQuery) {
                    throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex);
                }
                try {
                    urlString = this.servletRequest.getRequestURL().toString();
                    this.uri = new URI(urlString);
                }
                catch (URISyntaxException ex2) {
                    throw new IllegalStateException("Could not resolve HttpServletRequest as URI: " + urlString, ex2);
                }
            }
        }
        return this.uri;
    }

    @Override
    public HttpHeaders getHeaders() {
        if (this.headers == null) {
            int requestContentLength;
            this.headers = new HttpHeaders();
            Enumeration names = this.servletRequest.getHeaderNames();
            while (names.hasMoreElements()) {
                String headerName = (String)names.nextElement();
                Enumeration headerValues = this.servletRequest.getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    String headerValue = (String)headerValues.nextElement();
                    this.headers.add(headerName, headerValue);
                }
            }
            try {
                String requestEncoding;
                String requestContentType;
                MediaType contentType = this.headers.getContentType();
                if (contentType == null && StringUtils.hasLength((String)(requestContentType = this.servletRequest.getContentType())) && (contentType = MediaType.parseMediaType(requestContentType)).isConcrete()) {
                    this.headers.setContentType(contentType);
                }
                if (contentType != null && contentType.getCharset() == null && StringUtils.hasLength((String)(requestEncoding = this.servletRequest.getCharacterEncoding()))) {
                    Charset charSet = Charset.forName(requestEncoding);
                    LinkedCaseInsensitiveMap params = new LinkedCaseInsensitiveMap();
                    params.putAll(contentType.getParameters());
                    params.put("charset", charSet.toString());
                    MediaType mediaType = new MediaType(contentType.getType(), contentType.getSubtype(), (Map<String, String>)params);
                    this.headers.setContentType(mediaType);
                }
            }
            catch (InvalidMediaTypeException contentType) {
                // empty catch block
            }
            if (this.headers.getContentLength() < 0L && (requestContentLength = this.servletRequest.getContentLength()) != -1) {
                this.headers.setContentLength(requestContentLength);
            }
        }
        return this.headers;
    }

    @Override
    public Principal getPrincipal() {
        return this.servletRequest.getUserPrincipal();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return new InetSocketAddress(this.servletRequest.getLocalAddr(), this.servletRequest.getLocalPort());
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(this.servletRequest.getRemoteHost(), this.servletRequest.getRemotePort());
    }

    @Override
    public InputStream getBody() throws IOException {
        if (ServletServerHttpRequest.isFormPost(this.servletRequest)) {
            return ServletServerHttpRequest.getBodyFromServletRequestParameters(this.servletRequest);
        }
        return this.servletRequest.getInputStream();
    }

    @Override
    public ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse response) {
        if (this.asyncRequestControl == null) {
            if (!(response instanceof ServletServerHttpResponse)) {
                throw new IllegalArgumentException("Response must be a ServletServerHttpResponse: " + response.getClass());
            }
            ServletServerHttpResponse servletServerResponse = (ServletServerHttpResponse)response;
            this.asyncRequestControl = new ServletServerHttpAsyncRequestControl(this, servletServerResponse);
        }
        return this.asyncRequestControl;
    }

    private static boolean isFormPost(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains("application/x-www-form-urlencoded") && HttpMethod.POST.matches(request.getMethod());
    }

    private static InputStream getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)bos, FORM_CHARSET);
        Map form = request.getParameterMap();
        Iterator entryIterator = form.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry entry = entryIterator.next();
            String name = (String)entry.getKey();
            List<Object> values = Arrays.asList((Object[])entry.getValue());
            Iterator<Object> valueIterator = values.iterator();
            while (valueIterator.hasNext()) {
                String value = (String)valueIterator.next();
                writer.write(URLEncoder.encode(name, FORM_CHARSET.name()));
                if (value == null) continue;
                ((Writer)writer).write(61);
                writer.write(URLEncoder.encode(value, FORM_CHARSET.name()));
                if (!valueIterator.hasNext()) continue;
                ((Writer)writer).write(38);
            }
            if (!entryIterator.hasNext()) continue;
            writer.append('&');
        }
        ((Writer)writer).flush();
        return new ByteArrayInputStream(bos.toByteArray());
    }
}

