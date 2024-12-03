/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.context.request;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

public class ServletWebRequest
extends ServletRequestAttributes
implements NativeWebRequest {
    private static final List<String> SAFE_METHODS = Arrays.asList("GET", "HEAD");
    private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
    private static final String[] DATE_FORMATS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy"};
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private boolean notModified = false;

    public ServletWebRequest(HttpServletRequest request) {
        super(request);
    }

    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
    }

    @Override
    public Object getNativeRequest() {
        return this.getRequest();
    }

    @Override
    public Object getNativeResponse() {
        return this.getResponse();
    }

    @Override
    public <T> T getNativeRequest(@Nullable Class<T> requiredType) {
        return WebUtils.getNativeRequest((ServletRequest)this.getRequest(), requiredType);
    }

    @Override
    public <T> T getNativeResponse(@Nullable Class<T> requiredType) {
        HttpServletResponse response = this.getResponse();
        return response != null ? (T)WebUtils.getNativeResponse((ServletResponse)response, requiredType) : null;
    }

    @Nullable
    public HttpMethod getHttpMethod() {
        return HttpMethod.resolve(this.getRequest().getMethod());
    }

    @Override
    @Nullable
    public String getHeader(String headerName) {
        return this.getRequest().getHeader(headerName);
    }

    @Override
    @Nullable
    public String[] getHeaderValues(String headerName) {
        Object[] headerValues = StringUtils.toStringArray(this.getRequest().getHeaders(headerName));
        return !ObjectUtils.isEmpty(headerValues) ? headerValues : null;
    }

    @Override
    public Iterator<String> getHeaderNames() {
        return CollectionUtils.toIterator(this.getRequest().getHeaderNames());
    }

    @Override
    @Nullable
    public String getParameter(String paramName) {
        return this.getRequest().getParameter(paramName);
    }

    @Override
    @Nullable
    public String[] getParameterValues(String paramName) {
        return this.getRequest().getParameterValues(paramName);
    }

    @Override
    public Iterator<String> getParameterNames() {
        return CollectionUtils.toIterator(this.getRequest().getParameterNames());
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.getRequest().getParameterMap();
    }

    @Override
    public Locale getLocale() {
        return this.getRequest().getLocale();
    }

    @Override
    public String getContextPath() {
        return this.getRequest().getContextPath();
    }

    @Override
    @Nullable
    public String getRemoteUser() {
        return this.getRequest().getRemoteUser();
    }

    @Override
    @Nullable
    public Principal getUserPrincipal() {
        return this.getRequest().getUserPrincipal();
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.getRequest().isUserInRole(role);
    }

    @Override
    public boolean isSecure() {
        return this.getRequest().isSecure();
    }

    @Override
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return this.checkNotModified(null, lastModifiedTimestamp);
    }

    @Override
    public boolean checkNotModified(String etag) {
        return this.checkNotModified(etag, -1L);
    }

    @Override
    public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
        HttpServletResponse response = this.getResponse();
        if (this.notModified || response != null && HttpStatus.OK.value() != response.getStatus()) {
            return this.notModified;
        }
        if (this.validateIfUnmodifiedSince(lastModifiedTimestamp)) {
            if (this.notModified && response != null) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            }
            if (SAFE_METHODS.contains(this.getRequest().getMethod())) {
                if (StringUtils.hasLength(etag) && response.getHeader("ETag") == null) {
                    response.setHeader("ETag", this.padEtagIfNecessary(etag));
                }
                response.setDateHeader("Last-Modified", lastModifiedTimestamp);
            }
            return this.notModified;
        }
        boolean validated = this.validateIfNoneMatch(etag);
        if (!validated) {
            this.validateIfModifiedSince(lastModifiedTimestamp);
        }
        if (response != null) {
            boolean isHttpGetOrHead = SAFE_METHODS.contains(this.getRequest().getMethod());
            if (this.notModified) {
                response.setStatus(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED.value() : HttpStatus.PRECONDITION_FAILED.value());
            }
            if (isHttpGetOrHead) {
                if (lastModifiedTimestamp > 0L && this.parseDateValue(response.getHeader("Last-Modified")) == -1L) {
                    response.setDateHeader("Last-Modified", lastModifiedTimestamp);
                }
                if (StringUtils.hasLength(etag) && response.getHeader("ETag") == null) {
                    response.setHeader("ETag", this.padEtagIfNecessary(etag));
                }
            }
        }
        return this.notModified;
    }

    private boolean validateIfUnmodifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0L) {
            return false;
        }
        long ifUnmodifiedSince = this.parseDateHeader("If-Unmodified-Since");
        if (ifUnmodifiedSince == -1L) {
            return false;
        }
        this.notModified = ifUnmodifiedSince < lastModifiedTimestamp / 1000L * 1000L;
        return true;
    }

    private boolean validateIfNoneMatch(@Nullable String etag) {
        Enumeration ifNoneMatch;
        if (!StringUtils.hasLength(etag)) {
            return false;
        }
        try {
            ifNoneMatch = this.getRequest().getHeaders("If-None-Match");
        }
        catch (IllegalArgumentException ex) {
            return false;
        }
        if (!ifNoneMatch.hasMoreElements()) {
            return false;
        }
        if ((etag = this.padEtagIfNecessary(etag)).startsWith("W/")) {
            etag = etag.substring(2);
        }
        block2: while (ifNoneMatch.hasMoreElements()) {
            String clientETags = (String)ifNoneMatch.nextElement();
            Matcher etagMatcher = ETAG_HEADER_VALUE_PATTERN.matcher(clientETags);
            while (etagMatcher.find()) {
                if (!StringUtils.hasLength(etagMatcher.group()) || !etag.equals(etagMatcher.group(3))) continue;
                this.notModified = true;
                continue block2;
            }
        }
        return true;
    }

    private String padEtagIfNecessary(String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean validateIfModifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0L) {
            return false;
        }
        long ifModifiedSince = this.parseDateHeader("If-Modified-Since");
        if (ifModifiedSince == -1L) {
            return false;
        }
        this.notModified = ifModifiedSince >= lastModifiedTimestamp / 1000L * 1000L;
        return true;
    }

    public boolean isNotModified() {
        return this.notModified;
    }

    private long parseDateHeader(String headerName) {
        long dateValue;
        block2: {
            dateValue = -1L;
            try {
                dateValue = this.getRequest().getDateHeader(headerName);
            }
            catch (IllegalArgumentException ex) {
                int separatorIndex;
                String headerValue = this.getHeader(headerName);
                if (headerValue == null || (separatorIndex = headerValue.indexOf(59)) == -1) break block2;
                String datePart = headerValue.substring(0, separatorIndex);
                dateValue = this.parseDateValue(datePart);
            }
        }
        return dateValue;
    }

    private long parseDateValue(@Nullable String headerValue) {
        if (headerValue == null) {
            return -1L;
        }
        if (headerValue.length() >= 3) {
            for (String dateFormat : DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                simpleDateFormat.setTimeZone(GMT);
                try {
                    return simpleDateFormat.parse(headerValue).getTime();
                }
                catch (ParseException parseException) {
                }
            }
        }
        return -1L;
    }

    @Override
    public String getDescription(boolean includeClientInfo) {
        HttpServletRequest request = this.getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("uri=").append(request.getRequestURI());
        if (includeClientInfo) {
            String user;
            HttpSession session;
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                sb.append(";client=").append(client);
            }
            if ((session = request.getSession(false)) != null) {
                sb.append(";session=").append(session.getId());
            }
            if (StringUtils.hasLength(user = request.getRemoteUser())) {
                sb.append(";user=").append(user);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ServletWebRequest: " + this.getDescription(true);
    }
}

