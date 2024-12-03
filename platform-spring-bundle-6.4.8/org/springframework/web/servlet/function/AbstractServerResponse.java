/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.ErrorHandlingServerResponse;
import org.springframework.web.servlet.function.ServerResponse;

abstract class AbstractServerResponse
extends ErrorHandlingServerResponse {
    private static final Set<HttpMethod> SAFE_METHODS = EnumSet.of(HttpMethod.GET, HttpMethod.HEAD);
    final int statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, Cookie> cookies;

    protected AbstractServerResponse(int statusCode, HttpHeaders headers, MultiValueMap<String, Cookie> cookies) {
        this.statusCode = statusCode;
        this.headers = HttpHeaders.readOnlyHttpHeaders(headers);
        this.cookies = CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap(cookies));
    }

    @Override
    public final HttpStatus statusCode() {
        return HttpStatus.valueOf(this.statusCode);
    }

    @Override
    public int rawStatusCode() {
        return this.statusCode;
    }

    @Override
    public final HttpHeaders headers() {
        return this.headers;
    }

    @Override
    public MultiValueMap<String, Cookie> cookies() {
        return this.cookies;
    }

    @Override
    public ModelAndView writeTo(HttpServletRequest request, HttpServletResponse response, ServerResponse.Context context) throws ServletException, IOException {
        try {
            this.writeStatusAndHeaders(response);
            long lastModified = this.headers().getLastModified();
            ServletWebRequest servletWebRequest = new ServletWebRequest(request, response);
            HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
            if (SAFE_METHODS.contains((Object)httpMethod) && servletWebRequest.checkNotModified(this.headers().getETag(), lastModified)) {
                return null;
            }
            return this.writeToInternal(request, response, context);
        }
        catch (Throwable throwable) {
            return this.handleError(throwable, request, response, context);
        }
    }

    private void writeStatusAndHeaders(HttpServletResponse response) {
        response.setStatus(this.statusCode);
        this.writeHeaders(response);
        this.writeCookies(response);
    }

    private void writeHeaders(HttpServletResponse servletResponse) {
        this.headers.forEach((headerName, headerValues) -> {
            for (String headerValue : headerValues) {
                servletResponse.addHeader(headerName, headerValue);
            }
        });
        if (servletResponse.getContentType() == null && this.headers.getContentType() != null) {
            servletResponse.setContentType(this.headers.getContentType().toString());
        }
        if (servletResponse.getCharacterEncoding() == null && this.headers.getContentType() != null && this.headers.getContentType().getCharset() != null) {
            servletResponse.setCharacterEncoding(this.headers.getContentType().getCharset().name());
        }
    }

    private void writeCookies(HttpServletResponse servletResponse) {
        this.cookies.values().stream().flatMap(Collection::stream).forEach(arg_0 -> ((HttpServletResponse)servletResponse).addCookie(arg_0));
    }

    @Nullable
    protected abstract ModelAndView writeToInternal(HttpServletRequest var1, HttpServletResponse var2, ServerResponse.Context var3) throws ServletException, IOException;
}

