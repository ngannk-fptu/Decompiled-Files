/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.DispatcherType
 *  javax.servlet.FilterChain
 *  javax.servlet.MultipartConfigElement
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestAttributeEvent
 *  javax.servlet.ServletRequestAttributeListener
 *  javax.servlet.ServletResponse
 *  javax.servlet.SessionTrackingMode
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpUpgradeHandler
 *  javax.servlet.http.Part
 *  javax.servlet.http.PushBuilder
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.Constants
 *  org.apache.coyote.Request
 *  org.apache.coyote.UpgradeToken
 *  org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.ContextBind
 *  org.apache.tomcat.InstanceManager
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.B2CConverter
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.EncodedSolidusHandling
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.http.CookieProcessor
 *  org.apache.tomcat.util.http.FastHttpDateFormat
 *  org.apache.tomcat.util.http.Parameters
 *  org.apache.tomcat.util.http.Parameters$FailReason
 *  org.apache.tomcat.util.http.RequestUtil
 *  org.apache.tomcat.util.http.Rfc6265CookieProcessor
 *  org.apache.tomcat.util.http.ServerCookie
 *  org.apache.tomcat.util.http.ServerCookies
 *  org.apache.tomcat.util.http.fileupload.FileItem
 *  org.apache.tomcat.util.http.fileupload.FileItemFactory
 *  org.apache.tomcat.util.http.fileupload.RequestContext
 *  org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory
 *  org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException
 *  org.apache.tomcat.util.http.fileupload.impl.SizeException
 *  org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload
 *  org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext
 *  org.apache.tomcat.util.http.parser.AcceptLanguage
 *  org.apache.tomcat.util.http.parser.Upgrade
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletResponse;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.CoyoteInputStream;
import org.apache.catalina.connector.CoyoteReader;
import org.apache.catalina.connector.InputBuffer;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.ApplicationMapping;
import org.apache.catalina.core.ApplicationPart;
import org.apache.catalina.core.ApplicationPushBuilder;
import org.apache.catalina.core.ApplicationSessionCookieConfig;
import org.apache.catalina.core.AsyncContextImpl;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.RequestUtil;
import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.util.URLEncoder;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Constants;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.ContextBind;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.EncodedSolidusHandling;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.apache.tomcat.util.http.fileupload.impl.SizeException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.apache.tomcat.util.http.parser.AcceptLanguage;
import org.apache.tomcat.util.http.parser.Upgrade;
import org.apache.tomcat.util.res.StringManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class Request
implements HttpServletRequest {
    private static final String HTTP_UPGRADE_HEADER_NAME = "upgrade";
    private static final Log log = LogFactory.getLog(Request.class);
    protected org.apache.coyote.Request coyoteRequest;
    @Deprecated
    protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    protected static final StringManager sm = StringManager.getManager(Request.class);
    protected Cookie[] cookies = null;
    @Deprecated
    protected final SimpleDateFormat[] formats;
    @Deprecated
    private static final SimpleDateFormat[] formatsTemplate = new SimpleDateFormat[]{new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)};
    protected static final Locale defaultLocale = Locale.getDefault();
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
    protected boolean sslAttributesParsed = false;
    protected final ArrayList<Locale> locales = new ArrayList();
    private final transient HashMap<String, Object> notes = new HashMap();
    protected String authType = null;
    protected DispatcherType internalDispatcherType = null;
    protected final InputBuffer inputBuffer = new InputBuffer();
    protected CoyoteInputStream inputStream = new CoyoteInputStream(this.inputBuffer);
    protected CoyoteReader reader = new CoyoteReader(this.inputBuffer);
    protected boolean usingInputStream = false;
    protected boolean usingReader = false;
    protected Principal userPrincipal = null;
    protected boolean parametersParsed = false;
    protected boolean cookiesParsed = false;
    protected boolean cookiesConverted = false;
    protected boolean secure = false;
    protected transient Subject subject = null;
    protected static final int CACHED_POST_LEN = 8192;
    protected byte[] postData = null;
    protected ParameterMap<String, String[]> parameterMap = new ParameterMap();
    protected Collection<Part> parts = null;
    protected Exception partsParseException = null;
    protected Session session = null;
    protected Object requestDispatcherPath = null;
    protected boolean requestedSessionCookie = false;
    protected String requestedSessionId = null;
    protected boolean requestedSessionURL = false;
    protected boolean requestedSessionSSL = false;
    protected boolean localesParsed = false;
    protected int localPort = -1;
    protected String remoteAddr = null;
    protected String peerAddr = null;
    protected String remoteHost = null;
    protected int remotePort = -1;
    protected String localAddr = null;
    protected String localName = null;
    private volatile AsyncContextImpl asyncContext = null;
    protected Boolean asyncSupported = null;
    private HttpServletRequest applicationRequest = null;
    protected final Connector connector;
    protected FilterChain filterChain = null;
    protected final MappingData mappingData = new MappingData();
    private final ApplicationMapping applicationMapping = new ApplicationMapping(this.mappingData);
    protected RequestFacade facade = null;
    protected Response response = null;
    protected B2CConverter URIConverter = null;
    private static final Map<String, SpecialAttributeAdapter> specialAttributes = new HashMap<String, SpecialAttributeAdapter>();

    public Request(Connector connector) {
        this.connector = connector;
        this.formats = new SimpleDateFormat[formatsTemplate.length];
        for (int i = 0; i < this.formats.length; ++i) {
            this.formats[i] = (SimpleDateFormat)formatsTemplate[i].clone();
        }
    }

    public void setCoyoteRequest(org.apache.coyote.Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
        this.inputBuffer.setRequest(coyoteRequest);
    }

    public org.apache.coyote.Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    protected void addPathParameter(String name, String value) {
        this.coyoteRequest.addPathParameter(name, value);
    }

    protected String getPathParameter(String name) {
        return this.coyoteRequest.getPathParameter(name);
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public void recycle() {
        this.internalDispatcherType = null;
        this.requestDispatcherPath = null;
        this.authType = null;
        this.inputBuffer.recycle();
        this.usingInputStream = false;
        this.usingReader = false;
        this.userPrincipal = null;
        this.subject = null;
        this.parametersParsed = false;
        if (this.parts != null) {
            for (Part part : this.parts) {
                try {
                    part.delete();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.warn((Object)sm.getString("coyoteRequest.deletePartFailed", new Object[]{part.getName()}), t);
                }
            }
            this.parts = null;
        }
        this.partsParseException = null;
        this.locales.clear();
        this.localesParsed = false;
        this.secure = false;
        this.remoteAddr = null;
        this.peerAddr = null;
        this.remoteHost = null;
        this.remotePort = -1;
        this.localPort = -1;
        this.localAddr = null;
        this.localName = null;
        this.attributes.clear();
        this.sslAttributesParsed = false;
        this.notes.clear();
        this.recycleSessionInfo();
        this.recycleCookieInfo(false);
        if (this.getDiscardFacades()) {
            this.parameterMap = new ParameterMap();
        } else {
            this.parameterMap.setLocked(false);
            this.parameterMap.clear();
        }
        this.mappingData.recycle();
        this.applicationMapping.recycle();
        this.applicationRequest = null;
        if (this.getDiscardFacades()) {
            if (this.facade != null) {
                this.facade.clear();
                this.facade = null;
            }
            if (this.inputStream != null) {
                this.inputStream.clear();
                this.inputStream = null;
            }
            if (this.reader != null) {
                this.reader.clear();
                this.reader = null;
            }
        }
        this.asyncSupported = null;
        if (this.asyncContext != null) {
            this.asyncContext.recycle();
            this.asyncContext = null;
        }
    }

    protected void recycleSessionInfo() {
        if (this.session != null) {
            try {
                this.session.endAccess();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.warn((Object)sm.getString("coyoteRequest.sessionEndAccessFail"), t);
            }
        }
        this.session = null;
        this.requestedSessionCookie = false;
        this.requestedSessionId = null;
        this.requestedSessionURL = false;
        this.requestedSessionSSL = false;
    }

    protected void recycleCookieInfo(boolean recycleCoyote) {
        this.cookiesParsed = false;
        this.cookiesConverted = false;
        this.cookies = null;
        if (recycleCoyote) {
            this.getCoyoteRequest().getCookies().recycle();
        }
    }

    public Connector getConnector() {
        return this.connector;
    }

    public Context getContext() {
        return this.mappingData.context;
    }

    public boolean getDiscardFacades() {
        return this.connector == null ? true : this.connector.getDiscardFacades();
    }

    public FilterChain getFilterChain() {
        return this.filterChain;
    }

    public void setFilterChain(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public Host getHost() {
        return this.mappingData.host;
    }

    public MappingData getMappingData() {
        return this.mappingData;
    }

    public HttpServletRequest getRequest() {
        if (this.facade == null) {
            this.facade = new RequestFacade(this);
        }
        if (this.applicationRequest == null) {
            this.applicationRequest = this.facade;
        }
        return this.applicationRequest;
    }

    public void setRequest(HttpServletRequest applicationRequest) {
        HttpServletRequest r = applicationRequest;
        while (r instanceof HttpServletRequestWrapper) {
            r = ((HttpServletRequestWrapper)r).getRequest();
        }
        if (r != this.facade) {
            throw new IllegalArgumentException(sm.getString("request.illegalWrap"));
        }
        this.applicationRequest = applicationRequest;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public InputStream getStream() {
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    protected B2CConverter getURIConverter() {
        return this.URIConverter;
    }

    protected void setURIConverter(B2CConverter URIConverter) {
        this.URIConverter = URIConverter;
    }

    public Wrapper getWrapper() {
        return this.mappingData.wrapper;
    }

    public ServletInputStream createInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    public void finishRequest() throws IOException {
        if (this.response.getStatus() == 413) {
            this.checkSwallowInput();
        }
    }

    public Object getNote(String name) {
        return this.notes.get(name);
    }

    public void removeNote(String name) {
        this.notes.remove(name);
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public void setNote(String name, Object value) {
        this.notes.put(name, value);
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setServerPort(int port) {
        this.coyoteRequest.setServerPort(port);
    }

    public Object getAttribute(String name) {
        SpecialAttributeAdapter adapter = specialAttributes.get(name);
        if (adapter != null) {
            return adapter.get(this, name);
        }
        Object attr = this.attributes.get(name);
        if (attr != null) {
            return attr;
        }
        attr = this.coyoteRequest.getAttribute(name);
        if (attr != null) {
            return attr;
        }
        if (!this.sslAttributesParsed && TLSUtil.isTLSRequestAttribute(name)) {
            this.coyoteRequest.action(ActionCode.REQ_SSL_ATTRIBUTE, (Object)this.coyoteRequest);
            attr = this.coyoteRequest.getAttribute("javax.servlet.request.X509Certificate");
            if (attr != null) {
                this.attributes.put("javax.servlet.request.X509Certificate", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("javax.servlet.request.cipher_suite")) != null) {
                this.attributes.put("javax.servlet.request.cipher_suite", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("javax.servlet.request.key_size")) != null) {
                this.attributes.put("javax.servlet.request.key_size", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("javax.servlet.request.ssl_session_id")) != null) {
                this.attributes.put("javax.servlet.request.ssl_session_id", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("javax.servlet.request.ssl_session_mgr")) != null) {
                this.attributes.put("javax.servlet.request.ssl_session_mgr", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("org.apache.tomcat.util.net.secure_protocol_version")) != null) {
                this.attributes.put("org.apache.tomcat.util.net.secure_protocol_version", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("org.apache.tomcat.util.net.secure_requested_protocol_versions")) != null) {
                this.attributes.put("org.apache.tomcat.util.net.secure_requested_protocol_versions", attr);
            }
            if ((attr = this.coyoteRequest.getAttribute("org.apache.tomcat.util.net.secure_requested_ciphers")) != null) {
                this.attributes.put("org.apache.tomcat.util.net.secure_requested_ciphers", attr);
            }
            attr = this.attributes.get(name);
            this.sslAttributesParsed = true;
        }
        return attr;
    }

    public long getContentLengthLong() {
        return this.coyoteRequest.getContentLengthLong();
    }

    public Enumeration<String> getAttributeNames() {
        if (this.isSecure() && !this.sslAttributesParsed) {
            this.getAttribute("javax.servlet.request.X509Certificate");
        }
        HashSet<String> names = new HashSet<String>(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    public String getCharacterEncoding() {
        String characterEncoding = this.coyoteRequest.getCharacterEncoding();
        if (characterEncoding != null) {
            return characterEncoding;
        }
        Context context = this.getContext();
        if (context != null) {
            return context.getRequestCharacterEncoding();
        }
        return null;
    }

    private Charset getCharset() {
        String encoding;
        Charset charset = null;
        try {
            charset = this.coyoteRequest.getCharset();
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        if (charset != null) {
            return charset;
        }
        Context context = this.getContext();
        if (context != null && (encoding = context.getRequestCharacterEncoding()) != null) {
            try {
                return B2CConverter.getCharset((String)encoding);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return Constants.DEFAULT_BODY_CHARSET;
    }

    public int getContentLength() {
        return this.coyoteRequest.getContentLength();
    }

    public String getContentType() {
        return this.coyoteRequest.getContentType();
    }

    public void setContentType(String contentType) {
        this.coyoteRequest.setContentType(contentType);
    }

    public ServletInputStream getInputStream() throws IOException {
        if (this.usingReader) {
            throw new IllegalStateException(sm.getString("coyoteRequest.getInputStream.ise"));
        }
        this.usingInputStream = true;
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    public Locale getLocale() {
        if (!this.localesParsed) {
            this.parseLocales();
        }
        if (this.locales.size() > 0) {
            return this.locales.get(0);
        }
        return defaultLocale;
    }

    public Enumeration<Locale> getLocales() {
        if (!this.localesParsed) {
            this.parseLocales();
        }
        if (this.locales.size() > 0) {
            return Collections.enumeration(this.locales);
        }
        ArrayList<Locale> results = new ArrayList<Locale>();
        results.add(defaultLocale);
        return Collections.enumeration(results);
    }

    public String getParameter(String name) {
        if (!this.parametersParsed) {
            this.parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameter(name);
    }

    public Map<String, String[]> getParameterMap() {
        if (this.parameterMap.isLocked()) {
            return this.parameterMap;
        }
        Enumeration<String> enumeration = this.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String[] values = this.getParameterValues(name);
            this.parameterMap.put(name, values);
        }
        this.parameterMap.setLocked(true);
        return this.parameterMap;
    }

    public Enumeration<String> getParameterNames() {
        if (!this.parametersParsed) {
            this.parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameterNames();
    }

    public String[] getParameterValues(String name) {
        if (!this.parametersParsed) {
            this.parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameterValues(name);
    }

    public String getProtocol() {
        return this.coyoteRequest.protocol().toStringType();
    }

    public BufferedReader getReader() throws IOException {
        String enc;
        Context context;
        if (this.usingInputStream) {
            throw new IllegalStateException(sm.getString("coyoteRequest.getReader.ise"));
        }
        if (this.coyoteRequest.getCharacterEncoding() == null && (context = this.getContext()) != null && (enc = context.getRequestCharacterEncoding()) != null) {
            this.setCharacterEncoding(enc);
        }
        this.usingReader = true;
        this.inputBuffer.checkConverter();
        if (this.reader == null) {
            this.reader = new CoyoteReader(this.inputBuffer);
        }
        return this.reader;
    }

    @Deprecated
    public String getRealPath(String path) {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        ServletContext servletContext = context.getServletContext();
        if (servletContext == null) {
            return null;
        }
        try {
            return servletContext.getRealPath(path);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            this.coyoteRequest.action(ActionCode.REQ_HOST_ADDR_ATTRIBUTE, (Object)this.coyoteRequest);
            this.remoteAddr = this.coyoteRequest.remoteAddr().toString();
        }
        return this.remoteAddr;
    }

    public String getPeerAddr() {
        if (this.peerAddr == null) {
            this.coyoteRequest.action(ActionCode.REQ_PEER_ADDR_ATTRIBUTE, (Object)this.coyoteRequest);
            this.peerAddr = this.coyoteRequest.peerAddr().toString();
        }
        return this.peerAddr;
    }

    public String getRemoteHost() {
        if (this.remoteHost == null) {
            if (!this.connector.getEnableLookups()) {
                this.remoteHost = this.getRemoteAddr();
            } else {
                this.coyoteRequest.action(ActionCode.REQ_HOST_ATTRIBUTE, (Object)this.coyoteRequest);
                this.remoteHost = this.coyoteRequest.remoteHost().toString();
            }
        }
        return this.remoteHost;
    }

    public int getRemotePort() {
        if (this.remotePort == -1) {
            this.coyoteRequest.action(ActionCode.REQ_REMOTEPORT_ATTRIBUTE, (Object)this.coyoteRequest);
            this.remotePort = this.coyoteRequest.getRemotePort();
        }
        return this.remotePort;
    }

    public String getLocalName() {
        if (this.localName == null) {
            this.coyoteRequest.action(ActionCode.REQ_LOCAL_NAME_ATTRIBUTE, (Object)this.coyoteRequest);
            this.localName = this.coyoteRequest.localName().toString();
        }
        return this.localName;
    }

    public String getLocalAddr() {
        if (this.localAddr == null) {
            this.coyoteRequest.action(ActionCode.REQ_LOCAL_ADDR_ATTRIBUTE, (Object)this.coyoteRequest);
            this.localAddr = this.coyoteRequest.localAddr().toString();
        }
        return this.localAddr;
    }

    public int getLocalPort() {
        if (this.localPort == -1) {
            this.coyoteRequest.action(ActionCode.REQ_LOCALPORT_ATTRIBUTE, (Object)this.coyoteRequest);
            this.localPort = this.coyoteRequest.getLocalPort();
        }
        return this.localPort;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        if (path == null) {
            return null;
        }
        int fragmentPos = path.indexOf(35);
        if (fragmentPos > -1) {
            log.warn((Object)sm.getString("request.fragmentInDispatchPath", new Object[]{path}));
            path = path.substring(0, fragmentPos);
        }
        if (path.startsWith("/")) {
            return context.getServletContext().getRequestDispatcher(path);
        }
        String servletPath = (String)this.getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = this.getServletPath();
        }
        String pathInfo = this.getPathInfo();
        String requestPath = null;
        requestPath = pathInfo == null ? servletPath : servletPath + pathInfo;
        int pos = requestPath.lastIndexOf(47);
        String relative = null;
        relative = context.getDispatchersUseEncodedPaths() ? (pos >= 0 ? URLEncoder.DEFAULT.encode(requestPath.substring(0, pos + 1), StandardCharsets.UTF_8) + path : URLEncoder.DEFAULT.encode(requestPath, StandardCharsets.UTF_8) + path) : (pos >= 0 ? requestPath.substring(0, pos + 1) + path : requestPath + path);
        return context.getServletContext().getRequestDispatcher(relative);
    }

    public String getScheme() {
        return this.coyoteRequest.scheme().toStringType();
    }

    public String getServerName() {
        return this.coyoteRequest.serverName().toString();
    }

    public int getServerPort() {
        return this.coyoteRequest.getServerPort();
    }

    public boolean isSecure() {
        return this.secure;
    }

    public void removeAttribute(String name) {
        boolean found;
        if (name.startsWith("org.apache.tomcat.")) {
            this.coyoteRequest.getAttributes().remove(name);
        }
        if (found = this.attributes.containsKey(name)) {
            Object value = this.attributes.get(name);
            this.attributes.remove(name);
            this.notifyAttributeRemoved(name, value);
        }
    }

    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("coyoteRequest.setAttribute.namenull"));
        }
        if (value == null) {
            this.removeAttribute(name);
            return;
        }
        SpecialAttributeAdapter adapter = specialAttributes.get(name);
        if (adapter != null) {
            adapter.set(this, name, value);
            return;
        }
        if (Globals.IS_SECURITY_ENABLED && name.equals("org.apache.tomcat.sendfile.filename")) {
            String canonicalPath;
            try {
                canonicalPath = new File(value.toString()).getCanonicalPath();
            }
            catch (IOException e) {
                throw new SecurityException(sm.getString("coyoteRequest.sendfileNotCanonical", new Object[]{value}), e);
            }
            System.getSecurityManager().checkRead(canonicalPath);
            value = canonicalPath;
        }
        Object oldValue = this.attributes.put(name, value);
        if (name.startsWith("org.apache.tomcat.")) {
            this.coyoteRequest.setAttribute(name, value);
        }
        this.notifyAttributeAssigned(name, value, oldValue);
    }

    private void notifyAttributeAssigned(String name, Object value, Object oldValue) {
        Context context = this.getContext();
        if (context == null) {
            return;
        }
        Object[] listeners = context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        boolean replaced = oldValue != null;
        ServletRequestAttributeEvent event = null;
        event = replaced ? new ServletRequestAttributeEvent(context.getServletContext(), (ServletRequest)this.getRequest(), name, oldValue) : new ServletRequestAttributeEvent(context.getServletContext(), (ServletRequest)this.getRequest(), name, value);
        for (Object o : listeners) {
            if (!(o instanceof ServletRequestAttributeListener)) continue;
            ServletRequestAttributeListener listener = (ServletRequestAttributeListener)o;
            try {
                if (replaced) {
                    listener.attributeReplaced(event);
                    continue;
                }
                listener.attributeAdded(event);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.attributes.put("javax.servlet.error.exception", t);
                context.getLogger().error((Object)sm.getString("coyoteRequest.attributeEvent"), t);
            }
        }
    }

    private void notifyAttributeRemoved(String name, Object value) {
        Context context = this.getContext();
        Object[] listeners = context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(context.getServletContext(), (ServletRequest)this.getRequest(), name, value);
        for (Object o : listeners) {
            if (!(o instanceof ServletRequestAttributeListener)) continue;
            ServletRequestAttributeListener listener = (ServletRequestAttributeListener)o;
            try {
                listener.attributeRemoved(event);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.attributes.put("javax.servlet.error.exception", t);
                context.getLogger().error((Object)sm.getString("coyoteRequest.attributeEvent"), t);
            }
        }
    }

    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        if (this.usingReader) {
            return;
        }
        Charset charset = B2CConverter.getCharset((String)enc);
        this.coyoteRequest.setCharset(charset);
    }

    public ServletContext getServletContext() {
        return this.getContext().getServletContext();
    }

    public AsyncContext startAsync() {
        return this.startAsync((ServletRequest)this.getRequest(), (ServletResponse)this.response.getResponse());
    }

    public AsyncContext startAsync(ServletRequest request, ServletResponse response) {
        if (!this.isAsyncSupported()) {
            IllegalStateException ise = new IllegalStateException(sm.getString("request.asyncNotSupported"));
            log.warn((Object)sm.getString("coyoteRequest.noAsync", new Object[]{StringUtils.join(this.getNonAsyncClassNames())}), (Throwable)ise);
            throw ise;
        }
        if (this.asyncContext == null) {
            this.asyncContext = new AsyncContextImpl(this);
        }
        this.asyncContext.setStarted(this.getContext(), request, response, request == this.getRequest() && response == this.getResponse().getResponse());
        this.asyncContext.setTimeout(this.getConnector().getAsyncTimeout());
        return this.asyncContext;
    }

    private Set<String> getNonAsyncClassNames() {
        FilterChain filterChain;
        HashSet<String> result = new HashSet<String>();
        Wrapper wrapper = this.getWrapper();
        if (!wrapper.isAsyncSupported()) {
            result.add(wrapper.getServletClass());
        }
        if ((filterChain = this.getFilterChain()) instanceof ApplicationFilterChain) {
            ((ApplicationFilterChain)filterChain).findNonAsyncFilters(result);
        } else {
            result.add(sm.getString("coyoteRequest.filterAsyncSupportUnknown"));
        }
        for (Container c = wrapper; c != null; c = c.getParent()) {
            c.getPipeline().findNonAsyncValves(result);
        }
        return result;
    }

    public boolean isAsyncStarted() {
        if (this.asyncContext == null) {
            return false;
        }
        return this.asyncContext.isStarted();
    }

    public boolean isAsyncDispatching() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_DISPATCHING, (Object)result);
        return result.get();
    }

    public boolean isAsyncCompleting() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_COMPLETING, (Object)result);
        return result.get();
    }

    public boolean isAsync() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_ASYNC, (Object)result);
        return result.get();
    }

    public boolean isAsyncSupported() {
        if (this.asyncSupported == null) {
            return true;
        }
        return this.asyncSupported;
    }

    public AsyncContext getAsyncContext() {
        if (!this.isAsyncStarted()) {
            throw new IllegalStateException(sm.getString("request.notAsync"));
        }
        return this.asyncContext;
    }

    public AsyncContextImpl getAsyncContextInternal() {
        return this.asyncContext;
    }

    public DispatcherType getDispatcherType() {
        if (this.internalDispatcherType == null) {
            return DispatcherType.REQUEST;
        }
        return this.internalDispatcherType;
    }

    public void addCookie(Cookie cookie) {
        if (!this.cookiesConverted) {
            this.convertCookies();
        }
        int size = 0;
        if (this.cookies != null) {
            size = this.cookies.length;
        }
        Cookie[] newCookies = new Cookie[size + 1];
        if (this.cookies != null) {
            System.arraycopy(this.cookies, 0, newCookies, 0, size);
        }
        newCookies[size] = cookie;
        this.cookies = newCookies;
    }

    public void addLocale(Locale locale) {
        this.locales.add(locale);
    }

    public void clearCookies() {
        this.cookiesParsed = true;
        this.cookiesConverted = true;
        this.cookies = null;
    }

    public void clearLocales() {
        this.locales.clear();
    }

    public void setAuthType(String type) {
        this.authType = type;
    }

    public void setPathInfo(String path) {
        this.mappingData.pathInfo.setString(path);
    }

    public void setRequestedSessionCookie(boolean flag) {
        this.requestedSessionCookie = flag;
    }

    public void setRequestedSessionId(String id) {
        this.requestedSessionId = id;
    }

    public void setRequestedSessionURL(boolean flag) {
        this.requestedSessionURL = flag;
    }

    public void setRequestedSessionSSL(boolean flag) {
        this.requestedSessionSSL = flag;
    }

    public String getDecodedRequestURI() {
        return this.coyoteRequest.decodedURI().toString();
    }

    public MessageBytes getDecodedRequestURIMB() {
        return this.coyoteRequest.decodedURI();
    }

    public void setUserPrincipal(Principal principal) {
        if (Globals.IS_SECURITY_ENABLED && principal != null) {
            if (this.subject == null) {
                HttpSession session = this.getSession(false);
                if (session == null) {
                    this.subject = this.newSubject(principal);
                } else {
                    this.subject = (Subject)session.getAttribute("javax.security.auth.subject");
                    if (this.subject == null) {
                        this.subject = this.newSubject(principal);
                        session.setAttribute("javax.security.auth.subject", (Object)this.subject);
                    } else {
                        this.subject.getPrincipals().add(principal);
                    }
                }
            } else {
                this.subject.getPrincipals().add(principal);
            }
        }
        this.userPrincipal = principal;
    }

    private Subject newSubject(Principal principal) {
        Subject result = new Subject();
        result.getPrincipals().add(principal);
        return result;
    }

    public boolean isTrailerFieldsReady() {
        return this.coyoteRequest.isTrailerFieldsReady();
    }

    public Map<String, String> getTrailerFields() {
        if (!this.isTrailerFieldsReady()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.trailersNotReady"));
        }
        HashMap<String, String> result = new HashMap<String, String>(this.coyoteRequest.getTrailerFields());
        return result;
    }

    public PushBuilder newPushBuilder() {
        return this.newPushBuilder(this);
    }

    public PushBuilder newPushBuilder(HttpServletRequest request) {
        AtomicBoolean result = new AtomicBoolean();
        this.coyoteRequest.action(ActionCode.IS_PUSH_SUPPORTED, (Object)result);
        if (result.get()) {
            return new ApplicationPushBuilder(this, request);
        }
        return null;
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        HttpUpgradeHandler handler;
        InstanceManager instanceManager = null;
        try {
            if (InternalHttpUpgradeHandler.class.isAssignableFrom(httpUpgradeHandlerClass)) {
                handler = (HttpUpgradeHandler)httpUpgradeHandlerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                instanceManager = this.getContext().getInstanceManager();
                handler = (HttpUpgradeHandler)instanceManager.newInstance(httpUpgradeHandlerClass);
            }
        }
        catch (IllegalArgumentException | ReflectiveOperationException | SecurityException | NamingException e) {
            throw new ServletException((Throwable)e);
        }
        UpgradeToken upgradeToken = new UpgradeToken(handler, (ContextBind)this.getContext(), instanceManager, this.getUpgradeProtocolName(httpUpgradeHandlerClass));
        this.coyoteRequest.action(ActionCode.UPGRADE, (Object)upgradeToken);
        this.response.setStatus(101);
        return (T)handler;
    }

    private String getUpgradeProtocolName(Class<? extends HttpUpgradeHandler> httpUpgradeHandlerClass) {
        List upgradeProtocols;
        String result = this.response.getHeader(HTTP_UPGRADE_HEADER_NAME);
        if (result == null && (upgradeProtocols = Upgrade.parse(this.getHeaders(HTTP_UPGRADE_HEADER_NAME))) != null && upgradeProtocols.size() == 1) {
            result = ((Upgrade)upgradeProtocols.get(0)).toString();
        }
        if (result == null) {
            result = httpUpgradeHandlerClass.getName();
        }
        return result;
    }

    public String getAuthType() {
        return this.authType;
    }

    public String getContextPath() {
        int lastSlash = this.mappingData.contextSlashCount;
        if (lastSlash == 0) {
            return "";
        }
        String canonicalContextPath = this.getServletContext().getContextPath();
        String uri = this.getRequestURI();
        int pos = 0;
        if (!this.getContext().getAllowMultipleLeadingForwardSlashInPath()) {
            while (++pos < uri.length() && uri.charAt(pos) == '/') {
            }
            uri = uri.substring(--pos);
        }
        char[] uriChars = uri.toCharArray();
        while (lastSlash > 0 && (pos = this.nextSlash(uriChars, pos + 1)) != -1) {
            --lastSlash;
        }
        String candidate = pos == -1 ? uri : uri.substring(0, pos);
        candidate = this.removePathParameters(candidate);
        candidate = UDecoder.URLDecode((String)candidate, (Charset)this.connector.getURICharset());
        candidate = org.apache.tomcat.util.http.RequestUtil.normalize((String)candidate);
        boolean match = canonicalContextPath.equals(candidate);
        while (!match && pos != -1) {
            candidate = (pos = this.nextSlash(uriChars, pos + 1)) == -1 ? uri : uri.substring(0, pos);
            candidate = this.removePathParameters(candidate);
            candidate = UDecoder.URLDecode((String)candidate, (Charset)this.connector.getURICharset());
            candidate = org.apache.tomcat.util.http.RequestUtil.normalize((String)candidate);
            match = canonicalContextPath.equals(candidate);
        }
        if (match) {
            if (pos == -1) {
                return uri;
            }
            return uri.substring(0, pos);
        }
        throw new IllegalStateException(sm.getString("coyoteRequest.getContextPath.ise", new Object[]{canonicalContextPath, uri}));
    }

    private String removePathParameters(String input) {
        int nextSlash;
        int nextSemiColon = input.indexOf(59);
        if (nextSemiColon == -1) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length());
        result.append(input.substring(0, nextSemiColon));
        while ((nextSlash = input.indexOf(47, nextSemiColon)) != -1) {
            nextSemiColon = input.indexOf(59, nextSlash);
            if (nextSemiColon == -1) {
                result.append(input.substring(nextSlash));
                break;
            }
            result.append(input.substring(nextSlash, nextSemiColon));
        }
        return result.toString();
    }

    private int nextSlash(char[] uri, int startPos) {
        int len = uri.length;
        for (int pos = startPos; pos < len; ++pos) {
            if (uri[pos] == '/') {
                return pos;
            }
            if (this.connector.getEncodedSolidusHandlingInternal() != EncodedSolidusHandling.DECODE || uri[pos] != '%' || pos + 2 >= len || uri[pos + 1] != '2' || uri[pos + 2] != 'f' && uri[pos + 2] != 'F') continue;
            return pos;
        }
        return -1;
    }

    public Cookie[] getCookies() {
        if (!this.cookiesConverted) {
            this.convertCookies();
        }
        return this.cookies;
    }

    public ServerCookies getServerCookies() {
        this.parseCookies();
        return this.coyoteRequest.getCookies();
    }

    public long getDateHeader(String name) {
        String value = this.getHeader(name);
        if (value == null) {
            return -1L;
        }
        long result = FastHttpDateFormat.parseDate((String)value);
        if (result != -1L) {
            return result;
        }
        throw new IllegalArgumentException(value);
    }

    public String getHeader(String name) {
        return this.coyoteRequest.getHeader(name);
    }

    public Enumeration<String> getHeaders(String name) {
        return this.coyoteRequest.getMimeHeaders().values(name);
    }

    public Enumeration<String> getHeaderNames() {
        return this.coyoteRequest.getMimeHeaders().names();
    }

    public int getIntHeader(String name) {
        String value = this.getHeader(name);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    public HttpServletMapping getHttpServletMapping() {
        return this.applicationMapping.getHttpServletMapping();
    }

    public String getMethod() {
        return this.coyoteRequest.method().toStringType();
    }

    public String getPathInfo() {
        return this.mappingData.pathInfo.toStringType();
    }

    public String getPathTranslated() {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        if (this.getPathInfo() == null) {
            return null;
        }
        return context.getServletContext().getRealPath(this.getPathInfo());
    }

    public String getQueryString() {
        return this.coyoteRequest.queryString().toString();
    }

    public String getRemoteUser() {
        if (this.userPrincipal == null) {
            return null;
        }
        return this.userPrincipal.getName();
    }

    public MessageBytes getRequestPathMB() {
        return this.mappingData.requestPath;
    }

    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    public String getRequestURI() {
        return this.coyoteRequest.requestURI().toString();
    }

    public StringBuffer getRequestURL() {
        return RequestUtil.getRequestURL(this);
    }

    public String getServletPath() {
        return this.mappingData.wrapperPath.toStringType();
    }

    public HttpSession getSession() {
        return this.getSession(true);
    }

    public HttpSession getSession(boolean create) {
        Session session = this.doGetSession(create);
        if (session == null) {
            return null;
        }
        return session.getSession();
    }

    public boolean isRequestedSessionIdFromCookie() {
        if (this.requestedSessionId == null) {
            return false;
        }
        return this.requestedSessionCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        if (this.requestedSessionId == null) {
            return false;
        }
        return this.requestedSessionURL;
    }

    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return this.isRequestedSessionIdFromURL();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isRequestedSessionIdValid() {
        if (this.requestedSessionId == null) {
            return false;
        }
        Context context = this.getContext();
        if (context == null) {
            return false;
        }
        ClassLoader originalClassLoader = context.bind(Globals.IS_SECURITY_ENABLED, null);
        try {
            Manager manager = context.getManager();
            if (manager == null) {
                boolean bl = false;
                return bl;
            }
            Session session = null;
            try {
                session = manager.findSession(this.requestedSessionId);
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (session == null || !session.isValid()) {
                if (this.getMappingData().contexts == null) {
                    boolean bl = false;
                    return bl;
                }
                for (int i = this.getMappingData().contexts.length; i > 0; --i) {
                    Context ctxt = this.getMappingData().contexts[i - 1];
                    try {
                        if (ctxt.getManager().findSession(this.requestedSessionId) == null) continue;
                        boolean bl = true;
                        return bl;
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                boolean bl = false;
                return bl;
            }
            boolean bl = true;
            return bl;
        }
        finally {
            context.unbind(Globals.IS_SECURITY_ENABLED, originalClassLoader);
        }
    }

    public boolean isUserInRole(String role) {
        if (this.userPrincipal == null) {
            return false;
        }
        Context context = this.getContext();
        if (context == null) {
            return false;
        }
        if ("*".equals(role)) {
            return false;
        }
        if ("**".equals(role) && !context.findSecurityRole("**")) {
            return this.userPrincipal != null;
        }
        Realm realm = context.getRealm();
        if (realm == null) {
            return false;
        }
        return realm.hasRole(this.getWrapper(), this.userPrincipal, role);
    }

    public Principal getPrincipal() {
        return this.userPrincipal;
    }

    public Principal getUserPrincipal() {
        if (this.userPrincipal instanceof TomcatPrincipal) {
            GSSCredential gssCredential = ((TomcatPrincipal)this.userPrincipal).getGssCredential();
            if (gssCredential != null) {
                int left = -1;
                try {
                    left = gssCredential.getRemainingLifetime();
                }
                catch (IllegalStateException | GSSException e) {
                    log.warn((Object)sm.getString("coyoteRequest.gssLifetimeFail", new Object[]{this.userPrincipal.getName()}), (Throwable)e);
                }
                if (left <= 0) {
                    try {
                        this.logout();
                    }
                    catch (ServletException servletException) {
                        // empty catch block
                    }
                    return null;
                }
            }
            return ((TomcatPrincipal)this.userPrincipal).getUserPrincipal();
        }
        return this.userPrincipal;
    }

    public Session getSessionInternal() {
        return this.doGetSession(true);
    }

    public void changeSessionId(String newSessionId) {
        Context context;
        if (this.requestedSessionId != null && this.requestedSessionId.length() > 0) {
            this.requestedSessionId = newSessionId;
        }
        if ((context = this.getContext()) != null && !context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) {
            return;
        }
        if (this.response != null) {
            Cookie newCookie = ApplicationSessionCookieConfig.createSessionCookie(context, newSessionId, this.isSecure());
            this.response.addSessionCookieInternal(newCookie);
        }
    }

    public String changeSessionId() {
        Session session = this.getSessionInternal(false);
        if (session == null) {
            throw new IllegalStateException(sm.getString("coyoteRequest.changeSessionId"));
        }
        Manager manager = this.getContext().getManager();
        String newSessionId = manager.rotateSessionId(session);
        this.changeSessionId(newSessionId);
        return newSessionId;
    }

    public Session getSessionInternal(boolean create) {
        return this.doGetSession(create);
    }

    public boolean isParametersParsed() {
        return this.parametersParsed;
    }

    public boolean isFinished() {
        return this.coyoteRequest.isFinished();
    }

    protected void checkSwallowInput() {
        Context context = this.getContext();
        if (context != null && !context.getSwallowAbortedUploads()) {
            this.coyoteRequest.action(ActionCode.DISABLE_SWALLOW_INPUT, null);
        }
    }

    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        if (response.isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.authenticate.ise"));
        }
        return this.getContext().getAuthenticator().authenticate(this, response);
    }

    public void login(String username, String password) throws ServletException {
        if (this.getAuthType() != null || this.getRemoteUser() != null || this.getUserPrincipal() != null) {
            throw new ServletException(sm.getString("coyoteRequest.alreadyAuthenticated"));
        }
        this.getContext().getAuthenticator().login(username, password, this);
    }

    public void logout() throws ServletException {
        this.getContext().getAuthenticator().logout(this);
    }

    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        this.parseParts(true);
        if (this.partsParseException != null) {
            if (this.partsParseException instanceof IOException) {
                throw (IOException)this.partsParseException;
            }
            if (this.partsParseException instanceof IllegalStateException) {
                throw (IllegalStateException)this.partsParseException;
            }
            if (this.partsParseException instanceof ServletException) {
                throw (ServletException)((Object)this.partsParseException);
            }
        }
        return this.parts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void parseParts(boolean explicit) {
        if (this.parts != null || this.partsParseException != null) {
            return;
        }
        Context context = this.getContext();
        MultipartConfigElement mce = this.getWrapper().getMultipartConfigElement();
        if (mce == null) {
            if (context.getAllowCasualMultipartParsing()) {
                mce = new MultipartConfigElement(null, (long)this.connector.getMaxPostSize(), (long)this.connector.getMaxPostSize(), this.connector.getMaxPostSize());
            } else {
                if (explicit) {
                    this.partsParseException = new IllegalStateException(sm.getString("coyoteRequest.noMultipartConfig"));
                    return;
                }
                this.parts = Collections.emptyList();
                return;
            }
        }
        int maxParameterCount = this.getConnector().getMaxParameterCount();
        Parameters parameters = this.coyoteRequest.getParameters();
        parameters.setLimit(maxParameterCount);
        boolean success = false;
        try {
            File location;
            String locationStr = mce.getLocation();
            if (locationStr == null || locationStr.length() == 0) {
                location = (File)context.getServletContext().getAttribute("javax.servlet.context.tempdir");
            } else {
                location = new File(locationStr);
                if (!location.isAbsolute()) {
                    location = new File((File)context.getServletContext().getAttribute("javax.servlet.context.tempdir"), locationStr).getAbsoluteFile();
                }
            }
            if (!location.exists() && context.getCreateUploadTargets()) {
                log.warn((Object)sm.getString("coyoteRequest.uploadCreate", new Object[]{location.getAbsolutePath(), this.getMappingData().wrapper.getName()}));
                if (!location.mkdirs()) {
                    log.warn((Object)sm.getString("coyoteRequest.uploadCreateFail", new Object[]{location.getAbsolutePath()}));
                }
            }
            if (!location.isDirectory()) {
                parameters.setParseFailedReason(Parameters.FailReason.MULTIPART_CONFIG_INVALID);
                this.partsParseException = new IOException(sm.getString("coyoteRequest.uploadLocationInvalid", new Object[]{location}));
                return;
            }
            DiskFileItemFactory factory = new DiskFileItemFactory();
            try {
                factory.setRepository(location.getCanonicalFile());
            }
            catch (IOException ioe) {
                parameters.setParseFailedReason(Parameters.FailReason.IO_ERROR);
                this.partsParseException = ioe;
                if (this.partsParseException != null || !success) {
                    parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                }
                return;
            }
            factory.setSizeThreshold(mce.getFileSizeThreshold());
            ServletFileUpload upload = new ServletFileUpload();
            upload.setFileItemFactory((FileItemFactory)factory);
            upload.setFileSizeMax(mce.getMaxFileSize());
            upload.setSizeMax(mce.getMaxRequestSize());
            if (maxParameterCount > -1) {
                upload.setFileCountMax((long)(maxParameterCount - parameters.size()));
            }
            this.parts = new ArrayList<Part>();
            try {
                List items = upload.parseRequest((RequestContext)new ServletRequestContext((HttpServletRequest)this));
                int maxPostSize = this.getConnector().getMaxPostSize();
                int postSize = 0;
                Charset charset = this.getCharset();
                for (FileItem item : items) {
                    ApplicationPart part = new ApplicationPart(item, location);
                    this.parts.add(part);
                    if (part.getSubmittedFileName() != null) continue;
                    String name = part.getName();
                    if (maxPostSize >= 0) {
                        postSize += name.getBytes(charset).length;
                        ++postSize;
                        postSize = (int)((long)postSize + part.getSize());
                        if (++postSize > maxPostSize) {
                            parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                            throw new IllegalStateException(sm.getString("coyoteRequest.maxPostSizeExceeded"));
                        }
                    }
                    String value = null;
                    try {
                        value = part.getString(charset.name());
                    }
                    catch (UnsupportedEncodingException unsupportedEncodingException) {
                        // empty catch block
                    }
                    parameters.addParameter(name, value);
                }
                success = true;
            }
            catch (InvalidContentTypeException e) {
                parameters.setParseFailedReason(Parameters.FailReason.INVALID_CONTENT_TYPE);
                this.partsParseException = new ServletException((Throwable)e);
            }
            catch (SizeException e) {
                parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                this.checkSwallowInput();
                this.partsParseException = new IllegalStateException(e);
            }
            catch (IOException e) {
                parameters.setParseFailedReason(Parameters.FailReason.IO_ERROR);
                this.partsParseException = e;
            }
            catch (IllegalStateException e) {
                this.checkSwallowInput();
                this.partsParseException = e;
            }
        }
        finally {
            if (this.partsParseException != null || !success) {
                parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
            }
        }
    }

    public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
        for (Part part : this.getParts()) {
            if (!name.equals(part.getName())) continue;
            return part;
        }
        return null;
    }

    protected Session doGetSession(boolean create) {
        Context context = this.getContext();
        if (context == null) {
            return null;
        }
        if (this.session != null && !this.session.isValid()) {
            this.session = null;
        }
        if (this.session != null) {
            return this.session;
        }
        Manager manager = context.getManager();
        if (manager == null) {
            return null;
        }
        if (this.requestedSessionId != null) {
            try {
                this.session = manager.findSession(this.requestedSessionId);
            }
            catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("request.session.failed", new Object[]{this.requestedSessionId, e.getMessage()}), (Throwable)e);
                } else {
                    log.info((Object)sm.getString("request.session.failed", new Object[]{this.requestedSessionId, e.getMessage()}));
                }
                this.session = null;
            }
            if (this.session != null && !this.session.isValid()) {
                this.session = null;
            }
            if (this.session != null) {
                this.session.access();
                return this.session;
            }
        }
        if (!create) {
            return null;
        }
        boolean trackModesIncludesCookie = context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE);
        if (trackModesIncludesCookie && this.response.getResponse().isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.sessionCreateCommitted"));
        }
        String sessionId = this.getRequestedSessionId();
        if (!this.requestedSessionSSL) {
            if ("/".equals(context.getSessionCookiePath()) && this.isRequestedSessionIdFromCookie()) {
                if (context.getValidateClientProvidedNewSessionId()) {
                    boolean found = false;
                    for (Container container : this.getHost().findChildren()) {
                        Manager m = ((Context)container).getManager();
                        if (m == null) continue;
                        try {
                            if (m.findSession(sessionId) == null) continue;
                            found = true;
                            break;
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }
                    if (!found) {
                        sessionId = null;
                    }
                }
            } else {
                sessionId = null;
            }
        }
        this.session = manager.createSession(sessionId);
        if (this.session != null && trackModesIncludesCookie) {
            Cookie cookie = ApplicationSessionCookieConfig.createSessionCookie(context, this.session.getIdInternal(), this.isSecure());
            this.response.addSessionCookieInternal(cookie);
        }
        if (this.session == null) {
            return null;
        }
        this.session.access();
        return this.session;
    }

    protected String unescape(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(92) == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c != '\\') {
                buf.append(c);
                continue;
            }
            if (++i >= s.length()) {
                throw new IllegalArgumentException();
            }
            c = s.charAt(i);
            buf.append(c);
        }
        return buf.toString();
    }

    private CookieProcessor getCookieProcessor() {
        Context context = this.getContext();
        if (context == null) {
            return new Rfc6265CookieProcessor();
        }
        return context.getCookieProcessor();
    }

    protected void parseCookies() {
        if (this.cookiesParsed) {
            return;
        }
        this.cookiesParsed = true;
        ServerCookies serverCookies = this.coyoteRequest.getCookies();
        serverCookies.setLimit(this.connector.getMaxCookieCount());
        this.getCookieProcessor().parseCookieHeader(this.coyoteRequest.getMimeHeaders(), serverCookies);
    }

    protected void convertCookies() {
        if (this.cookiesConverted) {
            return;
        }
        this.cookiesConverted = true;
        this.parseCookies();
        ServerCookies serverCookies = this.coyoteRequest.getCookies();
        int count = serverCookies.getCookieCount();
        if (count <= 0) {
            return;
        }
        this.cookies = new Cookie[count];
        int idx = 0;
        for (int i = 0; i < count; ++i) {
            ServerCookie scookie = serverCookies.getCookie(i);
            try {
                Cookie cookie = new Cookie(scookie.getName().toString(), null);
                int version = scookie.getVersion();
                cookie.setVersion(version);
                scookie.getValue().getByteChunk().setCharset(this.getCookieProcessor().getCharset());
                cookie.setValue(this.unescape(scookie.getValue().toString()));
                cookie.setPath(this.unescape(scookie.getPath().toString()));
                String domain = scookie.getDomain().toString();
                if (domain != null) {
                    cookie.setDomain(this.unescape(domain));
                }
                String comment = scookie.getComment().toString();
                cookie.setComment(version == 1 ? this.unescape(comment) : null);
                this.cookies[idx++] = cookie;
                continue;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        if (idx < count) {
            Cookie[] ncookies = new Cookie[idx];
            System.arraycopy(this.cookies, 0, ncookies, 0, idx);
            this.cookies = ncookies;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    protected void parseParameters() {
        block39: {
            this.parametersParsed = true;
            Parameters parameters = this.coyoteRequest.getParameters();
            boolean success = false;
            try {
                int semicolon;
                int maxParameterCount = this.getConnector().getMaxParameterCount();
                if (this.parts != null && maxParameterCount > 0) {
                    maxParameterCount -= this.parts.size();
                }
                parameters.setLimit(maxParameterCount);
                Charset charset = this.getCharset();
                boolean useBodyEncodingForURI = this.connector.getUseBodyEncodingForURI();
                parameters.setCharset(charset);
                if (useBodyEncodingForURI) {
                    parameters.setQueryStringCharset(charset);
                }
                parameters.handleQueryParameters();
                if (this.usingInputStream || this.usingReader) {
                    success = true;
                    return;
                }
                String contentType = this.getContentType();
                if (contentType == null) {
                    contentType = "";
                }
                if ("multipart/form-data".equals(contentType = (semicolon = contentType.indexOf(59)) >= 0 ? contentType.substring(0, semicolon).trim() : contentType.trim())) {
                    this.parseParts(false);
                    success = true;
                    return;
                }
                if (!this.getConnector().isParseBodyMethod(this.getMethod())) {
                    success = true;
                    return;
                }
                if (!"application/x-www-form-urlencoded".equals(contentType)) {
                    success = true;
                    return;
                }
                int len = this.getContentLength();
                if (len > 0) {
                    int maxPostSize = this.connector.getMaxPostSize();
                    if (maxPostSize >= 0 && len > maxPostSize) {
                        Context context = this.getContext();
                        if (context != null && context.getLogger().isDebugEnabled()) {
                            context.getLogger().debug((Object)sm.getString("coyoteRequest.postTooLarge"));
                        }
                        this.checkSwallowInput();
                        parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                        return;
                    }
                    byte[] formData = null;
                    if (len < 8192) {
                        if (this.postData == null) {
                            this.postData = new byte[8192];
                        }
                        formData = this.postData;
                    } else {
                        formData = new byte[len];
                    }
                    try {
                        this.readPostBodyFully(formData, len);
                    }
                    catch (IOException e) {
                        Context context = this.getContext();
                        if (context != null && context.getLogger().isDebugEnabled()) {
                            context.getLogger().debug((Object)sm.getString("coyoteRequest.parseParameters"), (Throwable)e);
                        }
                        parameters.setParseFailedReason(Parameters.FailReason.CLIENT_DISCONNECT);
                        if (!success) {
                            parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                        }
                        return;
                    }
                    parameters.processParameters(formData, 0, len);
                } else if ("chunked".equalsIgnoreCase(this.coyoteRequest.getHeader("transfer-encoding"))) {
                    byte[] formData = null;
                    try {
                        formData = this.readChunkedPostBody();
                    }
                    catch (IllegalStateException ise) {
                        parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                        Context context = this.getContext();
                        if (context != null && context.getLogger().isDebugEnabled()) {
                            context.getLogger().debug((Object)sm.getString("coyoteRequest.parseParameters"), (Throwable)ise);
                        }
                        if (!success) {
                            parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                        }
                        return;
                    }
                    catch (IOException e) {
                        block38: {
                            parameters.setParseFailedReason(Parameters.FailReason.CLIENT_DISCONNECT);
                            Context context = this.getContext();
                            if (context != null && context.getLogger().isDebugEnabled()) {
                                context.getLogger().debug((Object)sm.getString("coyoteRequest.parseParameters"), (Throwable)e);
                            }
                            if (success) break block38;
                            parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                        }
                        return;
                    }
                    if (formData != null) {
                        parameters.processParameters(formData, 0, formData.length);
                    }
                }
                success = true;
                break block39;
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                if (!success) {
                    parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                }
            }
        }
    }

    @Deprecated
    protected int readPostBody(byte[] body, int len) throws IOException {
        int inputLen;
        int offset = 0;
        do {
            if ((inputLen = this.getStream().read(body, offset, len - offset)) > 0) continue;
            return offset;
        } while (len - (offset += inputLen) > 0);
        return len;
    }

    protected void readPostBodyFully(byte[] body, int len) throws IOException {
        int inputLen;
        int offset = 0;
        do {
            if ((inputLen = this.getStream().read(body, offset, len - offset)) > 0) continue;
            throw new EOFException();
        } while (len - (offset += inputLen) > 0);
    }

    protected byte[] readChunkedPostBody() throws IOException {
        ByteChunk body = new ByteChunk();
        byte[] buffer = new byte[8192];
        int len = 0;
        while (len > -1) {
            len = this.getStream().read(buffer, 0, 8192);
            if (this.connector.getMaxPostSize() >= 0 && body.getLength() + len > this.connector.getMaxPostSize()) {
                this.checkSwallowInput();
                throw new IllegalStateException(sm.getString("coyoteRequest.chunkedPostTooLarge"));
            }
            if (len <= 0) continue;
            body.append(buffer, 0, len);
        }
        if (body.getLength() == 0) {
            return null;
        }
        if (body.getLength() < body.getBuffer().length) {
            int length = body.getLength();
            byte[] result = new byte[length];
            System.arraycopy(body.getBuffer(), 0, result, 0, length);
            return result;
        }
        return body.getBuffer();
    }

    protected void parseLocales() {
        this.localesParsed = true;
        TreeMap<Double, ArrayList<Locale>> locales = new TreeMap<Double, ArrayList<Locale>>();
        Enumeration<String> values = this.getHeaders("accept-language");
        while (values.hasMoreElements()) {
            String value = values.nextElement();
            this.parseLocalesHeader(value, locales);
        }
        for (ArrayList list : locales.values()) {
            for (Locale locale : list) {
                this.addLocale(locale);
            }
        }
    }

    protected void parseLocalesHeader(String value, TreeMap<Double, ArrayList<Locale>> locales) {
        List acceptLanguages;
        try {
            acceptLanguages = AcceptLanguage.parse((StringReader)new StringReader(value));
        }
        catch (IOException e) {
            return;
        }
        for (AcceptLanguage acceptLanguage : acceptLanguages) {
            Double key = -acceptLanguage.getQuality();
            locales.computeIfAbsent(key, k -> new ArrayList()).add(acceptLanguage.getLocale());
        }
    }

    static {
        specialAttributes.put("org.apache.catalina.core.DISPATCHER_TYPE", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.internalDispatcherType == null ? DispatcherType.REQUEST : request.internalDispatcherType;
            }

            @Override
            public void set(Request request, String name, Object value) {
                request.internalDispatcherType = (DispatcherType)value;
            }
        });
        specialAttributes.put("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.requestDispatcherPath == null ? request.getRequestPathMB().toString() : request.requestDispatcherPath.toString();
            }

            @Override
            public void set(Request request, String name, Object value) {
                request.requestDispatcherPath = value;
            }
        });
        specialAttributes.put("org.apache.catalina.ASYNC_SUPPORTED", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.asyncSupported;
            }

            @Override
            public void set(Request request, String name, Object value) {
                Boolean oldValue = request.asyncSupported;
                request.asyncSupported = (Boolean)value;
                request.notifyAttributeAssigned(name, value, oldValue);
            }
        });
        specialAttributes.put("org.apache.catalina.realm.GSS_CREDENTIAL", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                if (request.userPrincipal instanceof TomcatPrincipal) {
                    return ((TomcatPrincipal)request.userPrincipal).getGssCredential();
                }
                return null;
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.catalina.parameter_parse_failed", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                if (request.getCoyoteRequest().getParameters().isParseFailed()) {
                    return Boolean.TRUE;
                }
                return null;
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.catalina.parameter_parse_failed_reason", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.getCoyoteRequest().getParameters().getParseFailedReason();
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.tomcat.sendfile.support", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.getConnector().getProtocolHandler().isSendfileSupported() && request.getCoyoteRequest().getSendfile();
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.coyote.connectionID", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                AtomicReference result = new AtomicReference();
                request.getCoyoteRequest().action(ActionCode.CONNECTION_ID, result);
                return result.get();
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.catalina.filters.RemoteIpFilter.secure", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                return request.isSecure();
            }

            @Override
            public void set(Request request, String name, Object value) {
                if (value instanceof Boolean) {
                    request.setSecure((Boolean)value);
                }
            }
        });
        specialAttributes.put("org.apache.coyote.streamID", new SpecialAttributeAdapter(){

            @Override
            public Object get(Request request, String name) {
                AtomicReference result = new AtomicReference();
                request.getCoyoteRequest().action(ActionCode.STREAM_ID, result);
                return result.get();
            }

            @Override
            public void set(Request request, String name, Object value) {
            }
        });
        for (SimpleDateFormat sdf : formatsTemplate) {
            sdf.setTimeZone(GMT_ZONE);
        }
    }

    private static interface SpecialAttributeAdapter {
        public Object get(Request var1, String var2);

        public void set(Request var1, String var2, Object var3);
    }
}

