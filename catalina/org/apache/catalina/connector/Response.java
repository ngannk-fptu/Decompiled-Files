/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.Constants
 *  org.apache.coyote.ContinueResponseTiming
 *  org.apache.coyote.Response
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.buf.CharChunk
 *  org.apache.tomcat.util.buf.UEncoder
 *  org.apache.tomcat.util.buf.UEncoder$SafeCharsSet
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.http.MimeHeaders
 *  org.apache.tomcat.util.http.parser.MediaTypeCache
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.connector.CoyoteOutputStream;
import org.apache.catalina.connector.CoyoteWriter;
import org.apache.catalina.connector.OutputBuffer;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.security.SecurityUtil;
import org.apache.catalina.util.SessionConfig;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Constants;
import org.apache.coyote.ContinueResponseTiming;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.buf.CharChunk;
import org.apache.tomcat.util.buf.UEncoder;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.parser.MediaTypeCache;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;

public class Response
implements HttpServletResponse {
    private static final Log log = LogFactory.getLog(Response.class);
    protected static final StringManager sm = StringManager.getManager(Response.class);
    private static final MediaTypeCache MEDIA_TYPE_CACHE = new MediaTypeCache(100);
    private static final boolean ENFORCE_ENCODING_IN_GET_WRITER = Boolean.parseBoolean(System.getProperty("org.apache.catalina.connector.Response.ENFORCE_ENCODING_IN_GET_WRITER", "true"));
    @Deprecated
    protected SimpleDateFormat format = null;
    protected org.apache.coyote.Response coyoteResponse;
    protected final OutputBuffer outputBuffer;
    protected CoyoteOutputStream outputStream;
    protected CoyoteWriter writer;
    protected boolean appCommitted = false;
    protected boolean included = false;
    private boolean isCharacterEncodingSet = false;
    protected boolean usingOutputStream = false;
    protected boolean usingWriter = false;
    protected final UEncoder urlEncoder = new UEncoder(UEncoder.SafeCharsSet.WITH_SLASH);
    protected final CharChunk redirectURLCC = new CharChunk();
    private final List<Cookie> cookies = new ArrayList<Cookie>();
    private HttpServletResponse applicationResponse = null;
    protected Request request = null;
    protected ResponseFacade facade = null;

    public Response() {
        this(8192);
    }

    public Response(int outputBufferSize) {
        this.outputBuffer = new OutputBuffer(outputBufferSize);
    }

    public void setCoyoteResponse(org.apache.coyote.Response coyoteResponse) {
        this.coyoteResponse = coyoteResponse;
        this.outputBuffer.setResponse(coyoteResponse);
    }

    public org.apache.coyote.Response getCoyoteResponse() {
        return this.coyoteResponse;
    }

    public Context getContext() {
        return this.request.getContext();
    }

    public void recycle() {
        this.cookies.clear();
        this.outputBuffer.recycle();
        this.usingOutputStream = false;
        this.usingWriter = false;
        this.appCommitted = false;
        this.included = false;
        this.isCharacterEncodingSet = false;
        this.applicationResponse = null;
        if (this.getRequest().getDiscardFacades()) {
            if (this.facade != null) {
                this.facade.clear();
                this.facade = null;
            }
            if (this.outputStream != null) {
                this.outputStream.clear();
                this.outputStream = null;
            }
            if (this.writer != null) {
                this.writer.clear();
                this.writer = null;
            }
        } else if (this.writer != null) {
            this.writer.recycle();
        }
    }

    public List<Cookie> getCookies() {
        return this.cookies;
    }

    public long getContentWritten() {
        return this.outputBuffer.getContentWritten();
    }

    public long getBytesWritten(boolean flush) {
        if (flush) {
            try {
                this.outputBuffer.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return this.getCoyoteResponse().getBytesWritten(flush);
    }

    public void setAppCommitted(boolean appCommitted) {
        this.appCommitted = appCommitted;
    }

    public boolean isAppCommitted() {
        return this.appCommitted || this.isCommitted() || this.isSuspended() || this.getContentLength() > 0 && this.getContentWritten() >= (long)this.getContentLength();
    }

    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        if (this.facade == null) {
            this.facade = new ResponseFacade(this);
        }
        if (this.applicationResponse == null) {
            this.applicationResponse = this.facade;
        }
        return this.applicationResponse;
    }

    public void setResponse(HttpServletResponse applicationResponse) {
        HttpServletResponse r = applicationResponse;
        while (r instanceof HttpServletResponseWrapper) {
            r = ((HttpServletResponseWrapper)r).getResponse();
        }
        if (r != this.facade) {
            throw new IllegalArgumentException(sm.getString("response.illegalWrap"));
        }
        this.applicationResponse = applicationResponse;
    }

    public void setSuspended(boolean suspended) {
        this.outputBuffer.setSuspended(suspended);
    }

    public boolean isSuspended() {
        return this.outputBuffer.isSuspended();
    }

    public boolean isClosed() {
        return this.outputBuffer.isClosed();
    }

    @Deprecated
    public boolean setError() {
        return this.getCoyoteResponse().setError();
    }

    public boolean isError() {
        return this.getCoyoteResponse().isError();
    }

    public boolean isErrorReportRequired() {
        return this.getCoyoteResponse().isErrorReportRequired();
    }

    public boolean setErrorReported() {
        return this.getCoyoteResponse().setErrorReported();
    }

    public void finishResponse() throws IOException {
        this.outputBuffer.close();
    }

    public int getContentLength() {
        return this.getCoyoteResponse().getContentLength();
    }

    public String getContentType() {
        return this.getCoyoteResponse().getContentType();
    }

    public PrintWriter getReporter() throws IOException {
        if (this.outputBuffer.isNew()) {
            this.outputBuffer.checkConverter();
            if (this.writer == null) {
                this.writer = new CoyoteWriter(this.outputBuffer);
            }
            return this.writer;
        }
        return null;
    }

    public void flushBuffer() throws IOException {
        this.outputBuffer.flush();
    }

    public int getBufferSize() {
        return this.outputBuffer.getBufferSize();
    }

    public String getCharacterEncoding() {
        String charset = this.getCoyoteResponse().getCharacterEncoding();
        if (charset != null) {
            return charset;
        }
        Context context = this.getContext();
        String result = null;
        if (context != null) {
            result = context.getResponseCharacterEncoding();
        }
        if (result == null) {
            result = Constants.DEFAULT_BODY_CHARSET.name();
        }
        return result;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        if (this.usingWriter) {
            throw new IllegalStateException(sm.getString("coyoteResponse.getOutputStream.ise"));
        }
        this.usingOutputStream = true;
        if (this.outputStream == null) {
            this.outputStream = new CoyoteOutputStream(this.outputBuffer);
        }
        return this.outputStream;
    }

    public Locale getLocale() {
        return this.getCoyoteResponse().getLocale();
    }

    public PrintWriter getWriter() throws IOException {
        if (this.usingOutputStream) {
            throw new IllegalStateException(sm.getString("coyoteResponse.getWriter.ise"));
        }
        if (ENFORCE_ENCODING_IN_GET_WRITER) {
            this.setCharacterEncoding(this.getCharacterEncoding());
        }
        this.usingWriter = true;
        this.outputBuffer.checkConverter();
        if (this.writer == null) {
            this.writer = new CoyoteWriter(this.outputBuffer);
        }
        return this.writer;
    }

    public boolean isCommitted() {
        return this.getCoyoteResponse().isCommitted();
    }

    public void reset() {
        if (this.included) {
            return;
        }
        this.getCoyoteResponse().reset();
        this.outputBuffer.reset();
        this.usingOutputStream = false;
        this.usingWriter = false;
        this.isCharacterEncodingSet = false;
    }

    public void resetBuffer() {
        this.resetBuffer(false);
    }

    public void resetBuffer(boolean resetWriterStreamFlags) {
        if (this.isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.resetBuffer.ise"));
        }
        this.outputBuffer.reset(resetWriterStreamFlags);
        if (resetWriterStreamFlags) {
            this.usingOutputStream = false;
            this.usingWriter = false;
            this.isCharacterEncodingSet = false;
        }
    }

    public void setBufferSize(int size) {
        if (this.isCommitted() || !this.outputBuffer.isNew()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.setBufferSize.ise"));
        }
        this.outputBuffer.setBufferSize(size);
    }

    public void setContentLength(int length) {
        this.setContentLengthLong(length);
    }

    public void setContentLengthLong(long length) {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.getCoyoteResponse().setContentLength(length);
    }

    public void setContentType(String type) {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        if (type == null) {
            this.getCoyoteResponse().setContentType(null);
            try {
                this.getCoyoteResponse().setCharacterEncoding(null);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            this.isCharacterEncodingSet = false;
            return;
        }
        String[] m = MEDIA_TYPE_CACHE.parse(type);
        if (m == null) {
            this.getCoyoteResponse().setContentTypeNoCharset(type);
            return;
        }
        if (m[1] == null) {
            this.getCoyoteResponse().setContentTypeNoCharset(type);
        } else {
            this.getCoyoteResponse().setContentTypeNoCharset(m[0]);
            if (!this.usingWriter) {
                try {
                    this.getCoyoteResponse().setCharacterEncoding(m[1]);
                }
                catch (UnsupportedEncodingException e) {
                    log.warn((Object)sm.getString("coyoteResponse.encoding.invalid", new Object[]{m[1]}), (Throwable)e);
                }
                this.isCharacterEncodingSet = true;
            }
        }
    }

    public void setCharacterEncoding(String encoding) {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        if (this.usingWriter) {
            return;
        }
        try {
            this.getCoyoteResponse().setCharacterEncoding(encoding);
        }
        catch (UnsupportedEncodingException e) {
            log.warn((Object)sm.getString("coyoteResponse.encoding.invalid", new Object[]{encoding}), (Throwable)e);
            return;
        }
        this.isCharacterEncodingSet = encoding != null;
    }

    public void setLocale(Locale locale) {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.getCoyoteResponse().setLocale(locale);
        if (this.usingWriter) {
            return;
        }
        if (this.isCharacterEncodingSet) {
            return;
        }
        if (locale == null) {
            try {
                this.getCoyoteResponse().setCharacterEncoding(null);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {}
        } else {
            String charset;
            Context context = this.getContext();
            if (context != null && (charset = context.getCharset(locale)) != null) {
                try {
                    this.getCoyoteResponse().setCharacterEncoding(charset);
                }
                catch (UnsupportedEncodingException e) {
                    log.warn((Object)sm.getString("coyoteResponse.encoding.invalid", new Object[]{charset}), (Throwable)e);
                }
            }
        }
    }

    public String getHeader(String name) {
        return this.getCoyoteResponse().getMimeHeaders().getHeader(name);
    }

    public Collection<String> getHeaderNames() {
        MimeHeaders headers = this.getCoyoteResponse().getMimeHeaders();
        int n = headers.size();
        ArrayList<String> result = new ArrayList<String>(n);
        for (int i = 0; i < n; ++i) {
            result.add(headers.getName(i).toString());
        }
        return result;
    }

    public Collection<String> getHeaders(String name) {
        Enumeration enumeration = this.getCoyoteResponse().getMimeHeaders().values(name);
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        while (enumeration.hasMoreElements()) {
            result.add((String)enumeration.nextElement());
        }
        return result;
    }

    public String getMessage() {
        return this.getCoyoteResponse().getMessage();
    }

    public int getStatus() {
        return this.getCoyoteResponse().getStatus();
    }

    public void addCookie(Cookie cookie) {
        if (this.included || this.isCommitted()) {
            return;
        }
        this.cookies.add(cookie);
        String header = this.generateCookieString(cookie);
        this.addHeader("Set-Cookie", header, this.getContext().getCookieProcessor().getCharset());
    }

    public void addSessionCookieInternal(Cookie cookie) {
        if (this.isCommitted()) {
            return;
        }
        String name = cookie.getName();
        String headername = "Set-Cookie";
        String startsWith = name + "=";
        String header = this.generateCookieString(cookie);
        boolean set = false;
        MimeHeaders headers = this.getCoyoteResponse().getMimeHeaders();
        int n = headers.size();
        for (int i = 0; i < n; ++i) {
            if (!headers.getName(i).toString().equals("Set-Cookie") || !headers.getValue(i).toString().startsWith(startsWith)) continue;
            headers.getValue(i).setString(header);
            set = true;
        }
        if (!set) {
            this.addHeader("Set-Cookie", header);
        }
    }

    public String generateCookieString(Cookie cookie) {
        if (SecurityUtil.isPackageProtectionEnabled()) {
            return AccessController.doPrivileged(new PrivilegedGenerateCookieString(this.getContext(), cookie, this.request.getRequest()));
        }
        return this.getContext().getCookieProcessor().generateHeader(cookie, this.request.getRequest());
    }

    public void addDateHeader(String name, long value) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.addHeader(name, FastHttpDateFormat.formatDate((long)value));
    }

    public void addHeader(String name, String value) {
        this.addHeader(name, value, null);
    }

    private void addHeader(String name, String value, Charset charset) {
        if (name == null || name.length() == 0 || value == null) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        this.getCoyoteResponse().addHeader(name, value, charset);
    }

    private boolean checkSpecialHeader(String name, String value) {
        if (name.equalsIgnoreCase("Content-Type")) {
            this.setContentType(value);
            return true;
        }
        return false;
    }

    public void addIntHeader(String name, int value) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.addHeader(name, "" + value);
    }

    public boolean containsHeader(String name) {
        char cc = name.charAt(0);
        if (cc == 'C' || cc == 'c') {
            if (name.equalsIgnoreCase("Content-Type")) {
                return this.getCoyoteResponse().getContentType() != null;
            }
            if (name.equalsIgnoreCase("Content-Length")) {
                return this.getCoyoteResponse().getContentLengthLong() != -1L;
            }
        }
        return this.getCoyoteResponse().containsHeader(name);
    }

    public void setTrailerFields(Supplier<Map<String, String>> supplier) {
        this.getCoyoteResponse().setTrailerFields(supplier);
    }

    public Supplier<Map<String, String>> getTrailerFields() {
        return this.getCoyoteResponse().getTrailerFields();
    }

    public String encodeRedirectURL(String url) {
        if (this.isEncodeable(this.toAbsolute(url))) {
            return this.toEncoded(url, this.request.getSessionInternal().getIdInternal());
        }
        return url;
    }

    @Deprecated
    public String encodeRedirectUrl(String url) {
        return this.encodeRedirectURL(url);
    }

    public String encodeURL(String url) {
        String absolute;
        try {
            absolute = this.toAbsolute(url);
        }
        catch (IllegalArgumentException iae) {
            return url;
        }
        if (this.isEncodeable(absolute)) {
            if (url.equalsIgnoreCase("")) {
                url = absolute;
            } else if (url.equals(absolute) && !this.hasPath(url)) {
                url = url + '/';
            }
            return this.toEncoded(url, this.request.getSessionInternal().getIdInternal());
        }
        return url;
    }

    @Deprecated
    public String encodeUrl(String url) {
        return this.encodeURL(url);
    }

    @Deprecated
    public void sendAcknowledgement() throws IOException {
        this.sendAcknowledgement(ContinueResponseTiming.ALWAYS);
    }

    public void sendAcknowledgement(ContinueResponseTiming continueResponseTiming) throws IOException {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.getCoyoteResponse().action(ActionCode.ACK, (Object)continueResponseTiming);
    }

    public void sendError(int status) throws IOException {
        this.sendError(status, null);
    }

    public void sendError(int status, String message) throws IOException {
        if (this.isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.sendError.ise"));
        }
        if (this.included) {
            return;
        }
        this.setError();
        this.getCoyoteResponse().setStatus(status);
        this.getCoyoteResponse().setMessage(message);
        this.resetBuffer();
        this.setSuspended(true);
    }

    public void sendRedirect(String location) throws IOException {
        this.sendRedirect(location, 302);
    }

    public void sendRedirect(String location, int status) throws IOException {
        if (this.isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteResponse.sendRedirect.ise"));
        }
        if (this.included) {
            return;
        }
        this.resetBuffer(true);
        try {
            Context context = this.getContext();
            String locationUri = this.getRequest().getCoyoteRequest().getSupportsRelativeRedirects() && (context == null || context.getUseRelativeRedirects()) ? location : this.toAbsolute(location);
            this.setStatus(status);
            this.setHeader("Location", locationUri);
            if (context != null && context.getSendRedirectBody()) {
                PrintWriter writer = this.getWriter();
                writer.print(sm.getString("coyoteResponse.sendRedirect.note", new Object[]{Escape.htmlElementContent((String)locationUri)}));
                this.flushBuffer();
            }
        }
        catch (IllegalArgumentException e) {
            log.warn((Object)sm.getString("response.sendRedirectFail", new Object[]{location}), (Throwable)e);
            this.setStatus(404);
        }
        this.setSuspended(true);
    }

    public void setDateHeader(String name, long value) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.setHeader(name, FastHttpDateFormat.formatDate((long)value));
    }

    public void setHeader(String name, String value) {
        if (name == null || name.length() == 0 || value == null) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        char cc = name.charAt(0);
        if ((cc == 'C' || cc == 'c') && this.checkSpecialHeader(name, value)) {
            return;
        }
        this.getCoyoteResponse().setHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        if (name == null || name.length() == 0) {
            return;
        }
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.setHeader(name, "" + value);
    }

    public void setStatus(int status) {
        this.setStatus(status, null);
    }

    @Deprecated
    public void setStatus(int status, String message) {
        if (this.isCommitted()) {
            return;
        }
        if (this.included) {
            return;
        }
        this.getCoyoteResponse().setStatus(status);
        this.getCoyoteResponse().setMessage(message);
    }

    protected boolean isEncodeable(String location) {
        if (location == null) {
            return false;
        }
        if (location.startsWith("#")) {
            return false;
        }
        Request hreq = this.request;
        Session session = hreq.getSessionInternal(false);
        if (session == null) {
            return false;
        }
        if (hreq.isRequestedSessionIdFromCookie()) {
            return false;
        }
        if (!hreq.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.URL)) {
            return false;
        }
        if (SecurityUtil.isPackageProtectionEnabled()) {
            Boolean result = AccessController.doPrivileged(new PrivilegedDoIsEncodable(this.getContext(), hreq, session, location));
            return result;
        }
        return Response.doIsEncodeable(this.getContext(), hreq, session, location);
    }

    private static boolean doIsEncodeable(Context context, Request hreq, Session session, String location) {
        int urlPort;
        URL url = null;
        try {
            URI uri = new URI(location);
            url = uri.toURL();
        }
        catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
            return false;
        }
        if (!hreq.getScheme().equalsIgnoreCase(url.getProtocol())) {
            return false;
        }
        if (!hreq.getServerName().equalsIgnoreCase(url.getHost())) {
            return false;
        }
        int serverPort = hreq.getServerPort();
        if (serverPort == -1) {
            serverPort = "https".equals(hreq.getScheme()) ? 443 : 80;
        }
        if ((urlPort = url.getPort()) == -1) {
            urlPort = "https".equals(url.getProtocol()) ? 443 : 80;
        }
        if (serverPort != urlPort) {
            return false;
        }
        String contextPath = context.getPath();
        if (contextPath != null) {
            String file = url.getFile();
            if (!file.startsWith(contextPath)) {
                return false;
            }
            String tok = ";" + SessionConfig.getSessionUriParamName(context) + "=" + session.getIdInternal();
            if (file.indexOf(tok, contextPath.length()) >= 0) {
                return false;
            }
        }
        return true;
    }

    protected String toAbsolute(String location) {
        if (location == null) {
            return location;
        }
        boolean leadingSlash = location.startsWith("/");
        if (location.startsWith("//")) {
            this.redirectURLCC.recycle();
            String scheme = this.request.getScheme();
            try {
                this.redirectURLCC.append(scheme, 0, scheme.length());
                this.redirectURLCC.append(':');
                this.redirectURLCC.append(location, 0, location.length());
                return this.redirectURLCC.toString();
            }
            catch (IOException e) {
                throw new IllegalArgumentException(location, e);
            }
        }
        if (leadingSlash || !UriUtil.hasScheme((CharSequence)location)) {
            this.redirectURLCC.recycle();
            String scheme = this.request.getScheme();
            String name = this.request.getServerName();
            int port = this.request.getServerPort();
            try {
                this.redirectURLCC.append(scheme, 0, scheme.length());
                this.redirectURLCC.append("://", 0, 3);
                this.redirectURLCC.append(name, 0, name.length());
                if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
                    this.redirectURLCC.append(':');
                    String portS = port + "";
                    this.redirectURLCC.append(portS, 0, portS.length());
                }
                if (!leadingSlash) {
                    String relativePath = this.request.getDecodedRequestURI();
                    int pos = relativePath.lastIndexOf(47);
                    CharChunk encodedURI = null;
                    if (SecurityUtil.isPackageProtectionEnabled()) {
                        try {
                            encodedURI = AccessController.doPrivileged(new PrivilegedEncodeUrl(this.urlEncoder, relativePath, pos));
                        }
                        catch (PrivilegedActionException pae) {
                            throw new IllegalArgumentException(location, pae.getException());
                        }
                    } else {
                        encodedURI = this.urlEncoder.encodeURL(relativePath, 0, pos);
                    }
                    this.redirectURLCC.append(encodedURI);
                    encodedURI.recycle();
                    this.redirectURLCC.append('/');
                }
                this.redirectURLCC.append(location, 0, location.length());
                this.normalize(this.redirectURLCC);
            }
            catch (IOException e) {
                throw new IllegalArgumentException(location, e);
            }
            return this.redirectURLCC.toString();
        }
        return location;
    }

    private void normalize(CharChunk cc) {
        int truncate = cc.indexOf('?');
        if (truncate == -1) {
            truncate = cc.indexOf('#');
        }
        char[] truncateCC = null;
        if (truncate > -1) {
            truncateCC = Arrays.copyOfRange(cc.getBuffer(), cc.getStart() + truncate, cc.getEnd());
            cc.setEnd(cc.getStart() + truncate);
        }
        if (cc.endsWith("/.") || cc.endsWith("/..")) {
            try {
                cc.append('/');
            }
            catch (IOException e) {
                throw new IllegalArgumentException(cc.toString(), e);
            }
        }
        char[] c = cc.getChars();
        int start = cc.getStart();
        int end = cc.getEnd();
        int index = 0;
        int startIndex = 0;
        for (int i = 0; i < 3; ++i) {
            startIndex = cc.indexOf('/', startIndex + 1);
        }
        index = startIndex;
        while ((index = cc.indexOf("/./", 0, 3, index)) >= 0) {
            this.copyChars(c, start + index, start + index + 2, end - start - index - 2);
            cc.setEnd(end -= 2);
        }
        index = startIndex;
        while ((index = cc.indexOf("/../", 0, 4, index)) >= 0) {
            if (index == startIndex) {
                throw new IllegalArgumentException();
            }
            int index2 = -1;
            for (int pos = start + index - 1; pos >= 0 && index2 < 0; --pos) {
                if (c[pos] != '/') continue;
                index2 = pos;
            }
            this.copyChars(c, start + index2, start + index + 3, end - start - index - 3);
            end = end + index2 - index - 3;
            cc.setEnd(end);
            index = index2;
        }
        if (truncateCC != null) {
            try {
                cc.append(truncateCC, 0, truncateCC.length);
            }
            catch (IOException ioe) {
                throw new IllegalArgumentException(ioe);
            }
        }
    }

    private void copyChars(char[] c, int dest, int src, int len) {
        System.arraycopy(c, src, c, dest, len);
    }

    private boolean hasPath(String uri) {
        int pos = uri.indexOf("://");
        if (pos < 0) {
            return false;
        }
        return (pos = uri.indexOf(47, pos + 3)) >= 0;
    }

    protected String toEncoded(String url, String sessionId) {
        StringBuilder sb;
        int pound;
        if (url == null || sessionId == null) {
            return url;
        }
        String path = url;
        String query = "";
        String anchor = "";
        int question = url.indexOf(63);
        if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
        }
        if ((pound = path.indexOf(35)) >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
        }
        if ((sb = new StringBuilder(path)).length() > 0) {
            sb.append(';');
            sb.append(SessionConfig.getSessionUriParamName(this.request.getContext()));
            sb.append('=');
            sb.append(sessionId);
        }
        sb.append(anchor);
        sb.append(query);
        return sb.toString();
    }

    private static class PrivilegedGenerateCookieString
    implements PrivilegedAction<String> {
        private final Context context;
        private final Cookie cookie;
        private final HttpServletRequest request;

        PrivilegedGenerateCookieString(Context context, Cookie cookie, HttpServletRequest request) {
            this.context = context;
            this.cookie = cookie;
            this.request = request;
        }

        @Override
        public String run() {
            return this.context.getCookieProcessor().generateHeader(this.cookie, this.request);
        }
    }

    private static class PrivilegedDoIsEncodable
    implements PrivilegedAction<Boolean> {
        private final Context context;
        private final Request hreq;
        private final Session session;
        private final String location;

        PrivilegedDoIsEncodable(Context context, Request hreq, Session session, String location) {
            this.context = context;
            this.hreq = hreq;
            this.session = session;
            this.location = location;
        }

        @Override
        public Boolean run() {
            return Response.doIsEncodeable(this.context, this.hreq, this.session, this.location);
        }
    }

    private static class PrivilegedEncodeUrl
    implements PrivilegedExceptionAction<CharChunk> {
        private final UEncoder urlEncoder;
        private final String relativePath;
        private final int end;

        PrivilegedEncodeUrl(UEncoder urlEncoder, String relativePath, int end) {
            this.urlEncoder = urlEncoder;
            this.relativePath = relativePath;
            this.end = end;
        }

        @Override
        public CharChunk run() throws IOException {
            return this.urlEncoder.encodeURL(this.relativePath, 0, this.end);
        }
    }
}

